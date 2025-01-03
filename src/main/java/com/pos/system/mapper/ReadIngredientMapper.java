package com.pos.system.mapper;

import com.pos.system.dto.ingredient.ReadIngredientDto;
import com.pos.system.entity.Ingredient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadIngredientMapper {
    private static final class InstanceHolder {
        private static final ReadIngredientMapper INSTANCE = new ReadIngredientMapper();
    }

    public static ReadIngredientMapper getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public ReadIngredientDto mapFrom(Ingredient obj, String categoryName) {
        return ReadIngredientDto.builder()
                .id(obj.getId())
                .name(obj.getName())
                .categoryIngredientName(categoryName)
                .measure(obj.getMeasure())
                .priceForUnit(obj.getPriceForUnit())
                .build();
    }
}
