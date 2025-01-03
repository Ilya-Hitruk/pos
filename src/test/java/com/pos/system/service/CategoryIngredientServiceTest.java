package com.pos.system.service;

import com.pos.system.dao.CategoryIngredientDao;
import com.pos.system.dto.ingcategory.CreateCategoryIngredientDto;
import com.pos.system.dto.ingcategory.ReadCategoryIngredientDto;
import com.pos.system.entity.CategoryIngredient;
import com.pos.system.entity.Status;
import com.pos.system.exception.ResourceNotFoundException;
import com.pos.system.exception.ValidationException;
import com.pos.system.mapper.CreateCategoryIngredientMapper;
import com.pos.system.mapper.ReadCategoryIngredientMapper;
import com.pos.system.validator.CreateCategoryIngredientValidator;
import com.pos.system.validator.Error;
import com.pos.system.validator.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(
        MockitoExtension.class
)
class CategoryIngredientServiceTest {
    @Mock
    private CategoryIngredientDao categoryIngredientDao;
    @Mock
    private ReadCategoryIngredientMapper readCategoryIngredientMapper;
    @Mock
    private CreateCategoryIngredientMapper createCategoryIngredientMapper;
    @Mock
    private CreateCategoryIngredientValidator categoryValidator;
    @InjectMocks
    private CategoryIngredientService categoryIngredientService;

    @Test
    void create_success_shouldReturnEntityId() {
        Integer expectedId = 1;
        CategoryIngredient entity = CategoryIngredient.builder()
                .name("Meat")
                .build();
        CreateCategoryIngredientDto dto = CreateCategoryIngredientDto.of("Meat");

        doReturn(new ValidationResult()).when(categoryValidator).validate(dto);
        doReturn(Optional.empty()).when(categoryIngredientDao).findIdByName(dto.getName());
        doReturn(entity).when(createCategoryIngredientMapper).mapFrom(dto);
        doReturn(expectedId).when(categoryIngredientDao).save(entity);

        Integer actualId = categoryIngredientService.create(dto);

        assertThat(actualId).isEqualTo(expectedId);
        verify(categoryValidator, times(1)).validate(dto);
        verify(categoryIngredientDao, times(1)).findIdByName(dto.getName());
        verify(createCategoryIngredientMapper, times(1)).mapFrom(dto);
        verify(categoryIngredientDao, times(1)).save(entity);
    }

    @Test
    void create_shouldThrowException_whenDtoIsNotValid() {
        CreateCategoryIngredientDto dto = CreateCategoryIngredientDto.of("");
        Error validationError
                = Error.of("invalid.category.name", "Field name is empty!");
        ValidationResult validationResult = new ValidationResult();
        validationResult.addError(validationError);

        doReturn(validationResult).when(categoryValidator).validate(dto);

        ValidationException exception
                = assertThrows(ValidationException.class, () -> categoryIngredientService.create(dto));

        assertThat(exception.getErrors()).hasSize(1);
        assertThat(exception.getErrors().getFirst()).isEqualTo(validationError);

        verify(categoryValidator, times(1)).validate(dto);
        verifyNoInteractions(categoryIngredientDao, createCategoryIngredientMapper);
    }

    @Test
    void create_shouldThrowException_whenEntityWithSpecifiedNameExists() {
        Integer existingId = 1;
        CreateCategoryIngredientDto dto = CreateCategoryIngredientDto.of("Meat");
        Error validationError
                = Error.of("invalid.category.exists", "Specified category is already exists!");

        doReturn(new ValidationResult()).when(categoryValidator).validate(dto);
        doReturn(Optional.of(existingId)).when(categoryIngredientDao).findIdByName(dto.getName());

        ValidationException exception
                = assertThrows(ValidationException.class, () -> categoryIngredientService.create(dto));

        assertThat(exception.getErrors()).hasSize(1);
        assertThat(exception.getErrors().getFirst()).isEqualTo(validationError);
        verifyNoInteractions(createCategoryIngredientMapper);
    }

    @Test
    void findAll__success_shouldReturnListOfExistingEntities() {
        List<ReadCategoryIngredientDto> expected = getReadDtosList();
        List<CategoryIngredient> entitiesList = getEntitiesList();

        doReturn(entitiesList).when(categoryIngredientDao).findAll();

        doReturn(expected.getFirst())
                .when(readCategoryIngredientMapper).mapFrom(entitiesList.getFirst());
        doReturn(expected.getLast())
                .when(readCategoryIngredientMapper).mapFrom(entitiesList.getLast());

        List<ReadCategoryIngredientDto> actual = categoryIngredientService.findAll();

        assertThat(actual).hasSize(2);
        assertThat(actual).containsAll(expected);
        verify(categoryIngredientDao, times(1)).findAll();
        verify(readCategoryIngredientMapper, times(2)).mapFrom(any());
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoEntitiesExist() {
        doReturn(Collections.EMPTY_LIST).when(categoryIngredientDao).findAll();

        List<ReadCategoryIngredientDto> actual = categoryIngredientService.findAll();

        assertThat(actual).isEmpty();
        verify(categoryIngredientDao, times(1)).findAll();
        verifyNoInteractions(readCategoryIngredientMapper);
    }

    @Test
    void findById_success_shouldReturnReadDto() {
        Integer entityId = 1;
        CategoryIngredient entity = CategoryIngredient.builder()
                .id(entityId)
                .name("Meat")
                .status(Status.ACTIVE)
                .build();

        ReadCategoryIngredientDto expected = ReadCategoryIngredientDto.builder()
                .id(entityId)
                .name("Meat")
                .status("ACTIVE")
                .build();

        doReturn(Optional.of(entity)).when(categoryIngredientDao).findById(entityId);
        doReturn(expected).when(readCategoryIngredientMapper).mapFrom(entity);

        ReadCategoryIngredientDto actual = categoryIngredientService.findById(String.valueOf(entityId));

        assertThat(actual).isEqualTo(expected);
        verify(categoryIngredientDao, times(1)).findById(entityId);
        verify(readCategoryIngredientMapper, times(1)).mapFrom(entity);
    }

    @Test
    void findById_shouldThrowException_whenNoEntityFound() {
        Integer fakeId = -1;

        doReturn(Optional.empty()).when(categoryIngredientDao).findById(fakeId);

        ResourceNotFoundException exception
                = assertThrows(ResourceNotFoundException.class, () -> categoryIngredientService.findById(String.valueOf(fakeId)));

        assertThat(exception.getMessage()).isEqualTo("Category wasn't found!");
        verify(categoryIngredientDao, times(1)).findById(fakeId);
        verifyNoInteractions(readCategoryIngredientMapper);
    }

    @Test
    void update_success() {
        Integer entityId = 1;
        CreateCategoryIngredientDto dto = CreateCategoryIngredientDto.of("Fish");
        CategoryIngredient entity = CategoryIngredient.builder()
                .id(entityId)
                .name("Meat")
                .status(Status.ACTIVE)
                .build();

        doReturn(new ValidationResult()).when(categoryValidator).validate(dto);
        doReturn(Optional.of(entity)).when(categoryIngredientDao).findById(entityId);
        doReturn(true).when(categoryIngredientDao).update(entity);

        categoryIngredientService.update(dto, String.valueOf(entityId));

        assertThat(dto.getName()).isEqualTo(entity.getName());
        verify(categoryValidator, times(1)).validate(dto);
        verify(categoryIngredientDao, times(1)).findById(entityId);
        verify(categoryIngredientDao, times(1)).update(entity);
    }

    @Test
    void update_shouldThrowException_whenEntityNotValid() {
        String id = "1";
        CreateCategoryIngredientDto dto = CreateCategoryIngredientDto.of("");
        Error validationError = Error.of("invalid.category.name", "Field name is empty!");
        ValidationResult validationResult = new ValidationResult();
        validationResult.addError(validationError);

        doReturn(validationResult).when(categoryValidator).validate(dto);

        ValidationException exception
                = assertThrows(ValidationException.class, () -> categoryIngredientService.update(dto, id));

        assertThat(exception.getErrors()).hasSize(1);
        assertThat(exception.getErrors().getFirst()).isEqualTo(validationError);
        verifyNoInteractions(categoryIngredientDao);
    }

    @Test
    void update_shouldThrowException_whenEntityNotExist() {
        Integer fakeId = -1;
        CreateCategoryIngredientDto dto = CreateCategoryIngredientDto.of("Fish");

        doReturn(new ValidationResult()).when(categoryValidator).validate(dto);
        doReturn(Optional.empty()).when(categoryIngredientDao).findById(fakeId);

        ResourceNotFoundException exception
                = assertThrows(ResourceNotFoundException.class, () -> categoryIngredientService.update(dto, String.valueOf(fakeId)));

        assertThat(exception.getMessage()).isEqualTo("Ingredient category does not exist!");
        verify(categoryIngredientDao, times(1)).findById(any());
        verify(categoryIngredientDao, times(0)).update(any());
    }

    @Test
    void delete_success() {
        Integer entityId = 1;
        doReturn(true).when(categoryIngredientDao).delete(entityId);

        categoryIngredientService.delete(String.valueOf(entityId));

        verify(categoryIngredientDao, times(1)).delete(entityId);
    }

    @Test
    void delete_ShouldThrowException_whenUnableToDeleteEntity() {
        Integer fakeId = -1;

        doReturn(false).when(categoryIngredientDao).delete(fakeId);

        IllegalStateException exception
                = assertThrows(IllegalStateException.class, () -> categoryIngredientService.delete(String.valueOf(fakeId)));

        assertThat(exception.getMessage()).isEqualTo("Unable to delete ingredient with id: "
                                                     + fakeId);
    }

    private static List<CategoryIngredient> getEntitiesList() {
        CategoryIngredient meat = CategoryIngredient.builder()
                .id(1)
                .name("Meat")
                .status(Status.ACTIVE)
                .build();
        CategoryIngredient vegetables = CategoryIngredient.builder()
                .id(2)
                .name("Vegetables")
                .status(Status.HIDDEN)
                .build();
        return List.of(
                meat, vegetables
        );
    }

    private static List<ReadCategoryIngredientDto> getReadDtosList() {
        ReadCategoryIngredientDto meat = ReadCategoryIngredientDto.builder()
                .id(1)
                .name("Meat")
                .status("ACTIVE")
                .build();

        ReadCategoryIngredientDto vegetables = ReadCategoryIngredientDto.builder()
                .id(2)
                .name("Vegetables")
                .status("HIDDEN")
                .build();

        return List.of(
                meat, vegetables
        );
    }
}