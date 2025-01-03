package com.pos.system.mapper;

import com.pos.system.dto.ingcategory.CreateCategoryIngredientDto;
import com.pos.system.entity.CategoryIngredient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CreateCategoryIngredientMapperTest {
    private final CreateCategoryIngredientMapper mapper
            = CreateCategoryIngredientMapper.getInstance();

    @Test
    void mapFrom() {
        CreateCategoryIngredientDto dto =
                CreateCategoryIngredientDto.of("Meat");

        CategoryIngredient expected = CategoryIngredient.builder()
                .name("Meat")
                .build();

        CategoryIngredient actual = mapper.mapFrom(dto);

        assertThat(actual).isEqualTo(expected);
    }
}