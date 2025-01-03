package com.pos.system.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private Integer id;
    private String name;
    private Integer categoryProductId;
    private String image;
    private BigDecimal boughtPrice;
    private BigDecimal salePrice;
    private Status status;
}
