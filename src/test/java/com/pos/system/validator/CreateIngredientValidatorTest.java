package com.pos.system.validator;

import com.pos.system.dto.ingredient.CreateIngredientDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateIngredientValidatorTest {
    private final CreateIngredientValidator createIngredientValidator
            = CreateIngredientValidator.getInstance();

    @Test
    void validate_success() {
        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("Beef")
                .categoryName("Meat")
                .measure("KG")
                .build();

        ValidationResult validationResult = createIngredientValidator.validate(dto);

        assertThat(validationResult.getErrors()).isEmpty();
        assertFalse(validationResult.isNotValid());
    }

    @Test
    void validate_invalidMeasureIsNull() {
        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("Beef")
                .categoryName("Meat")
                .measure(null)
                .build();

        ValidationResult validationResult = createIngredientValidator.validate(dto);

        assertThat(validationResult.getErrors()).hasSize(1);
        assertThat(validationResult.getErrors().getFirst().getCode()).isEqualTo("invalid.wrong.measure");
        assertTrue(validationResult.isNotValid());
    }

    @Test
    void validate_invalidMeasureNotExistingMeasure() {
        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("Beef")
                .categoryName("Meat")
                .measure("dummy")
                .build();

        ValidationResult validationResult = createIngredientValidator.validate(dto);

        assertThat(validationResult.getErrors()).hasSize(1);
        assertThat(validationResult.getErrors().getFirst().getCode()).isEqualTo("invalid.wrong.measure");
        assertTrue(validationResult.isNotValid());
    }

    @Test
    void validate_invalidCategoryNameIsNull() {
        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("Beef")
                .categoryName(null)
                .measure("KG")
                .build();

        ValidationResult validationResult = createIngredientValidator.validate(dto);

        assertThat(validationResult.getErrors()).hasSize(1);
        assertThat(validationResult.getErrors().getFirst().getCode()).isEqualTo("invalid.empty.category");
        assertTrue(validationResult.isNotValid());
    }

    @Test
    void validate_invalidCategoryNameIsBlank() {
        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("Beef")
                .categoryName("")
                .measure("KG")
                .build();

        ValidationResult validationResult = createIngredientValidator.validate(dto);

        assertThat(validationResult.getErrors()).hasSize(1);
        assertThat(validationResult.getErrors().getFirst().getCode()).isEqualTo("invalid.empty.category");
        assertTrue(validationResult.isNotValid());
    }

    @Test
    void validate_invalidNameIsNull() {
        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name(null)
                .categoryName("Meat")
                .measure("KG")
                .build();

        ValidationResult validationResult = createIngredientValidator.validate(dto);

        assertThat(validationResult.getErrors()).hasSize(1);
        assertThat(validationResult.getErrors().getFirst().getCode()).isEqualTo("invalid.empty.name");
        assertTrue(validationResult.isNotValid());
    }

    @Test
    void validate_invalidNameIsBlank() {
        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("")
                .categoryName("Meat")
                .measure("KG")
                .build();

        ValidationResult validationResult = createIngredientValidator.validate(dto);

        assertThat(validationResult.getErrors()).hasSize(1);
        assertThat(validationResult.getErrors().getFirst().getCode()).isEqualTo("invalid.empty.name");
        assertTrue(validationResult.isNotValid());
    }

    @Test
    void validate_multipleInvalids() {
        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("")
                .categoryName(null)
                .measure("dummy")
                .build();

        ValidationResult validationResult = createIngredientValidator.validate(dto);
        List<String> errorCodes = validationResult.getErrors().stream()
                .map(Error::getCode)
                .toList();

        assertThat(errorCodes).hasSize(3);
        assertThat(errorCodes).contains(
                "invalid.wrong.measure",
                "invalid.empty.category",
                "invalid.empty.name"
        );
        assertTrue(validationResult.isNotValid());
    }
}