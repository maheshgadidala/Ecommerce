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
import com.JavaEcommerce.Ecommerce.response.CartResponse;
import com.JavaEcommerce.Ecommerce.util.AuthUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public CartResponse addProductsToCart(Long productId, Integer quantity) {
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

            Double priceAdded = product.getSpecialPrice() * quantity;

            if (cartItem != null) {
                logger.debug("Cart item already exists, updating quantity");
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItemRepo.save(cartItem);

                // update cart total
                userCart.setTotalPrice(userCart.getTotalPrice() + priceAdded);
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
                userCart.setTotalPrice(userCart.getTotalPrice() + priceAdded);
                cartRepo.save(userCart);
                logger.info("New cart item created and product stock decremented successfully");
            }

            // prepare CartDto
            CartDto cartDto = modelMapper.map(userCart, CartDto.class);
            List<CartsItem> cartItems = userCart.getCartItems();

            List<ProductDto> productDtos = cartItems.stream().map((cartItem1) -> {
                ProductDto productDto = modelMapper.map(cartItem1.getProduct(), ProductDto.class);
                productDto.setQuantity(cartItem1.getQuantity());
                return productDto;
            }).toList();
            cartDto.setProducts(productDtos);

            // prepare added product DTO
            ProductDto addedProductDto = modelMapper.map(product, ProductDto.class);
            addedProductDto.setQuantity(quantity);

            logger.info("Product successfully added to cart. Total cart items: {}", productDtos.size());
            logger.info("Cart Response - CartId: {}, TotalPrice: {}, Items Count: {}",
                cartDto.getCartId(), cartDto.getTotalPrice(), productDtos.size());
            for (ProductDto dto : productDtos) {
                logger.info("  Product: {} (ID: {}), Qty: {}, Price: {}, SpecialPrice: {}, Discount: {}%",
                    dto.getProductName(), dto.getProductId(), dto.getQuantity(),
                    dto.getPrice(), dto.getSpecialPrice(), dto.getDiscount());
            }

            // Build and return enhanced response
            CartResponse response = CartResponse.builder()
                    .success(true)
                    .message("Product added to cart successfully")
                    .timestamp(LocalDateTime.now())
                    .cart(cartDto)
                    .addedProduct(addedProductDto)
                    .quantityAdded(quantity)
                    .priceAdded(priceAdded)
                    .status("201 CREATED")
                    .build();

            logger.info("Returning CartResponse with enriched product data");
            return response;

        } catch (ResourceNotFoundException | ApiException e) {
            logger.error("Error adding product to cart: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while adding product to cart", e);
            throw new ApiException("Error adding product to cart: " + e.getMessage());
        }
    }

    @Override
    public List<CartDto> getAllCarts() {
        return List.of();
    }

    @Override
    public CartResponse updateProductQuantityInCart(Long productId, Integer quantity) {
        try {
            logger.info("Updating product quantity in cart - ProductId: {}, NewQuantity: {}", productId, quantity);

            // Validate inputs
            if (productId == null || productId <= 0) {
                throw new ApiException("ProductId must be a positive number");
            }
            if (quantity == null || quantity <= 0) {
                throw new ApiException("Quantity must be a positive number");
            }

            // Get user's cart
            Cart userCart = createCart();
            logger.debug("Cart found with ID: {}", userCart.getCartId());

            // Get product
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

            // Check stock
            if (product.getQuantity() < quantity) {
                logger.warn("Insufficient stock. Available: {}, Requested: {}", product.getQuantity(), quantity);
                throw new ApiException("Insufficient stock for product: " + product.getProductName());
            }

            // Find existing cart item
            CartsItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(userCart.getCartId(), productId);
            if (cartItem == null) {
                throw new ResourceNotFoundException("Cart Item", "productId", productId);
            }

            // Calculate price difference
            Double oldPrice = cartItem.getQuantity() * product.getSpecialPrice();
            Double newPrice = quantity * product.getSpecialPrice();
            Double priceDifference = newPrice - oldPrice;

            // Update quantity
            cartItem.setQuantity(quantity);
            cartItemRepo.save(cartItem);
            logger.debug("Cart item quantity updated");

            // Update cart total
            userCart.setTotalPrice(userCart.getTotalPrice() + priceDifference);
            cartRepo.save(userCart);
            logger.info("Cart total updated");

            // Prepare response
            CartDto cartDto = modelMapper.map(userCart, CartDto.class);
            List<CartsItem> cartItems = userCart.getCartItems();
            List<ProductDto> productDtos = cartItems.stream().map((item) -> {
                ProductDto productDto = modelMapper.map(item.getProduct(), ProductDto.class);
                productDto.setQuantity(item.getQuantity());
                return productDto;
            }).toList();
            cartDto.setProducts(productDtos);

            return CartResponse.builder()
                    .success(true)
                    .message("Product quantity updated successfully")
                    .timestamp(LocalDateTime.now())
                    .cart(cartDto)
                    .quantityAdded(quantity)
                    .priceAdded(newPrice)
                    .status("200 OK")
                    .build();

        } catch (ResourceNotFoundException | ApiException e) {
            logger.error("Error updating product quantity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while updating product quantity", e);
            throw new ApiException("Error updating product quantity: " + e.getMessage());
        }
    }

    @Override
    public CartResponse deleteProductFromCart(Long productId) {
        try {
            logger.info("Deleting product from cart - ProductId: {}", productId);

            if (productId == null || productId <= 0) {
                throw new ApiException("ProductId must be a positive number");
            }

            // Get user's cart
            Cart userCart = createCart();
            logger.debug("Cart found with ID: {}", userCart.getCartId());

            // Find cart item
            CartsItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(userCart.getCartId(), productId);
            if (cartItem == null) {
                throw new ResourceNotFoundException("Cart Item", "productId", productId);
            }

            // Get product
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

            // Calculate removed price
            Double removedPrice = cartItem.getQuantity() * product.getSpecialPrice();

            // Delete cart item
            cartItemRepo.delete(cartItem);
            logger.debug("Cart item deleted");

            // Update cart total
            userCart.setTotalPrice(userCart.getTotalPrice() - removedPrice);
            cartRepo.save(userCart);
            logger.info("Cart total updated after deletion");

            // Prepare response
            CartDto cartDto = modelMapper.map(userCart, CartDto.class);
            List<CartsItem> cartItems = userCart.getCartItems();
            List<ProductDto> productDtos = cartItems.stream().map((item) -> {
                ProductDto productDto = modelMapper.map(item.getProduct(), ProductDto.class);
                productDto.setQuantity(item.getQuantity());
                return productDto;
            }).toList();
            cartDto.setProducts(productDtos);

            return CartResponse.builder()
                    .success(true)
                    .message("Product deleted from cart successfully")
                    .timestamp(LocalDateTime.now())
                    .cart(cartDto)
                    .priceAdded(removedPrice)
                    .status("200 OK")
                    .build();

        } catch (ResourceNotFoundException | ApiException e) {
            logger.error("Error deleting product from cart: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while deleting product", e);
            throw new ApiException("Error deleting product: " + e.getMessage());
        }
    }

    @Override
    public CartResponse getCartDetails() {
        try {
            logger.info("Fetching cart details for user");

            Cart userCart = createCart();
            logger.debug("Cart found with ID: {}", userCart.getCartId());

            CartDto cartDto = modelMapper.map(userCart, CartDto.class);
            List<CartsItem> cartItems = userCart.getCartItems();
            List<ProductDto> productDtos = cartItems.stream().map((item) -> {
                ProductDto productDto = modelMapper.map(item.getProduct(), ProductDto.class);
                productDto.setQuantity(item.getQuantity());
                return productDto;
            }).toList();
            cartDto.setProducts(productDtos);

            return CartResponse.builder()
                    .success(true)
                    .message("Cart details retrieved successfully")
                    .timestamp(LocalDateTime.now())
                    .cart(cartDto)
                    .status("200 OK")
                    .build();

        } catch (Exception e) {
            logger.error("Error fetching cart details: {}", e.getMessage());
            throw new ApiException("Error fetching cart details: " + e.getMessage());
        }
    }

    @Override
    public CartResponse clearCart() {
        try {
            logger.info("Clearing cart for user");

            Cart userCart = createCart();
            logger.debug("Cart found with ID: {}", userCart.getCartId());

            // Delete all cart items
            for (CartsItem cartItem : new java.util.ArrayList<>(userCart.getCartItems())) {
                cartItemRepo.delete(cartItem);
            }
            logger.debug("All cart items deleted");

            // Reset cart total
            userCart.setTotalPrice(0.0);
            cartRepo.save(userCart);
            logger.info("Cart cleared successfully");

            CartDto cartDto = modelMapper.map(userCart, CartDto.class);
            cartDto.setProducts(List.of());

            return CartResponse.builder()
                    .success(true)
                    .message("Cart cleared successfully")
                    .timestamp(LocalDateTime.now())
                    .cart(cartDto)
                    .status("200 OK")
                    .build();

        } catch (Exception e) {
            logger.error("Error clearing cart: {}", e.getMessage());
            throw new ApiException("Error clearing cart: " + e.getMessage());
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
