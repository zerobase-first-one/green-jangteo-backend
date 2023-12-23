package com.firstone.greenjangteo.product.domain.document;

import com.firstone.greenjangteo.product.domain.dto.CategoryDto;
import com.firstone.greenjangteo.product.domain.dto.ImageDto;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.elasticsearch.annotations.DateFormat.date_hour_minute_second_millis;
import static org.springframework.data.elasticsearch.annotations.DateFormat.epoch_millis;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "products")
@Mapping(mappingPath = "elastic/product-mapping.json")
@Setting(settingPath = "elastic/product-setting.json")
public class ProductDocument {
    @Id
    private Long id;

    private Store store;

    private CategoryDto category;

    private List<ImageDto> images;

    private String name;

    private int price;

    private String description;

    private int inventory;

    private int averageScore;

    private int salesRate;

    @Field(type = FieldType.Date, format = {date_hour_minute_second_millis, epoch_millis})
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = {date_hour_minute_second_millis, epoch_millis})
    private LocalDateTime modifiedAt;

    public static ProductDocument from(Product product) {
        return ProductDocument.builder()
                .id(product.getId())
                .store(product.getStore())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .category(CategoryDto.of(product.getCategory()))
                .averageScore(product.getAverageScore())
                .inventory(product.getInventory())
                .salesRate(product.getSalesRate())
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .build();
    }
}