package com.pos.system.dto.ingredient;

import com.pos.system.entity.Measure;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ReadIngredientDto {
    Long id;
    String name;
    String categoryIngredientName;
    Measure measure;
    BigDecimal priceForUnit;
}
