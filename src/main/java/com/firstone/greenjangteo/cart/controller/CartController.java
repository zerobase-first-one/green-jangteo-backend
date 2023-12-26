package com.firstone.greenjangteo.cart.controller;

import com.firstone.greenjangteo.cart.domain.dto.request.AddCartProductRequestDto;
import com.firstone.greenjangteo.cart.domain.dto.request.DeleteCartProductRequestDto;
import com.firstone.greenjangteo.cart.domain.dto.request.UpdateCartProductRequestDto;
import com.firstone.greenjangteo.cart.domain.dto.response.CartProductListResponseDto;
import com.firstone.greenjangteo.cart.form.DeleteSelectCartProductForm;
import com.firstone.greenjangteo.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping(value = "/carts")
    public ResponseEntity addCart(
            @RequestBody AddCartProductRequestDto addCartProductRequestDto,
            BindingResult bindingResult
    ) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new Exception(HttpStatus.BAD_REQUEST.toString());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addCart(addCartProductRequestDto));
    }

    @GetMapping(value = "/carts")
    public ResponseEntity<List<CartProductListResponseDto>> cartListAll(
            @RequestParam Long userId
    ) {
        List<CartProductListResponseDto> cartProductList = cartService.getCartList(userId);
        return ResponseEntity.status(HttpStatus.OK).body(cartProductList);
    }

    @PutMapping(value = "/carts/cart-products/{cartProductId}")
    public ResponseEntity<Object> updateCartProductList(
            @RequestBody UpdateCartProductRequestDto updateCartProductRequestDto,
            BindingResult bindingResult
    ) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new Exception(HttpStatus.BAD_REQUEST.toString());
        }
        cartService.updateCartProduct(updateCartProductRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(value = "/carts/cart-products/{cartProductId}")
    public ResponseEntity<Void> deleteCartProduct(
            @RequestBody DeleteCartProductRequestDto deleteCartProductRequestDto
    ) {
        cartService.deleteCartList(deleteCartProductRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(value = "/carts/selects")
    public ResponseEntity<Void> deleteSelectCartProductList(
            @RequestBody DeleteSelectCartProductForm deleteSelectCartProductForm
    ) {
        cartService.deleteCartProductList(deleteSelectCartProductForm);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(value = "/carts")
    public ResponseEntity<Void> deleteCart(
            @RequestParam Long userId
    ) {
        cartService.deleteCart(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}