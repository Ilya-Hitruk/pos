package com.pos.system.service;

import com.pos.system.dto.ingcategory.CreateCategoryIngredientDto;
import com.pos.system.dto.ingcategory.ReadCategoryIngredientDto;
import com.pos.system.exception.ResourceNotFoundException;
import com.pos.system.exception.ValidationException;
import com.pos.system.integration.IntegrationTestBase;
import com.pos.system.validator.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CategoryIngredientServiceIT extends IntegrationTestBase {
    private CategoryIngredientService categoryIngredientService;

    @BeforeEach
    void setUp() {
        categoryIngredientService = CategoryIngredientService.getInstance();
    }

    @Test
    void create_success_shouldReturnId() {
        Integer expectedId = 1;
        CreateCategoryIngredientDto dto = getCreateDto("Meat");

        Integer actualId = categoryIngredientService.create(dto);

        assertThat(actualId).isEqualTo(expectedId);
    }

    @Test
    void create_shouldThrowException_whenDtoIsNotValid() {
        CreateCategoryIngredientDto invalidDto = getCreateDto("");
        Error error = Error.of("invalid.category.name", "Field name is empty!");

        ValidationException exception
                = assertThrows(ValidationException.class, () -> categoryIngredientService.create(invalidDto));

        assertThat(exception.getErrors()).hasSize(1);
        assertThat(exception.getErrors().getFirst()).isEqualTo(error);
    }

    @Test
    void create_shouldThrowException_whenEntityIsAlreadyExist() {
        CreateCategoryIngredientDto dto = getCreateDto("Meat");
        Error error = Error.of("invalid.category.exists", "Specified category is already exists!");
        categoryIngredientService.create(dto);

        ValidationException exception
                = assertThrows(ValidationException.class, () -> categoryIngredientService.create(dto));
        assertThat(exception.getErrors()).hasSize(1);
        assertThat(exception.getErrors().getFirst()).isEqualTo(error);
    }

    @Test
    void findById_success() {
        ReadCategoryIngredientDto expected = ReadCategoryIngredientDto.builder()
                .id(1)
                .name("Meat")
                .status("ACTIVE")
                .build();
        CreateCategoryIngredientDto dto = getCreateDto("Meat");
        Integer entityId = categoryIngredientService.create(dto);

        ReadCategoryIngredientDto actualResult = categoryIngredientService.findById(String.valueOf(entityId));

        assertThat(actualResult).isEqualTo(expected);
    }

    @Test
    void findById_shouldThrowException_whenNoEntityFoundById() {
        Integer fakeId = -1;
        ResourceNotFoundException exception
                = assertThrows(ResourceNotFoundException.class, () -> categoryIngredientService.findById(String.valueOf(fakeId)));

        assertThat(exception.getMessage()).isEqualTo("Category wasn't found!");
    }

    @Test
    void findAll_success() {
        List<ReadCategoryIngredientDto> expected = getReadDtosList();

        categoryIngredientService.create(getCreateDto("Meat"));
        categoryIngredientService.create(getCreateDto("Fruits"));

        List<ReadCategoryIngredientDto> actualResult = categoryIngredientService.findAll();

        assertThat(actualResult).hasSize(expected.size());
        assertThat(actualResult).containsAll(expected);
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoEntitiesFound() {
        List<ReadCategoryIngredientDto> actual = categoryIngredientService.findAll();
        assertThat(actual).isEmpty();
    }

    @Test
    void update_success() {
        CreateCategoryIngredientDto createDto = getCreateDto("Meat");
        CreateCategoryIngredientDto updateDto = getCreateDto("Fruits");

        Integer entityId = categoryIngredientService.create(createDto);

        categoryIngredientService.update(updateDto, String.valueOf(entityId));
    }

    @Test
    void update_shouldThrowException_whenDtoIsNotValid() {
        Error expectedError = Error.of("invalid.category.name", "Field name is empty!");
        CreateCategoryIngredientDto createDto = getCreateDto("Meat");
        CreateCategoryIngredientDto updateDto = getCreateDto("");

        Integer entityId = categoryIngredientService.create(createDto);

        ValidationException exception
                = assertThrows(ValidationException.class, () -> categoryIngredientService.update(updateDto, String.valueOf(entityId)));

        assertThat(exception.getErrors()).hasSize(1);
        assertThat(exception.getErrors().getFirst()).isEqualTo(expectedError);
    }

    @Test
    void update_shouldThrowException_whenEntityNotExist() {
        Integer fakeId = -1;
        CreateCategoryIngredientDto updateDto = getCreateDto("Meat");

        ResourceNotFoundException exception
                = assertThrows(ResourceNotFoundException.class, () -> categoryIngredientService.update(updateDto, String.valueOf(fakeId)));
        assertThat(exception.getMessage()).isEqualTo("Ingredient category does not exist!");
    }

    @Test
    void delete_success() {
        CreateCategoryIngredientDto dto = getCreateDto("Meat");
        Integer entityId = categoryIngredientService.create(dto);

        categoryIngredientService.delete(String.valueOf(entityId));
    }

    @Test
    void delete_shouldThrowException_whenUnableToDeleteEntity() {
        String dummyId = "-1";
        IllegalStateException exception
                = assertThrows(IllegalStateException.class, () -> categoryIngredientService.delete(dummyId));
        assertThat(exception.getMessage()).isEqualTo("Unable to delete ingredient with id: "
                                                     + dummyId);
    }

    private List<ReadCategoryIngredientDto> getReadDtosList() {
        return List.of(
                ReadCategoryIngredientDto.builder()
                        .id(1)
                        .name("Meat")
                        .status("ACTIVE")
                        .build(),
                ReadCategoryIngredientDto.builder()
                        .id(2)
                        .name("Fruits")
                        .status("ACTIVE")
                        .build()
        );
    }

    private static CreateCategoryIngredientDto getCreateDto(String name) {
        return CreateCategoryIngredientDto.of(name);
    }
}
