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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(
        MockitoExtension.class
)
class IngredientServiceTest {
    @Mock
    private IngredientDao ingredientDao;
    @Mock
    private CategoryIngredientDao categoryIngredientDao;
    @Mock
    private CreateIngredientValidator createIngredientValidator;
    @Mock
    private ReadIngredientMapper readIngredientMapper;
    @Mock
    private CreateIngredientMapper createIngredientMapper;
    @InjectMocks
    private IngredientService ingredientService;

    @Test
    void create_success_shouldReturnEntityId() {
        long entityId = 1L;
        int categoryIngredientId = 1;

        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("Beef")
                .categoryName("Meat")
                .measure("KG")
                .build();

        Ingredient entity = Ingredient.builder()
                .name("Beef")
                .categoryIngredientId(1)
                .measure(Measure.KG)
                .build();

        doReturn(new ValidationResult()).when(createIngredientValidator).validate(dto);
        doReturn(Optional.empty()).when(ingredientDao).findIdByName(entity.getName());
        doReturn(Optional.of(categoryIngredientId)).when(categoryIngredientDao).findIdByName(dto.getCategoryName());
        doReturn(entity).when(createIngredientMapper).mapDtoToEntity(dto, categoryIngredientId);
        doReturn(entityId).when(ingredientDao).save(entity);

        Long actualResult = ingredientService.create(dto);

        assertThat(actualResult).isEqualTo(entityId);
        verify(createIngredientValidator, times(1)).validate(dto);
        verify(ingredientDao, times(1)).findIdByName(entity.getName());
        verify(categoryIngredientDao, times(1)).findIdByName(dto.getCategoryName());
        verify(ingredientDao, times(1)).save(entity);
    }

    @Test
    void create_failed_shouldThrowValidationException_whenDtoIsInvalid() {
        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("")
                .categoryName("Test")
                .measure("KG")
                .build();

        ValidationResult validationResult = new ValidationResult();
        validationResult.addError(Error.of("invalid.wrong.measure", "Measure does not exist!"));

        doReturn(validationResult).when(createIngredientValidator).validate(dto);

        ValidationException exception
                = assertThrows(ValidationException.class, () -> ingredientService.create(dto));
        assertThat(exception.getErrors().getFirst()).isEqualTo(validationResult.getErrors().getFirst());
        assertThat(validationResult.getErrors()).hasSize(1);
        verifyNoInteractions(ingredientDao, categoryIngredientDao, createIngredientMapper);
    }

    @Test
    void create_failed_shouldThrowValidationException_whenEntityExistsBySpecifiedName() {
        long existingEntityId = 1L;
        String existingEntityName = "name";
        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("Beef")
                .categoryName(existingEntityName)
                .measure("KG")
                .build();
        Error expectedError = Error.of("invalid.ingredient.exists",
                "Ingredient with specified name is already exist!");

        doReturn(new ValidationResult()).when(createIngredientValidator).validate(dto);
        doReturn(Optional.of(existingEntityId)).when(ingredientDao).findIdByName(dto.getName());

        ValidationException exception
                = assertThrows(ValidationException.class, () -> ingredientService.create(dto));
        assertThat(exception.getErrors().getFirst()).isEqualTo(expectedError);
        verify(ingredientDao, atMost(1)).findIdByName(dto.getName());
        verifyNoInteractions(categoryIngredientDao, createIngredientMapper);
    }

    @Test
    void create_failed_shouldThrowValidationException_whenCategoryIngredientNotFound() {
        Error error = Error.of("invalid.category", "No such category found!");
        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("Beef")
                .categoryName("dummy")
                .measure("KG")
                .build();

        doReturn(new ValidationResult()).when(createIngredientValidator).validate(dto);
        doReturn(Optional.empty()).when(ingredientDao).findIdByName(dto.getName());
        doReturn(Optional.empty()).when(categoryIngredientDao).findIdByName(dto.getCategoryName());

        ValidationException exception
                = assertThrows(ValidationException.class, () -> ingredientService.create(dto));
        assertThat(exception.getErrors()).hasSize(1);
        assertThat(exception.getErrors().getFirst()).isEqualTo(error);
        verify(ingredientDao, times(1)).findIdByName(dto.getName());
        verify(categoryIngredientDao, atMost(1)).findIdByName(dto.getCategoryName());
        verifyNoInteractions(createIngredientMapper);
    }


    @Test
    void findById_shouldFindExistingEntityById() {
        long existingId = 1L;
        int categoryId = 1;
        String categoryName = "Meat";
        ReadIngredientDto expectedDto = ReadIngredientDto.builder()
                .id(1L)
                .name("Beef")
                .categoryIngredientName(categoryName)
                .measure(Measure.KG)
                .priceForUnit(new BigDecimal("0.00"))
                .build();

        Ingredient entity = Ingredient.builder()
                .id(existingId)
                .name("Beef")
                .categoryIngredientId(1)
                .measure(Measure.KG)
                .priceForUnit(new BigDecimal("0.00"))
                .build();

        doReturn(Optional.of(entity)).when(ingredientDao).findById(existingId);
        doReturn(Optional.of(categoryName)).when(categoryIngredientDao).findNameById(categoryId);
        doReturn(expectedDto).when(readIngredientMapper).mapFrom(entity, categoryName);

        ReadIngredientDto actualResult = ingredientService.findById(String.valueOf(existingId));

        assertThat(actualResult).isEqualTo(expectedDto);
        verify(ingredientDao, times(1)).findById(existingId);
        verify(categoryIngredientDao, times(1)).findNameById(categoryId);
        verify(readIngredientMapper, times(1)).mapFrom(entity, categoryName);
    }

    @Test
    void findById_shouldThrowException_whenEntityNotFoundById() {
        Long fakeId = -1L;
        String expectedMExceptionMessage = "Ingredient not found by id: " + fakeId;

        doReturn(Optional.empty()).when(ingredientDao).findById(fakeId);

        ResourceNotFoundException exception
                = assertThrows(ResourceNotFoundException.class, () -> ingredientService.findById(String.valueOf(fakeId)));
        assertThat(exception.getMessage()).isEqualTo(expectedMExceptionMessage);
        verify(ingredientDao, times(1)).findById(fakeId);
        verifyNoInteractions(categoryIngredientDao, readIngredientMapper);
    }

    @Test
    void findById_shouldSetDefaultCategoryNameAndReturnEntity_whenNoCategoryFoundById() {
        Long entityId = 1L;
        Integer dummyCategoryId = -1;
        String defaultCategoryName = "Not selected";
        ReadIngredientDto expectedDto = ReadIngredientDto.builder()
                .id(1L)
                .name("Beef")
                .categoryIngredientName(defaultCategoryName)
                .measure(Measure.KG)
                .priceForUnit(new BigDecimal("0.00"))
                .build();
        Ingredient entity = Ingredient.builder()
                .id(entityId)
                .name("Beef")
                .categoryIngredientId(dummyCategoryId)
                .measure(Measure.KG)
                .priceForUnit(new BigDecimal("0.00"))
                .build();

        doReturn(Optional.of(entity)).when(ingredientDao).findById(entityId);
        doReturn(Optional.empty()).when(categoryIngredientDao).findNameById(dummyCategoryId);
        doReturn(expectedDto).when(readIngredientMapper).mapFrom(entity, defaultCategoryName);

        ReadIngredientDto actualDto = ingredientService.findById(String.valueOf(entityId));

        assertThat(actualDto.getCategoryIngredientName()).isEqualTo(defaultCategoryName);
        assertThat(actualDto).isEqualTo(expectedDto);
        verify(ingredientDao, times(1)).findById(entityId);
        verify(categoryIngredientDao, times(1)).findNameById(dummyCategoryId);
        verify(readIngredientMapper, times(1)).mapFrom(entity, defaultCategoryName);
    }


    @Test
    void findAll_shouldReturnListOfExistingEntities() {
        List<Ingredient> entitiesList = getEntitiesList();
        List<ReadIngredientDto> dtosList = getReadDtosList();

        doReturn(entitiesList).when(ingredientDao).findAll();

        doReturn(Optional.of(dtosList.getFirst().getCategoryIngredientName())).when(categoryIngredientDao)
                .findNameById(entitiesList.getFirst().getCategoryIngredientId());
        doReturn(Optional.of(dtosList.getLast().getCategoryIngredientName())).when(categoryIngredientDao)
                .findNameById(entitiesList.getLast().getCategoryIngredientId());

        doReturn(dtosList.getFirst()).when(readIngredientMapper)
                .mapFrom(entitiesList.getFirst(), dtosList.getFirst().getCategoryIngredientName());
        doReturn(dtosList.getLast()).when(readIngredientMapper)
                .mapFrom(entitiesList.getLast(), dtosList.getLast().getCategoryIngredientName());

        List<ReadIngredientDto> actualResult = ingredientService.findAll();

        assertThat(actualResult).hasSize(2);
        assertThat(actualResult).containsAll(dtosList);
        verify(ingredientDao, times(1)).findAll();
        verify(categoryIngredientDao, times(2)).findNameById(any());
        verify(readIngredientMapper, times(2)).mapFrom(any(), any());
    }

    @Test
    void findAll_ShouldReturnEmptyList_whenNoEntitiesFound() {
        doReturn(Collections.EMPTY_LIST).when(ingredientDao).findAll();

        List<ReadIngredientDto> actualResult = ingredientService.findAll();

        assertThat(actualResult).isEmpty();
        verifyNoInteractions(categoryIngredientDao, readIngredientMapper);
    }

    @Test
    void update_success() {
        long entityId = 1L;
        int categoryIngredientId = 1;

        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("Beef")
                .categoryName("Meat")
                .measure("KG")
                .build();

        Ingredient entity = Ingredient.builder()
                .id(entityId)
                .name(dto.getName())
                .categoryIngredientId(categoryIngredientId)
                .measure(Measure.valueOf(dto.getMeasure()))
                .build();

        doReturn(new ValidationResult()).when(createIngredientValidator).validate(dto);
        doReturn(Optional.of(entity)).when(ingredientDao).findById(entityId);
        doReturn(Optional.of(categoryIngredientId)).when(categoryIngredientDao).findIdByName(dto.getCategoryName());
        doReturn(true).when(ingredientDao).update(entity);

        ingredientService.update(dto, String.valueOf(entityId));

        verify(createIngredientValidator, times(1)).validate(dto);
        verify(ingredientDao, times(1)).findById(entityId);
        verify(categoryIngredientDao, times(1)).findIdByName(dto.getCategoryName());
        verify(ingredientDao, times(1)).update(entity);
    }

    @Test
    void update_shouldThrowException_whenDtoIsNotValid() {
        long entityId = 1L;
        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("Beef")
                .categoryName("")
                .measure("KG")
                .build();

        ValidationResult validationResult = new ValidationResult();
        Error categoryError = Error.of("invalid.empty.category", "Category name is empty!");
        validationResult.addError(categoryError);

        doReturn(validationResult).when(createIngredientValidator).validate(dto);

        ValidationException exception
                = assertThrows(ValidationException.class, () -> ingredientService.update(dto, String.valueOf(entityId)));

        assertThat(exception.getErrors()).hasSize(1);
        assertThat(exception.getErrors().getFirst()).isEqualTo(categoryError);
        verifyNoInteractions(ingredientDao, categoryIngredientDao);
    }

    @Test
    void update_shouldThrowException_whenNoEntityExistById() {
        long fakeId = 1L;
        int categoryIngredientId = 1;

        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("Beef")
                .categoryName("Meat")
                .measure("KG")
                .build();


        doReturn(new ValidationResult()).when(createIngredientValidator).validate(dto);
        doReturn(Optional.empty()).when(ingredientDao).findById(fakeId);

        ResourceNotFoundException exception
                = assertThrows(ResourceNotFoundException.class, () -> ingredientService.update(dto, String.valueOf(fakeId)));
        assertThat(exception.getMessage()).isEqualTo("Ingredient does not exist!");
    }


    @Test
    void update_shouldThrowException_whenUnableToUpdateEntity() {
        long entityId = 1L;
        int categoryIngredientId = 1;

        CreateIngredientDto dto = CreateIngredientDto.builder()
                .name("Beef")
                .categoryName("Meat")
                .measure("KG")
                .build();

        Ingredient entity = Ingredient.builder()
                .id(entityId)
                .name(dto.getName())
                .categoryIngredientId(categoryIngredientId)
                .measure(Measure.valueOf(dto.getMeasure()))
                .build();

        doReturn(new ValidationResult()).when(createIngredientValidator).validate(dto);
        doReturn(Optional.of(entity)).when(ingredientDao).findById(entityId);
        doReturn(Optional.of(categoryIngredientId)).when(categoryIngredientDao).findIdByName(dto.getCategoryName());
        doReturn(false).when(ingredientDao).update(entity);


        IllegalStateException exception
                = assertThrows(IllegalStateException.class, () -> ingredientService.update(dto, String.valueOf(entityId)));
        assertThat(exception.getMessage()).isEqualTo("Unable to update ingredient with id: "
                                                     + entityId);

        verify(createIngredientValidator, times(1)).validate(dto);
        verify(ingredientDao, times(1)).findById(entityId);
        verify(categoryIngredientDao, times(1)).findIdByName(dto.getCategoryName());
        verify(ingredientDao, times(1)).update(entity);
    }

    @Test
    void delete_success() {
        Long existingId = 1L;
        doReturn(true).when(ingredientDao).delete(existingId);

        boolean isDeleted = ingredientDao.delete(existingId);

        assertTrue(isDeleted);
    }

    @Test
    void deleteFailed_shouldThrowException_whenUnableToDeleteEntity() {
        Long fakeId = -1L;
        doReturn(false).when(ingredientDao).delete(fakeId);

        IllegalStateException exception
                = assertThrows(IllegalStateException.class, () -> ingredientService.delete(String.valueOf(fakeId)));

        assertThat(exception.getMessage()).isEqualTo("Unable to delete ingredient with id: "
                                                     + fakeId);
    }

    private static List<ReadIngredientDto> getReadDtosList() {
        ReadIngredientDto beef = ReadIngredientDto.builder()
                .id(1L)
                .name("Beef")
                .categoryIngredientName("Meat")
                .measure(Measure.KG)
                .priceForUnit(new BigDecimal("0.00"))
                .build();

        ReadIngredientDto apple = ReadIngredientDto.builder()
                .id(2L)
                .name("Apple")
                .categoryIngredientName("Fruit")
                .measure(Measure.KG)
                .priceForUnit(new BigDecimal("0.00"))
                .build();

        return List.of(beef, apple);
    }

    private static List<Ingredient> getEntitiesList() {
        Ingredient beef = Ingredient.builder()
                .id(1L)
                .name("Beef")
                .categoryIngredientId(1)
                .measure(Measure.KG)
                .priceForUnit(new BigDecimal("0.00"))
                .build();


        Ingredient apple = Ingredient.builder()
                .id(2L)
                .name("Apple")
                .categoryIngredientId(2)
                .measure(Measure.KG)
                .priceForUnit(new BigDecimal("0.00"))
                .build();
        return List.of(beef, apple);
    }
}