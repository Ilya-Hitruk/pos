package com.pos.system.validator;

import com.pos.system.dto.ingcategory.CreateCategoryIngredientDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCategoryIngredientValidator implements Validator<CreateCategoryIngredientDto> {

    @Override
    public ValidationResult validate(CreateCategoryIngredientDto obj) {
        ValidationResult validationResult = new ValidationResult();

        if (obj.getName() == null || obj.getName().isBlank()) {
            validationResult.addError(Error.of("invalid.category.name", "Field name is empty!"));
        }

        return validationResult;
    }

    private static final class InstanceHolder {
        private static final CreateCategoryIngredientValidator INSTANCE =
                new CreateCategoryIngredientValidator();
        
    }

    public static CreateCategoryIngredientValidator getInstance() {
        return InstanceHolder.INSTANCE;
    }

}
