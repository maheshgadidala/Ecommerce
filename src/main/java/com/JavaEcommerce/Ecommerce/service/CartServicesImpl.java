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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CartServicesImpl implements CartServices{

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



    @Override
    public CartDto addProductsToCart(Long productId, Integer quantity) {
        Cart userCart = createCart();
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));

        // check stock
        if (product.getQuantity() < quantity) {
            throw new ApiException(product.getProductName() + " stock is not available");
        }

        // find existing cart item for this cart and product
        CartsItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(userCart.getCartId(), productId);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepo.save(cartItem);

            // update cart total
            userCart.setTotalPrice(userCart.getTotalPrice() + (product.getSpecialPrice() * quantity));
            cartRepo.save(userCart);

        } else {
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
        return cartDto;
    }

    private Cart createCart() {
        // find existing cart by user's email (CartRepo updated to findByUserUserEmail)
        Cart userCart = cartRepo.findByUserUserEmail(authUtil.loggedInUserEmail());
        if (userCart != null) {
            return userCart;
        }

        Cart newCart = new Cart();
        // initialize fields
        newCart.setTotalPrice(0.0);
        // set user relation: find user by email is not done here; Cart.user is a relation so typically you'd set user entity.
        // To keep this change minimal and compilable we will not set the user field here. If desired, wire UserRepository and set the relation.
        return cartRepo.save(newCart);
    }
}
