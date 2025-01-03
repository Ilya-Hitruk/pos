package com.pos.system.service;

import com.pos.system.dao.CategoryIngredientDao;
import com.pos.system.dto.ingcategory.ReadCategoryIngredientDto;
import com.pos.system.dto.ingcategory.CreateCategoryIngredientDto;
import com.pos.system.entity.CategoryIngredient;
import com.pos.system.exception.ResourceNotFoundException;
import com.pos.system.exception.ValidationException;
import com.pos.system.mapper.CreateCategoryIngredientMapper;
import com.pos.system.mapper.ReadCategoryIngredientMapper;
import com.pos.system.validator.CreateCategoryIngredientValidator;
import com.pos.system.validator.Error;
import com.pos.system.validator.ValidationResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryIngredientService {
    private final CategoryIngredientDao categoryIngredientDao;
    private final ReadCategoryIngredientMapper readCategoryIngredientMapper;
    private final CreateCategoryIngredientMapper createCategoryIngredientMapper;
    private final CreateCategoryIngredientValidator categoryValidator;

    private static final class InstanceHolder {
        private static final CategoryIngredientService INSTANCE = new CategoryIngredientService(
                CategoryIngredientDao.getInstance(),
                ReadCategoryIngredientMapper.getInstance(),
                CreateCategoryIngredientMapper.getInstance(),
                CreateCategoryIngredientValidator.getInstance()
        );
    }

    public static CategoryIngredientService getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Integer create(CreateCategoryIngredientDto dto) {
        ValidationResult validationResult = categoryValidator.validate(dto);

        if (validationResult.isNotValid()) {
            throw new ValidationException(validationResult.getErrors());
        }

        Optional<Integer> optionalId = categoryIngredientDao.findIdByName(dto.getName());
        if (optionalId.isPresent()) {
            validationResult.addError(Error.of("invalid.category.exists", "Specified category is already exists!"));
            throw new ValidationException(validationResult.getErrors());
        }
        return categoryIngredientDao.save(createCategoryIngredientMapper.mapFrom(dto));
    }

    public ReadCategoryIngredientDto findById(String id) {
        Objects.requireNonNull(id);
        Integer entityId = Integer.valueOf(id);

        return categoryIngredientDao.findById(entityId)
                .map(readCategoryIngredientMapper::mapFrom)
                .orElseThrow(() -> new ResourceNotFoundException("Category wasn't found!"));
    }

    public List<ReadCategoryIngredientDto> findAll() {
        return categoryIngredientDao.findAll().stream()
                .map(readCategoryIngredientMapper::mapFrom)
                .toList();
    }

    public void update(CreateCategoryIngredientDto dto, String id) {
        Objects.requireNonNull(id);
        ValidationResult validationResult = categoryValidator.validate(dto);

        if (validationResult.isNotValid()) {
            throw new ValidationException(validationResult.getErrors());
        }

        Integer entityId = Integer.valueOf(id);
        CategoryIngredient entity = categoryIngredientDao.findById(entityId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient category does not exist!"));

        entity.setId(entityId);
        entity.setName(dto.getName());

        boolean isUpdated = categoryIngredientDao.update(entity);

        if (!isUpdated) {
            throw new IllegalStateException("Unable to update ingredient with id: "
                                            + id);
        }
    }

    public void delete(String id) {
        Objects.requireNonNull(id);
        Integer entityId = Integer.valueOf(id);
        boolean isDeleted = categoryIngredientDao.delete(entityId);
        if (!isDeleted) {
            throw new IllegalStateException(("Unable to delete ingredient with id: "
                                                       + id));
        }
    }
}
