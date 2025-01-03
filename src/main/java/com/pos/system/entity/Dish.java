package com.pos.system.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dish {
    private Integer id;
    private String name;
    private Integer categoryProductId;
    private String image;
    private BigDecimal price;
    private BigDecimal cost_price;
    private String description;
    private LocalDateTime updatedAt;
    private Status status;
    private List<DishIngredient> ingredients;
}
