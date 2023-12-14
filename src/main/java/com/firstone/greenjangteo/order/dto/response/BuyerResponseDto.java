package com.firstone.greenjangteo.order.dto.response;

import com.firstone.greenjangteo.user.dto.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class BuyerResponseDto {
    private String buyerName;
    private String buyerPhone;
    private AddressDto shippingAddressDto;
}
