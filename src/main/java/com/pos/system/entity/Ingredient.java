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
public class Ingredient {
    private Long id;
    private String name;
    private Integer categoryIngredientId;
    private Measure measure;
    private BigDecimal priceForUnit;
}
