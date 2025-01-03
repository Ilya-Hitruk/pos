package com.pos.system.validator;

import com.pos.system.dto.ingredient.CreateIngredientDto;
import com.pos.system.entity.Measure;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateIngredientValidator implements Validator<CreateIngredientDto> {
    private static final class InstanceHolder {
        private static final CreateIngredientValidator INSTANCE = new CreateIngredientValidator();
    }
    public static CreateIngredientValidator getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public ValidationResult validate(CreateIngredientDto obj) {
        ValidationResult validationResult = new ValidationResult();

        validateName(obj.getName(), validationResult);
        validateCategory(obj.getCategoryName(), validationResult);
        validateMeasure(obj.getMeasure(), validationResult);

        return validationResult;
    }

    private void validateMeasure(String measure, ValidationResult validationResult) {
        if (measure == null || Measure.find(measure).isEmpty()) {
            validationResult.addError(Error.of("invalid.wrong.measure", "Measure does not exist!"));
        }
    }

    private void validateCategory(String categoryName, ValidationResult validationResult) {
        if (categoryName == null || categoryName.isBlank()) {
            validationResult.addError(Error.of("invalid.empty.category", "Category name is empty!"));
        }
    }

    private void validateName(String name, ValidationResult validationResult) {
        if (name == null || name.isBlank()) {
            validationResult.addError(Error.of("invalid.empty.name", "Field name is Empty!"));
        }
    }
}
