package com.pos.system.mapper;

import com.pos.system.dto.ingredient.CreateIngredientDto;
import com.pos.system.entity.Ingredient;
import com.pos.system.entity.Measure;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CreateIngredientMapperTest {
    private final CreateIngredientMapper mapper =
            CreateIngredientMapper.getInstance();

    @Test
    void mapDtoToEntity() {
        Integer categoryIngredientId = 1;
        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("Beef")
                .measure("KG")
                .build();

        Ingredient expected = Ingredient.builder()
                .name("Beef")
                .categoryIngredientId(categoryIngredientId)
                .measure(Measure.KG)
                .build();

        Ingredient actual = mapper.mapDtoToEntity(dto, categoryIngredientId);

        assertThat(actual).isEqualTo(expected);
    }
}