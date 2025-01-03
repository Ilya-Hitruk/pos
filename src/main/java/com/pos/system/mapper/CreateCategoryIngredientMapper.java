package com.pos.system.mapper;

import com.pos.system.dto.ingcategory.CreateCategoryIngredientDto;
import com.pos.system.entity.CategoryIngredient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCategoryIngredientMapper implements Mapper<CreateCategoryIngredientDto, CategoryIngredient> {
    private static final class InstanceHolder {
        private static final CreateCategoryIngredientMapper INSTANCE =
                new CreateCategoryIngredientMapper();
    }

    public static CreateCategoryIngredientMapper getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public CategoryIngredient mapFrom(CreateCategoryIngredientDto obj) {
        return CategoryIngredient.builder()
                .name(obj.getName())
                .build();
    }
}
