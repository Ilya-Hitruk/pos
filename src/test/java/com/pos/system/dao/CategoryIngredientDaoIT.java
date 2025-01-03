package com.pos.system.dao;

import com.pos.system.AssertjDbSourceProvider;
import com.pos.system.entity.CategoryIngredient;
import com.pos.system.integration.IntegrationTestBase;
import org.assertj.db.type.Changes;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CategoryIngredientDaoIT extends IntegrationTestBase {
    private static final String TABLE_NAME = "category_ingredient";
    private final CategoryIngredientDao categoryIngredientDao = CategoryIngredientDao.getInstance();
    private Source source;
    private Table categoryTable;

    @BeforeEach
    void setUp() {
        source = AssertjDbSourceProvider.getSource();
        categoryTable = new Table(source, TABLE_NAME);
    }

    @Test
    void save_shouldSaveEntity_andReturnId() {
        Integer expectedMeetId = 1;
        Integer expectedFishId = 2;

        CategoryIngredient meatCategory = CategoryIngredient.builder()
                .name("Meat")
                .build();

        CategoryIngredient fishCategory = CategoryIngredient.builder()
                .name("Fish")
                .build();

        categoryIngredientDao.save(meatCategory);
        categoryIngredientDao.save(fishCategory);

        assertThat(categoryTable).hasNumberOfRows(2);

        assertThat(categoryTable).row(0)
                .value("id").isEqualTo(expectedMeetId)
                .value("name").isEqualTo(meatCategory.getName())
                .value("status").isEqualTo("ACTIVE");

        assertThat(categoryTable).row(1)
                .value("id").isEqualTo(expectedFishId)
                .value("name").isEqualTo(fishCategory.getName())
                .value("status").isEqualTo("ACTIVE");
    }

    @Test
    void findById_shouldReturnEntityById() {
        CategoryIngredient entity = buildEntity();
        Integer expectedId = categoryIngredientDao.save(entity);

        Optional<CategoryIngredient> optionalEntity = categoryIngredientDao.findById(expectedId);

        assertThat(optionalEntity).isPresent();
        assertThat(optionalEntity.get().getId()).isEqualTo(expectedId);
        assertThat(categoryTable).hasNumberOfRows(1)
                .row(0)
                .value("id").isEqualTo(expectedId)
                .value("name").isEqualTo(entity.getName());
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenNoEntityByIdFound() {
        Integer fakeId = -1;

        Optional<CategoryIngredient> optionalEntity = categoryIngredientDao.findById(fakeId);

        assertThat(optionalEntity).isEmpty();
        assertThat(categoryTable).hasNumberOfRows(0);
    }

    @Test
    void findIdByName_shouldReturnIdByName() {
        CategoryIngredient entity = buildEntity();
        Integer expectedID = categoryIngredientDao.save(entity);

        Optional<Integer> optionalId = categoryIngredientDao.findIdByName(entity.getName());
        assertThat(optionalId).isPresent();
        assertThat(optionalId.get()).isEqualTo(expectedID);
        assertThat(categoryTable).hasNumberOfRows(1)
                .row(0)
                .value("id").isEqualTo(expectedID)
                .value("name").isEqualTo(entity.getName());
    }

    @Test
    void findIdByName_shouldReturnEmptyOptional_whenNoIdByNameFound() {
        Optional<Integer> optionalId = categoryIngredientDao.findIdByName("dummy");

        assertThat(categoryTable).hasNumberOfRows(0);
        assertThat(optionalId).isEmpty();
    }

    @Test
    void findAll_shouldReturnListOfExistingEntities() {
        CategoryIngredient meat = CategoryIngredient.builder()
                .name("Meat")
                .build();

        CategoryIngredient fish = CategoryIngredient.builder()
                .name("Fish")
                .build();

        CategoryIngredient vegetables = CategoryIngredient.builder()
                .name("Vegetables")
                .build();

        Integer meetId = categoryIngredientDao.save(meat);
        Integer fishId = categoryIngredientDao.save(fish);
        Integer vegetablesId = categoryIngredientDao.save(vegetables);

        List<CategoryIngredient> actualResult = categoryIngredientDao.findAll();
        List<Integer> ids = actualResult.stream()
                .map(CategoryIngredient::getId)
                .toList();

        assertThat(categoryTable).hasNumberOfRows(actualResult.size());
        assertThat(ids).contains(
                meetId,
                fishId,
                vegetablesId
        );
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoEntitiesFound() {
        List<CategoryIngredient> actualResult = categoryIngredientDao.findAll();

        assertThat(categoryTable).hasNumberOfRows(actualResult.size());
        assertThat(actualResult).isEmpty();
    }

    @Test
    void findNameById_shouldReturnEntityNameById() {
        CategoryIngredient entity = buildEntity();
        Integer id = categoryIngredientDao.save(entity);

        Optional<String> optionalName = categoryIngredientDao.findNameById(id);

        assertThat(optionalName).isPresent();
        assertThat(optionalName.get()).isEqualTo(entity.getName());
        assertThat(categoryTable).hasNumberOfRows(1)
                .row(0)
                .value("name").isEqualTo(optionalName.get());
    }

    @Test
    void findNameById_shouldReturnEmptyOptional_whenNoNameByIdFound() {
        Integer fakeId = -1;
        Optional<String> optionalName = categoryIngredientDao.findNameById(fakeId);

        assertThat(optionalName).isEmpty();
        assertThat(categoryTable).hasNumberOfRows(0);
    }

    @Test
    void update_shouldUpdateEntity_andReturnTrue() {
        String expectedName = "Fish";
        CategoryIngredient entity = buildEntity();
        categoryIngredientDao.save(entity);
        entity.setName(expectedName);

        Changes changes = new Changes(source).setStartPointNow();
        boolean isUpdated = categoryIngredientDao.update(entity);
        changes.setEndPointNow();

        assertTrue(isUpdated);
        assertThat(changes).changeOfModificationOnTable(TABLE_NAME)
                .hasModifiedColumns("name")
                .isModification();

        assertThat(categoryTable).hasNumberOfRows(1)
                .row(0)
                .value("name").isEqualTo(expectedName);
    }

    @Test
    void update_shouldNotUpdateEntity_andReturnFalse_whenNoEntityFound() {
        CategoryIngredient dummy = buildEntity();
        dummy.setId(-1);

        boolean isUpdated = categoryIngredientDao.update(dummy);

        assertFalse(isUpdated);
        assertThat(categoryTable).hasNumberOfRows(0);
    }

    @Test
    void delete_shouldDeleteEntityById_andReturnTrue() {
        CategoryIngredient entity = buildEntity();
        categoryIngredientDao.save(entity);

        Changes changes = new Changes(source).setStartPointNow();
        boolean isDeleted = categoryIngredientDao.delete(entity.getId());
        changes.setEndPointNow();

        assertThat(changes).changeOnTable(TABLE_NAME)
                .isDeletion();
        assertTrue(isDeleted);
        assertThat(categoryTable).hasNumberOfRows(0);
    }

    @Test
    void delete_shouldNotDeleteEntity_andReturnFalse() {
        Integer fakeId = -1;
        boolean isDeleted = categoryIngredientDao.delete(fakeId);

        assertFalse(isDeleted);
        assertThat(categoryTable).hasNumberOfRows(0);
    }

    private static CategoryIngredient buildEntity() {
        return CategoryIngredient.builder()
                .name("Meat")
                .build();
    }
}