package com.pos.system.mapper;

import com.pos.system.dto.ingredient.CreateIngredientDto;
import com.pos.system.entity.Ingredient;
import com.pos.system.entity.Measure;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateIngredientMapper {

    private static final class InstanceHolder {
        private static final CreateIngredientMapper INSTANCE =  new CreateIngredientMapper();
    }

    public static CreateIngredientMapper getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Ingredient mapDtoToEntity(CreateIngredientDto dto, Integer categoryId) {
        return Ingredient.builder()
                .name(dto.getName())
                .categoryIngredientId(categoryId)
                .measure(Measure.valueOf(dto.getMeasure()))
                .build();
    }
}
