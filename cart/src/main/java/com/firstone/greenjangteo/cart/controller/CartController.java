package com.example.greenjangteo.cart.controller;

import com.example.greenjangteo.cart.domain.dto.CartDto;
import com.example.greenjangteo.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "user/{userId}/cart")
    public @ResponseBody ResponseEntity<Object> addCart(
            @RequestBody CartDto cartDto,
            @PathVariable("userId") Long userId,
            BindingResult bindingResult
            //,Principal principal
    ) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new Exception(HttpStatus.BAD_REQUEST.toString());
        }

        //String email = principal.getName();

        Map<String, Object> result = new HashMap<>();
        try {
            result = cartService.addCart(userId, cartDto);
        } catch (Exception e) {
            throw new Exception(HttpStatus.BAD_REQUEST.toString());
        }

        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "user/{userId}/cart")
    public ResponseEntity<List<CartDto>> cartListAll(
            @PathVariable("userId") Long userId
            //,Principal principal
    ) throws Exception {
        List<CartDto> cartProductList = cartService.getCartList(userId);//principal.getName()); <<회원 인증관련
        return ResponseEntity.ok(cartProductList);
    }

    @PutMapping(value = "user/{userId}/cart/{cartId}")
    public ResponseEntity<Object> updateCartList(
            @PathVariable("userId") Long userId,
            @PathVariable("cartId") Long cartId,
            @RequestBody List<CartDto> cartDtoList
    ) throws Exception {
        cartService.updateCartList(userId, cartId, cartDtoList);
        return ResponseEntity.ok(204);
    }

    @DeleteMapping(value = "user/{userId}/cart/{cartId}")
    public ResponseEntity<Object> deleteCartList(
            @PathVariable("userId") Long userId,
            @PathVariable("cartId") Long cartId,
            @RequestParam List<Long> productIdList
    ) throws Exception {
        cartService.deleteCartList(userId, cartId, productIdList);
        return ResponseEntity.ok(204);
    }
}