package com.pos.system.dto.ingcategory;

import lombok.*;

@Value(staticConstructor = "of")
public class CreateCategoryIngredientDto {
    String name;
}
