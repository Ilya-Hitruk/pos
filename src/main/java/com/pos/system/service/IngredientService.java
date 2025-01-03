package com.pos.system.service;

import com.pos.system.dao.CategoryIngredientDao;
import com.pos.system.dao.IngredientDao;
import com.pos.system.dto.ingredient.CreateIngredientDto;
import com.pos.system.dto.ingredient.ReadIngredientDto;
import com.pos.system.entity.Ingredient;
import com.pos.system.entity.Measure;
import com.pos.system.exception.ResourceNotFoundException;
import com.pos.system.exception.ValidationException;
import com.pos.system.mapper.CreateIngredientMapper;
import com.pos.system.mapper.ReadIngredientMapper;
import com.pos.system.validator.CreateIngredientValidator;
import com.pos.system.validator.Error;
import com.pos.system.validator.ValidationResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class IngredientService {
    private final IngredientDao ingredientDao;
    private final CategoryIngredientDao categoryIngredientDao;
    private final CreateIngredientValidator createIngredientValidator;
    private final ReadIngredientMapper readIngredientMapper;
    private final CreateIngredientMapper createIngredientMapper;

    private static final class InstanceHolder {
        private static final IngredientService INSTANCE = new IngredientService(
                IngredientDao.getInstance(),
                CategoryIngredientDao.getInstance(),
                CreateIngredientValidator.getInstance(),
                ReadIngredientMapper.getInstance(),
                CreateIngredientMapper.getInstance()
        );
    }

    public static IngredientService getInstance() {
        return InstanceHolder.INSTANCE;
    }


    public Long create(CreateIngredientDto dto) {
        ValidationResult validationResult = createIngredientValidator.validate(dto);

        if (validationResult.isNotValid()) {
            throw new ValidationException(validationResult.getErrors());
        }

        Optional<Long> optionalId = ingredientDao.findIdByName(dto.getName());
        if (optionalId.isPresent()) {
            validationResult.addError(Error.of("invalid.ingredient.exists",
                    "Ingredient with specified name is already exist!"));
            throw new ValidationException(validationResult.getErrors());
        }

        Integer categoryId = getCategoryIdByNameIfExist(dto.getCategoryName());
        return ingredientDao.save(createIngredientMapper.mapDtoToEntity(dto, categoryId));
    }

    public ReadIngredientDto findById(String id) {
        Objects.requireNonNull(id);
        Long entityId = Long.valueOf(id);
        Ingredient ingredient = ingredientDao.findById(entityId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found by id: " + id));

        String categoryName = getCategoryNameByIdOrDefault(ingredient.getCategoryIngredientId());
        return readIngredientMapper.mapFrom(ingredient, categoryName);
    }

    public List<ReadIngredientDto> findAll() {
        return ingredientDao.findAll().stream()
                .map(ing ->
                        readIngredientMapper.mapFrom(ing, getCategoryNameByIdOrDefault(ing.getCategoryIngredientId())))
                .toList();
    }


    public void update(CreateIngredientDto dto, String id) {
        Objects.requireNonNull(id);
        ValidationResult validationResult = createIngredientValidator.validate(dto);

        if (validationResult.isNotValid()) {
            throw new ValidationException(validationResult.getErrors());
        }

        Long ingredientId = Long.valueOf(id);
        Ingredient entity = ingredientDao.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient does not exist!"));

        Integer categoryId = getCategoryIdByNameIfExist(dto.getCategoryName());

        entity.setName(dto.getName());
        entity.setMeasure(Measure.valueOf(dto.getMeasure()));
        entity.setCategoryIngredientId(categoryId);

        boolean isUpdated = ingredientDao.update(entity);

        if (!isUpdated) {
            throw new IllegalStateException("Unable to update ingredient with id: "
                                            + id);
        }
    }

    public void delete(String id) {
        Objects.requireNonNull(id);
        Long ingredientId = Long.valueOf(id);

        boolean isDeleted = ingredientDao.delete(ingredientId);
        if (!isDeleted) {
            throw new IllegalStateException(("Unable to delete ingredient with id: "
                                             + ingredientId));
        }
    }

    private String getCategoryNameByIdOrDefault(Integer categoryId) {
        return categoryIngredientDao
                .findNameById(categoryId)
                .orElse("Not selected");
    }

    private Integer getCategoryIdByNameIfExist(String categoryName) {
        return categoryIngredientDao.findIdByName(categoryName)
                .orElseThrow(() -> new ValidationException(List.of(Error.of(
                        "invalid.category", "No such category found!"))));
    }
}
