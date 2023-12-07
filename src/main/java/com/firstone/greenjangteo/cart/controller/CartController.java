package com.firstone.greenjangteo.cart.controller;

import com.firstone.greenjangteo.cart.domain.dto.CartProductDto;
import com.firstone.greenjangteo.cart.service.CartService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping(value = "/carts") // 장바구니 화면에서 장바구니에 추가하는 요청(개수 변경)과 상품 화면에서 추가하는 요청(1씩 증가)
    public @ResponseBody ResponseEntity<Object> addCart(
            @RequestParam Long userId,
            @RequestBody CartProductDto cartProductDto,
            BindingResult bindingResult
    ) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new Exception(HttpStatus.BAD_REQUEST.toString());
        }

        Map<String, Object> result = new HashMap<>();
        try {
            result = cartService.addCart(userId, cartProductDto);
        } catch (Exception e) {
            throw new Exception(HttpStatus.BAD_REQUEST.toString());
        }

        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/carts")
    public ResponseEntity<List<CartProductDto>> cartListAll(
            @RequestParam Long userId
    ) {
        List<CartProductDto> cartProductList = cartService.getCartList(userId);
        return ResponseEntity.ok().body(cartProductList);
    }

    @PutMapping(value = "/carts/cart-products/{cartProductId}")
    public ResponseEntity<Object> updateCartProductList(
            @RequestParam Long userId,
            @PathVariable("cartProductId") Long cartProductId,
            @RequestParam int quantity
    ) {
        cartService.updateCartProduct(userId, cartProductId, quantity);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/carts/cart-products/{cartProductId}")
    public ResponseEntity<Object> deleteCartProduct(
            @RequestParam Long userId,
            @PathVariable("cartProductId") Long cartProductId
    ) {
        cartService.deleteCartList(userId, cartProductId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/carts/selects")
    public ResponseEntity<Object> deleteSelectCartProductList(
            @RequestParam Long userId,
            @Parameter List<CartProductDto> cartProductDtoList
    ) {
        cartService.deleteCartProductList(userId, cartProductDtoList);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/carts/selects")
    public ResponseEntity<Object> deleteCart(
            @RequestParam Long userId
    ) {
        cartService.deleteCart(userId);
        return ResponseEntity.noContent().build();
    }
}