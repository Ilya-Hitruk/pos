package com.pos.system.service;

import com.pos.system.dao.CategoryIngredientDao;
import com.pos.system.dto.ingcategory.CreateCategoryIngredientDto;
import com.pos.system.dto.ingredient.CreateIngredientDto;
import com.pos.system.dto.ingredient.ReadIngredientDto;
import com.pos.system.entity.CategoryIngredient;
import com.pos.system.entity.Measure;
import com.pos.system.exception.ResourceNotFoundException;
import com.pos.system.exception.ValidationException;
import com.pos.system.integration.IntegrationTestBase;
import com.pos.system.validator.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IngredientServiceIT extends IntegrationTestBase {
    private IngredientService ingredientService;
    private CategoryIngredientDao categoryIngredientDao;

    @BeforeEach
    void setUp() {
        ingredientService = IngredientService.getInstance();
        categoryIngredientDao = CategoryIngredientDao.getInstance();
        CategoryIngredientService categoryIngredientService = CategoryIngredientService.getInstance();

        categoryIngredientService.create(CreateCategoryIngredientDto.of("Meat"));
    }

    @Test
    void create_success_shouldReturnId() {
        Long expectedId = 1L;
        CreateIngredientDto dto = getCreateDto("Beef", "Meat", "KG");

        Long actualId = ingredientService.create(dto);

        assertThat(actualId).isEqualTo(expectedId);
    }

    @Test
    void create_shouldThrowException_whenDtoNotValid() {
        List<Error> expectedErrors = getListOfValidationErrors();
        CreateIngredientDto invalidDto = getCreateDto(null, "", "dummy");

        ValidationException exception
                = assertThrows(ValidationException.class, () -> ingredientService.create(invalidDto));

        List<Error> actualErrors = exception.getErrors();

        assertThat(actualErrors.size()).isEqualTo(expectedErrors.size());
        assertThat(actualErrors).containsAll(expectedErrors);
    }

    @Test
    void create_shouldThrowException_whenEntityIsAlreadyExist() {
        CreateIngredientDto dto = getCreateDto("Beef", "Meat", "KG");
        Error error = Error.of("invalid.ingredient.exists",
                "Ingredient with specified name is already exist!");

        ingredientService.create(dto);

        ValidationException exception
                = assertThrows(ValidationException.class, () -> ingredientService.create(dto));

        assertThat(exception.getErrors()).hasSize(1);
        assertThat(exception.getErrors().getFirst()).isEqualTo(error);
    }

    @Test
    void create_shouldThrowException_whenCategoryNameNotExist() {
        CreateIngredientDto dto = getCreateDto("Beef", "dummy", "KG");
        Error error = Error.of("invalid.category", "No such category found!");

        ValidationException exception
                = assertThrows(ValidationException.class, () -> ingredientService.create(dto));

        assertThat(exception.getErrors()).hasSize(1);
        assertThat(exception.getErrors().getFirst()).isEqualTo(error);
    }

    @Test
    void finById_success_shouldReturnDto() {
        CreateIngredientDto dto = getCreateDto("Beef", "Meat", "KG");
        Long expectedId = ingredientService.create(dto);
        ReadIngredientDto expected = ReadIngredientDto.builder()
                .id(1L)
                .name("Beef")
                .categoryIngredientName("Meat")
                .measure(Measure.KG)
                .priceForUnit(new BigDecimal("0.00"))
                .build();

        ReadIngredientDto actualResult = ingredientService.findById(String.valueOf(expectedId));

        assertThat(actualResult.getId()).isEqualTo(expected.getId());
        assertThat(actualResult).isEqualTo(expected);
    }

    @Test
    void findById_shouldThrowException_whenEntityNotFoundById() {
        Long fakeId = -1L;

        ResourceNotFoundException exception
                = assertThrows(ResourceNotFoundException.class, () -> ingredientService.findById(String.valueOf(fakeId)));

        assertThat(exception.getMessage()).isEqualTo("Ingredient not found by id: " + fakeId);
    }

    @Test
    void findById_shouldSetDefaultCategoryName_whenCategoryNotFound() {
        Integer categoryId = 1;
        CreateIngredientDto dto = getCreateDto("Beef", "Meat", "KG");
        Long ingredientId = ingredientService.create(dto);
        categoryIngredientDao.delete(categoryId);

        ReadIngredientDto actualResult = ingredientService.findById(String.valueOf(ingredientId));

        assertThat(actualResult.getCategoryIngredientName()).isEqualTo("Not selected");
    }

    @Test
    void findAll_success_shouldReturnListOfExistingEntities() {
        List<ReadIngredientDto> expected = getListOfReadDtos();
        List<CreateIngredientDto> createDtos = getListOfCreateDtos();
        CategoryIngredient fruitsCategory = new CategoryIngredient();
        fruitsCategory.setName("Fruits");

        categoryIngredientDao.save(fruitsCategory);
        ingredientService.create(createDtos.getFirst());
        ingredientService.create(createDtos.getLast());

        List<ReadIngredientDto> actualResult = ingredientService.findAll();

        assertThat(actualResult).hasSize(expected.size());
        assertThat(actualResult).containsAll(expected);
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoEntitiesFound() {
        List<ReadIngredientDto> actualResult = ingredientService.findAll();

        assertThat(actualResult).isEmpty();
    }

    @Test
    void update_success() {
        CreateIngredientDto saveDto = getCreateDto("Beef", "Meat", "KG");
        CreateIngredientDto updateDto = getCreateDto("Pork", "Meat", "KG");

        Long entityId = ingredientService.create(saveDto);
        ingredientService.update(updateDto, String.valueOf(entityId));
    }

    @Test
    void update_shouldThrowException_whenDtoNotValid() {
        Long id = 1L;
        CreateIngredientDto dto = getCreateDto("", "Meat", "KG");

        ValidationException exception
                = assertThrows(ValidationException.class, () -> ingredientService.update(dto, String.valueOf(id)));

        assertThat(exception.getErrors()).hasSize(1);
    }

    @Test
    void update_shouldThrowException_whenEntityNotExist() {
        Long fakeId = -1L;
        CreateIngredientDto dto = getCreateDto("Beef", "Meat", "KG");

        ResourceNotFoundException exception
                = assertThrows(ResourceNotFoundException.class, () -> ingredientService.update(dto, String.valueOf(fakeId)));

        assertThat(exception.getMessage()).isEqualTo("Ingredient does not exist!");
    }

    @Test
    void update_shouldThrowException_whenCategoryNotExist() {
        CreateIngredientDto createDto = getCreateDto("Beef", "Meat", "KG");
        CreateIngredientDto updateDto = getCreateDto("Potato", "Vegetables", "KG");

        Long entityId = ingredientService.create(createDto);
        ValidationException exception
                = assertThrows(ValidationException.class, () -> ingredientService.update(updateDto, String.valueOf(entityId)));

        assertThat(exception.getErrors()).hasSize(1);
    }

    @Test
    void delete_success() {
        CreateIngredientDto dto = getCreateDto("Beef", "Meat", "KG");

        Long entityId = ingredientService.create(dto);

        ingredientService.delete(String.valueOf(entityId));
    }

    @Test
    void delete_shouldThrowException_whenUnableToDeleteEntity() {
        Long fakeId = -1L;

        IllegalStateException exception
                = assertThrows(IllegalStateException.class, () -> ingredientService.delete(String.valueOf(fakeId)));

        assertThat(exception.getMessage()).isEqualTo("Unable to delete ingredient with id: "
                                                     + fakeId);
    }

    private static List<CreateIngredientDto> getListOfCreateDtos() {
        return List.of(
                getCreateDto("Beef", "Meat", "KG"),
                getCreateDto("Apple", "Fruits", "KG")
        );
    }

    private static List<ReadIngredientDto> getListOfReadDtos() {
        return List.of(
                ReadIngredientDto.builder()
                        .id(1L)
                        .name("Beef")
                        .categoryIngredientName("Meat")
                        .measure(Measure.KG)
                        .priceForUnit(new BigDecimal("0.00"))
                        .build(),
                ReadIngredientDto.builder()
                        .id(2L)
                        .name("Apple")
                        .categoryIngredientName("Fruits")
                        .measure(Measure.KG)
                        .priceForUnit(new BigDecimal("0.00"))
                        .build()
        );
    }

    private static CreateIngredientDto getCreateDto(String name, String categoryName, String measure) {
        return CreateIngredientDto.builder()
                .name(name)
                .categoryName(categoryName)
                .measure(measure)
                .build();
    }

    private static List<Error> getListOfValidationErrors() {
        return List.of(
                Error.of("invalid.empty.name", "Field name is Empty!"),
                Error.of("invalid.empty.category", "Category name is empty!"),
                Error.of("invalid.wrong.measure", "Measure does not exist!")
        );
    }
}
