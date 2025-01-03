package com.pos.system.dto.ingredient;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateIngredientDto {
    String name;
    String categoryName;
    String measure;
}