package com.pos.system.mapper;

import com.pos.system.dto.ingcategory.ReadCategoryIngredientDto;
import com.pos.system.entity.CategoryIngredient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadCategoryIngredientMapper  implements Mapper<CategoryIngredient, ReadCategoryIngredientDto>{

    @Override
    public ReadCategoryIngredientDto mapFrom(CategoryIngredient obj) {
        return ReadCategoryIngredientDto.builder()
                .id(obj.getId())
                .name(obj.getName())
                .status(obj.getStatus().name())
                .build();
    }

    private static final class InstanceHolder {
        private static final ReadCategoryIngredientMapper INSTANCE =
                new ReadCategoryIngredientMapper();
    }

    public static ReadCategoryIngredientMapper getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
