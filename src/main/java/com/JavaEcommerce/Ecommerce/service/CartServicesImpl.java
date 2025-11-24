package com.JavaEcommerce.Ecommerce.service;

import com.JavaEcommerce.Ecommerce.exception.ApiException;
import com.JavaEcommerce.Ecommerce.exception.ResourceNotFoundException;
import com.JavaEcommerce.Ecommerce.model.Cart;
import com.JavaEcommerce.Ecommerce.model.CartsItem;
import com.JavaEcommerce.Ecommerce.model.Product;
import com.JavaEcommerce.Ecommerce.payload.CartDto;
import com.JavaEcommerce.Ecommerce.payload.ProductDto;
import com.JavaEcommerce.Ecommerce.repo.CartItemRepo;
import com.JavaEcommerce.Ecommerce.repo.CartRepo;
import com.JavaEcommerce.Ecommerce.util.AuthUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CartServicesImpl implements CartServices{

    private static final Logger logger = LoggerFactory.getLogger(CartServicesImpl.class);

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private com.JavaEcommerce.Ecommerce.repo.ProductRepo productRepo;

    @Autowired
    AuthUtils authUtil;

    @Autowired
    CartItemRepo cartItemRepo;

    @Autowired
    ModelMapper modelMapper;

    // ...existing code...

    @Override
    public CartDto addProductsToCart(Long productId, Integer quantity) {
        try {
            // Validate inputs
            if (productId == null || productId <= 0) {
                logger.warn("Invalid productId: {}", productId);
                throw new ApiException("ProductId must be a positive number");
            }

            if (quantity == null || quantity <= 0) {
                logger.warn("Invalid quantity: {}", quantity);
                throw new ApiException("Quantity must be a positive number");
            }

            logger.info("Adding product to cart - ProductId: {}, Quantity: {}", productId, quantity);

            Cart userCart = createCart();
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));

            logger.debug("Product found: {}", product.getProductName());

            // check stock
            if (product.getQuantity() < quantity) {
                logger.warn("Insufficient stock for product: {}. Available: {}, Requested: {}",
                    product.getProductName(), product.getQuantity(), quantity);
                throw new ApiException(product.getProductName() + " stock is not available");
            }

            // find existing cart item for this cart and product
            CartsItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(userCart.getCartId(), productId);

            if (cartItem != null) {
                logger.debug("Cart item already exists, updating quantity");
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItemRepo.save(cartItem);

                // update cart total
                userCart.setTotalPrice(userCart.getTotalPrice() + (product.getSpecialPrice() * quantity));
                cartRepo.save(userCart);
                logger.info("Cart item updated successfully");

            } else {
                logger.debug("Creating new cart item");
                CartsItem newCartItem = new CartsItem();
                newCartItem.setProduct(product);
                newCartItem.setQuantity(quantity);
                newCartItem.setCart(userCart);
                // set the foreign-key fields so JPA derived query can work (these fields are non-insertable on relations)
                newCartItem.setCartId(userCart.getCartId());
                newCartItem.setProductId(product.getProductId());
                cartItemRepo.save(newCartItem);

                // decrement product stock and save
                product.setQuantity(product.getQuantity() - quantity);
                productRepo.save(product);

                // update cart total and save
                userCart.setTotalPrice(userCart.getTotalPrice() + (product.getSpecialPrice() * quantity));
                cartRepo.save(userCart);
                logger.info("New cart item created and product stock decremented successfully");
            }

            // prepare and return DTO
            CartDto cartDto = modelMapper.map(userCart, CartDto.class);
            List<CartsItem> cartItems = userCart.getCartItems();

            List<ProductDto> productDtos = cartItems.stream().map((cartItem1) -> {
                ProductDto productDto = modelMapper.map(cartItem1.getProduct(), ProductDto.class);
                productDto.setQuantity(cartItem1.getQuantity());
                return productDto;
            }).toList();
            cartDto.setProducts(productDtos);

            logger.info("Product successfully added to cart. Total cart items: {}", productDtos.size());
            return cartDto;

        } catch (ResourceNotFoundException | ApiException e) {
            logger.error("Error adding product to cart: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while adding product to cart", e);
            throw new ApiException("Error adding product to cart: " + e.getMessage());
        }
    }

    private Cart createCart() {
        try {
            logger.debug("Creating or retrieving cart for user");

            // find existing cart by user's email (CartRepo updated to findByUserUserEmail)
            Cart userCart = cartRepo.findByUserUserEmail(authUtil.loggedInUserEmail());
            if (userCart != null) {
                logger.debug("Existing cart found for user email: {}", authUtil.loggedInUserEmail());
                return userCart;
            }

            logger.debug("No existing cart found, creating new cart for user");
            Cart newCart = new Cart();
            // initialize fields
            newCart.setTotalPrice(0.0);
            // set user relation: find user by email is not done here; Cart.user is a relation so typically you'd set user entity.
            // To keep this change minimal and compilable we will not set the user field here. If desired, wire UserRepository and set the relation.
            Cart savedCart = cartRepo.save(newCart);
            logger.info("New cart created successfully with CartId: {}", savedCart.getCartId());
            return savedCart;

        } catch (Exception e) {
            logger.error("Error creating or retrieving cart", e);
            throw new ApiException("Error creating cart: " + e.getMessage());
        }
    }
}
