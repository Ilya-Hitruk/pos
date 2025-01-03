package com.pos.system.dao;

import com.pos.system.AssertjDbSourceProvider;
import com.pos.system.entity.Ingredient;
import com.pos.system.entity.Measure;
import com.pos.system.integration.IntegrationTestBase;
import com.pos.system.util.ConnectionPool;
import org.assertj.db.type.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IngredientDaoIT extends IntegrationTestBase {
    private static final String TABLE_NAME = "ingredient";
    private static final String PREPARE_CATEGORY_INGREDIENT_SQL = """
            INSERT INTO category_ingredient(id, name) VALUES (1, 'Meat');
            """;
    private final IngredientDao ingredientDao = IngredientDao.getInstance();
    private Source source;
    private Table ingredientTable;

    @BeforeEach
    void setUp() {
        source = AssertjDbSourceProvider.getSource();
        ingredientTable = new Table(source, TABLE_NAME);

        try (Connection connection = ConnectionPool.get();
             PreparedStatement statement = connection.prepareStatement(PREPARE_CATEGORY_INGREDIENT_SQL)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void save_shouldCreateEntity_andReturnId() {
        Long expectedBeefId = 1L;
        Long expectedPorkId = 2L;
        Ingredient beef = Ingredient.builder()
                .name("Beef")
                .categoryIngredientId(1)
                .measure(Measure.KG)
                .build();
        Ingredient pork = Ingredient.builder()
                .name("Pork")
                .categoryIngredientId(1)
                .measure(Measure.KG)
                .build();

        Long beefId = ingredientDao.save(beef);
        Long porkId = ingredientDao.save(pork);

        assertThat(ingredientTable).hasNumberOfRows(2);
        assertThat(ingredientTable).row(0)
                .value("id").isEqualTo(expectedBeefId)
                .value("name").isEqualTo(beef.getName())
                .value("category_ingredient_id").isEqualTo(beef.getCategoryIngredientId())
                .value("measure").isEqualTo(beef.getMeasure().name())
                .value("price_for_unit").isEqualTo("0.00");
        assertThat(ingredientTable).row(1)
                .value("id").isEqualTo(expectedPorkId)
                .value("name").isEqualTo(pork.getName())
                .value("category_ingredient_id").isEqualTo(pork.getCategoryIngredientId())
                .value("measure").isEqualTo(pork.getMeasure().name())
                .value("price_for_unit").isEqualTo("0.00");

        assertThat(beefId).isEqualTo(expectedBeefId);
        assertThat(porkId).isEqualTo(expectedPorkId);
    }

    @Test
    void findById_shouldFindExistingEntity() {
        Ingredient entity = buildEntity();
        Long entityId = ingredientDao.save(entity);

        Optional<Ingredient> optionalEntity = ingredientDao.findById(entityId);
        assertThat(optionalEntity).isPresent();

        assertThat(ingredientTable).row(0)
                .value("id").isEqualTo(entityId);
    }

    @Test
    void findById_shouldReturnEmptyOptional_causeNoEntityWasFoundBySpecifiedId() {
        Long dummyId = -1L;

        Optional<Ingredient> optionalEntity = ingredientDao.findById(dummyId);
        assertThat(optionalEntity).isEmpty();
    }

    @Test
    void findAll_shouldReturnListOfAllExistingEntities() {
        List<Ingredient> ingredients = getIngredientList();
        ingredients.forEach(ingredientDao::save);

        List<Ingredient> actualResult = ingredientDao.findAll();

        assertThat(ingredientTable).hasNumberOfRows(actualResult.size());

        List<Long> expectedIds = ingredients.stream()
                .map(Ingredient::getId)
                .toList();

        List<Long> actualIds = actualResult.stream()
                .map(Ingredient::getId)
                .toList();

        assertThat(actualIds).containsAll(expectedIds);
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoEntitiesFound() {
        List<Ingredient> actualResult = ingredientDao.findAll();

        assertThat(ingredientTable).hasNumberOfRows(actualResult.size());
        assertThat(actualResult).isEmpty();
    }

    @Test
    void findIdByName_shouldReturnIdByName() {
        Ingredient entity = buildEntity();
        Long expectedId = ingredientDao.save(entity);

        Optional<Long> actualResult = ingredientDao.findIdByName(entity.getName());

        assertThat(actualResult).isPresent();
        assertThat(actualResult).get().isEqualTo(expectedId);
    }

    @Test
    void findIdByName_shouldReturnEmptyOptional_whenNameNotFound() {
        Optional<Long> actualResult = ingredientDao.findIdByName("dummy");
        assertThat(actualResult).isEmpty();
    }

    @Test
    void update_shouldUpdateEntity_andReturnTrue() {
        Ingredient entity = buildEntity();
        ingredientDao.save(entity);

        entity.setName("Pork");
        entity.setMeasure(Measure.LTR);

        Changes changes = new Changes(source);
        changes.setStartPointNow();

        boolean isUpdated = ingredientDao.update(entity);
        changes.setEndPointNow();

        assertTrue(isUpdated);
        assertThat(changes)
                .hasNumberOfChanges(1)
                .changeOfModificationOnTable("ingredient")
                .hasModifiedColumns("name", "measure");

        assertThat(ingredientTable)
                .row(0)
                .value("name").isEqualTo(entity.getName())
                .value("measure").isEqualTo(entity.getMeasure().name());
    }

    @Test
    void update_shouldNotUpdateEntity_andReturnFalse() {
        Ingredient dummy = buildEntity();
        dummy.setId(-1L);
        boolean isUpdated = ingredientDao.update(dummy);

        assertFalse(isUpdated);
    }

    @Test
    void delete_shouldDeleteEntityById() {
        Ingredient entity = buildEntity();
        Long entityId = ingredientDao.save(entity);

        Changes changes = new Changes(source).setStartPointNow();

        boolean isDeleted = ingredientDao.delete(entityId);
        changes.setEndPointNow();

        assertThat(changes.getChangesOfType(ChangeType.DELETION))
                .hasNumberOfChanges(1)
                .changeOnTable("ingredient");
        assertThat(ingredientTable).hasNumberOfRows(0);
        assertTrue(isDeleted);
    }

    @Test
    void delete_shouldNotDeleteEntity_whenNoIdFound_andReturnFalse() {
        Long fakeId = -1L;
        boolean actualResult = ingredientDao.delete(fakeId);
        assertFalse(actualResult);
    }

    private static Ingredient buildEntity() {
        return Ingredient.builder()
                .name("Beef")
                .categoryIngredientId(1)
                .measure(Measure.KG)
                .build();
    }

    private static List<Ingredient> getIngredientList() {
        return List.of(
                Ingredient.builder()
                        .name("Beef")
                        .categoryIngredientId(1)
                        .measure(Measure.KG)
                        .build(),
                Ingredient.builder()
                        .name("Pork")
                        .categoryIngredientId(1)
                        .measure(Measure.KG)
                        .build(),
                Ingredient.builder()
                        .name("Chicken")
                        .categoryIngredientId(1)
                        .measure(Measure.KG)
                        .build());
    }
}