package com.pos.system.mapper;

import com.pos.system.dto.ingcategory.ReadCategoryIngredientDto;
import com.pos.system.entity.CategoryIngredient;
import com.pos.system.entity.Status;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class ReadCategoryIngredientMapperTest {
    private final ReadCategoryIngredientMapper mapper
            = ReadCategoryIngredientMapper.getInstance();

    @Test
    void mapFrom() {
        ReadCategoryIngredientDto expected = ReadCategoryIngredientDto.builder()
                .id(1)
                .name("Beef")
                .status("ACTIVE")
                .build();

        CategoryIngredient entity = CategoryIngredient.builder()
                .id(1)
                .name("Beef")
                .status(Status.ACTIVE)
                .build();

        ReadCategoryIngredientDto actual = mapper.mapFrom(entity);

        assertThat(actual).isEqualTo(expected);
    }
}