package com.firstone.greenjangteo.cart.service;

import com.firstone.greenjangteo.cart.domain.dto.request.AddCartProductRequestDto;
import com.firstone.greenjangteo.cart.domain.dto.request.CartProductRequestDto;
import com.firstone.greenjangteo.cart.domain.dto.request.DeleteCartProductRequestDto;
import com.firstone.greenjangteo.cart.domain.dto.request.UpdateCartProductRequestDto;
import com.firstone.greenjangteo.cart.domain.dto.response.AddCartProductResponseDto;
import com.firstone.greenjangteo.cart.domain.dto.response.CartProductListResponseDto;
import com.firstone.greenjangteo.cart.domain.model.Cart;
import com.firstone.greenjangteo.cart.domain.model.CartProduct;
import com.firstone.greenjangteo.cart.form.DeleteSelectCartProductForm;
import com.firstone.greenjangteo.cart.repository.CartProductRepository;
import com.firstone.greenjangteo.cart.repository.CartRepository;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.ProductImage;
import com.firstone.greenjangteo.product.service.ProductImageService;
import com.firstone.greenjangteo.product.service.ProductService;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final UserServiceImpl userService;
    private final ProductService productService;
    private final ProductImageService productImageService;

    public AddCartProductResponseDto addCart(AddCartProductRequestDto addCartProductRequestDto) {
        User user = userService.getUser(addCartProductRequestDto.getUserId());
        Optional<Cart> addCart = cartRepository.findByUserId(addCartProductRequestDto.getUserId());

        if (!addCart.isPresent()) {
            Cart curCart = Cart.createCart(user);
            cartRepository.save(curCart);
        }

        Product product = productService.getProduct(addCartProductRequestDto.getCartProduct().getProductId());
        Optional<Cart> cart = cartRepository.findByUserId(addCartProductRequestDto.getUserId());
        Optional<CartProduct> cartProduct = cartProductRepository.findByCartIdAndProductId(cart.get().getId(), product.getId());

        int quantity = addCartProductRequestDto.getCartProduct().getQuantity();
        CartProduct addCartProduct = null;
        if(!cartProduct.isPresent()) {
            addCartProduct = CartProduct.cartProductCreatedOf(cart.get(), product, quantity);
        } else {
            quantity += cartProduct.get().getQuantity();
            addCartProduct = CartProduct.cartProductModifiedOf(cartProduct.get(), quantity);
        }

        cartProductRepository.save(addCartProduct);
        return AddCartProductResponseDto.of(addCartProduct.getCart().getId(), addCartProduct.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<CartProductListResponseDto> getCartList(Long userId) {
        List<CartProductListResponseDto> cartProductList = new ArrayList<>();

        if (userService.getUser(userId) != null) {
            Optional<Cart> cart = cartRepository.findByUserId(userId);
            if (cart.isEmpty()) {
                return new ArrayList<>();
            }

            List<CartProduct> cartProducts = cartProductRepository.findCartProductsByCartId(cart.get().getId());
            for (CartProduct curCartProduct : cartProducts) {
                List<ProductImage> imageDto = productImageService.getProductImages(curCartProduct.getProduct().getId());
                cartProductList.add(CartProductListResponseDto.of(curCartProduct.getProduct(), curCartProduct.getQuantity(), imageDto.get(0).getUrl()));
            }
        }

        return cartProductList;
    }

    public void updateCartProduct(UpdateCartProductRequestDto updateCartProductRequestDto) {
        Optional<Cart> cart = cartRepository.findByUserId(updateCartProductRequestDto.getUserId());
        Optional<CartProduct> cartProduct = cartProductRepository.findByCartIdAndProductId(cart.get().getId(),
                                            updateCartProductRequestDto.getCartProduct().getProductId());
        CartProduct updateCartProductDto = CartProduct.cartProductModifiedOf(cartProduct.get(),
                                            updateCartProductRequestDto.getCartProduct().getQuantity());
        cartProductRepository.save(updateCartProductDto);
    }


    public void deleteCartList(DeleteCartProductRequestDto deleteCartProductRequestDto) {
        Optional<CartProduct> cartProduct = cartProductRepository.findById(deleteCartProductRequestDto.getCartProductId());
        cartProductRepository.deleteById(cartProduct.get().getId());
    }

    public void deleteCartProductList(DeleteSelectCartProductForm deleteSelectCartProductForm) {
        for (CartProductRequestDto cartProductDto : deleteSelectCartProductForm.getCartProducts()) {
            Optional<CartProduct> cartProduct = cartProductRepository.findById(cartProductDto.getCartProductId());
            cartProductRepository.deleteById(cartProduct.get().getId());
        }
    }

    public void deleteCart(Long userId) {
        Optional<Cart> cart = cartRepository.findByUserId(userId);
        cart.get().setUser(null);
        cartRepository.deleteById(cart.get().getId());
    }
}