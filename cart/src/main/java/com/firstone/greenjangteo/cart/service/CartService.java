package com.example.greenjangteo.cart.service;

import com.example.greenjangteo.cart.domain.dto.CartDto;
import com.example.greenjangteo.cart.domain.model.Cart;
import com.example.greenjangteo.cart.domain.model.CartProduct;
import com.example.greenjangteo.cart.domain.model.User;
import com.example.greenjangteo.cart.repository.CartProductRepository;
import com.example.greenjangteo.cart.repository.CartRepository;
import com.example.greenjangteo.cart.repository.UserRepository;
import com.example.greenjangteo.product.domain.model.Product;
import com.example.greenjangteo.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;

    public Map<String, Object> addCart(Long userId, CartDto cartDto) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(Exception::new);
        Cart cart = cartRepository.findByUserId(user.getId());

        if (cart == null) {
            cart = Cart.createCart(user);
            cartRepository.save(cart);
        }

        Long productId = cartDto.getProductId();
        int value = cartDto.getQuantity();
        Product product = productRepository.findById(productId).orElseThrow(Exception::new);

        CartProduct savedCartProduct = cartProductRepository.findCartProductByCartAndProduct(cart.getId(), product.getId());

        int addQuantity = 0;
        if (savedCartProduct != null) {
            addQuantity = savedCartProduct.getQuantity();
        }

        CartProduct cartProduct = CartProduct.builder()
                .cart(cart)
                .product(product)
                .quantity(addQuantity + value)
                .createdAt(LocalDateTime.now())
                .build();

        cartProductRepository.save(cartProduct);

        Map<String, Object> result = new HashMap<>();
        result.put("productId", cartProduct.getId());
        result.put("createdAt", cartProduct.getCreatedAt());

        return result;
    }

    @Transactional(readOnly = true)
    public List<CartDto> getCartList(Long userId) throws Exception {
        List<CartDto> cartProductList = new ArrayList<>();

        User user = userRepository.findById(userId).orElseThrow(Exception::new);
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            return cartProductList;
        }

        List<CartDto> cartDtoList = cartProductRepository.findCartProductsByCartId(cart.getId());
        if (cartDtoList.isEmpty()) {
            throw new Exception("장바구니에 상품이 존재하지 않습니다.");
        }

        return cartDtoList;
    }

    @Transactional(readOnly = true)
    public void updateCartList(Long userId, Long cartId, List<CartDto> cartDtoList) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(Exception::new);
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            throw new Exception("장바구니 정보가 존재하지 않습니다.");
        }

        if (cartId != cart.getId()) {
            throw new Exception("장바구니 정보와 회원정보가 일치하지 않습니다.");
        }

        for (CartDto cartDto : cartDtoList) {
            Product product = productRepository.findById(cartDto.getProductId()).orElseThrow(Exception::new);
            CartProduct cartProduct = CartProduct.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(cartDto.getQuantity())
                    .modifiedAt(LocalDateTime.now())
                    .build();

            cartProductRepository.save(cartProduct);
        }
    }


    public void deleteCartList(Long userId, Long cartId, List<Long> productIdList) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(Exception::new);
        if (user == null) {
            throw new Exception("회원정보가 일치하지 않습니다.");
        }

        Cart cart = cartRepository.findByUserId(user.getId());
        if (!cart.getId().equals(cartId)) {
            throw new Exception("회원정보와 장바구니 정보가 일치하지 않습니다.");
        }

        for (int i = 0; i < productIdList.size(); i++) {
            cartProductRepository.deleteByCartAndProduct(cart.getId(), productIdList.get(i));
        }
    }
}