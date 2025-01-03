package com.pos.system.mapper;

import com.pos.system.dto.ingcategory.ReadCategoryIngredientDto;
import com.pos.system.dto.ingredient.ReadIngredientDto;
import com.pos.system.entity.Ingredient;
import com.pos.system.entity.Measure;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ReadIngredientMapperTest {
    private final ReadIngredientMapper mapper
            = ReadIngredientMapper.getInstance();

    @Test
    void mapFrom() {
        String categoryName = "Meat";
        Ingredient entity = Ingredient.builder()
                .id(1L)
                .name("Beef")
                .measure(Measure.KG)
                .priceForUnit(new BigDecimal("0.00"))
                .build();

        ReadIngredientDto expected = ReadIngredientDto.builder()
                .id(1L)
                .name("Beef")
                .categoryIngredientName("Meat")
                .measure(Measure.KG)
                .priceForUnit(new BigDecimal("0.00"))
                .build();

        ReadIngredientDto actual = mapper.mapFrom(entity, categoryName);

        assertThat(actual).isEqualTo(expected);
    }
}