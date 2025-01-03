package com.pos.system.dto.ingcategory;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ReadCategoryIngredientDto {
    Integer id;
    String name;
    String status;
}
