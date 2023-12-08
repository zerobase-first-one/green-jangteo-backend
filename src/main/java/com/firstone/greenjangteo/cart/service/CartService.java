package com.firstone.greenjangteo.cart.service;

import com.firstone.greenjangteo.cart.domain.dto.CartProductDto;
import com.firstone.greenjangteo.cart.domain.model.Cart;
import com.firstone.greenjangteo.cart.domain.model.CartProduct;
import com.firstone.greenjangteo.cart.repository.CartProductRepository;
import com.firstone.greenjangteo.cart.repository.CartRepository;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.service.ProductService;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.AuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final AuthenticationServiceImpl authenticationService;
    private final ProductService productService;

    public Map<String, Object> addCart(Long userId, CartProductDto cartProductDto) {
        User user = (User) authenticationService.loadUserByUsername(userId.toString());
        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null) {
            cart = Cart.createCart(user);
            cartRepository.save(cart);
        }

        Long productId = cartProductDto.getProductId();
        int inputValue = cartProductDto.getCartProductQuantity();
        Product product = productService.getProductDetail(productId).getProduct();

        CartProduct savedCartProduct = cartProductRepository.findCartProductByCartAndProduct(cart.getId(), product.getId());
        CartProduct cartProduct = null;
        Map<String, Object> result = new HashMap<>();
        result.put("productId", product.getId());

        if (savedCartProduct != null) {
            cartProduct = CartProductDto.cartProductModifiedOf(savedCartProduct.getCart(), savedCartProduct.getProduct(), savedCartProduct.getQuantity() + inputValue);
            result.put("modifiedAt", cartProduct.getModifiedAt());
        } else {
            cartProduct = cartProductRepository.save(CartProductDto.cartProductCreatedOf(cart, product, inputValue));
            result.put("createdAt", cartProduct.getCreatedAt());
        }

        cartProductRepository.save(cartProduct);

        return result;
    }

    @Transactional(readOnly = true)
    public List<CartProductDto> getCartList(Long userId) {
        List<CartProductDto> cartProductList = new ArrayList<>();

        User user = (User) authenticationService.loadUserByUsername(userId.toString());
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart != null) {
            List<CartProduct> cartProducts = cartProductRepository.findCartProductsByCartId(cart.getId());
            for (CartProduct cartProduct : cartProducts) {
                cartProductList.add(CartProductDto.of(cartProduct));
            }
        }

        return cartProductList;
    }

    public void updateCartProduct(Long userId, Long cartProductId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId);
        CartProduct cartProduct = cartProductRepository.findCartProductByCartAndProduct(cart.getId(), cartProductId);
        cartProduct = CartProductDto.cartProductModifiedOf(cartProduct, quantity);
        cartProductRepository.save(cartProduct);
    }


    public void deleteCartList(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId);
        CartProduct cartProduct = cartProductRepository.findCartProductByCartAndProduct(cart.getId(), productId);
        cartProductRepository.deleteById(cartProduct.getId());
    }

    public void deleteCartProductList(Long userId, List<CartProductDto> cartProductDtoList) {
        Cart cart = cartRepository.findByUserId(userId);
        for (CartProductDto cartProductDto : cartProductDtoList) {
            Long productId = cartProductDto.getProductId();
            CartProduct cartProduct = cartProductRepository.findCartProductByCartAndProduct(cart.getId(), productId);
            cartProductRepository.deleteById(cartProduct.getId());
        }
    }

    public void deleteCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        cartRepository.deleteById(cart.getId());
    }
}