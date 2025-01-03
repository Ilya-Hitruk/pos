package com.pos.system.validator;

import com.pos.system.dto.ingcategory.CreateCategoryIngredientDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CreateCategoryIngredientValidatorTest {
    private final CreateCategoryIngredientValidator createCategoryIngredientValidator
            = CreateCategoryIngredientValidator.getInstance();

    @Test
    void validate_success() {
        CreateCategoryIngredientDto dto = CreateCategoryIngredientDto.of("Meat");
        ValidationResult validationResult = createCategoryIngredientValidator.validate(dto);

        assertThat(validationResult.getErrors()).hasSize(0);
        assertFalse(validationResult.isNotValid());
    }

    @Test
    void validate_invalidNameIsNull() {
        CreateCategoryIngredientDto dto = CreateCategoryIngredientDto.of(null);
        ValidationResult validationResult = createCategoryIngredientValidator.validate(dto);

        assertThat(validationResult.getErrors()).hasSize(1);
        assertThat(validationResult.getErrors().getFirst().getCode())
                .isEqualTo("invalid.category.name");
        assertTrue(validationResult.isNotValid());
    }

    @Test
    void validate_invalidNameIsBlank() {
        CreateCategoryIngredientDto dto = CreateCategoryIngredientDto.of("");
        ValidationResult validationResult = createCategoryIngredientValidator.validate(dto);

        assertThat(validationResult.getErrors()).hasSize(1);
        assertThat(validationResult.getErrors().getFirst().getCode())
                .isEqualTo("invalid.category.name");
        assertTrue(validationResult.isNotValid());
    }
}