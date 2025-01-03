package com.pos.system.dao;

import com.pos.system.entity.Ingredient;
import com.pos.system.entity.Measure;
import com.pos.system.util.ConnectionPool;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IngredientDao implements Dao<Long, Ingredient> {
    private static final String SAVE_SQL = """
            INSERT INTO ingredient(name, category_ingredient_id, measure)
            VALUES (?, ?, ?);
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT
                id,
                name,
                category_ingredient_id,
                measure,
                price_for_unit
            FROM ingredient
            WHERE id = ?;
            """;

    private static final String FIND_ID_BY_NAME_SQL = """
            SELECT
                id
            FROM ingredient
            WHERE name ILIKE ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT
                id,
                name,
                category_ingredient_id,
                measure,
                price_for_unit
            FROM ingredient;
            """;

    private static final String UPDATE_SQL = """
            UPDATE ingredient
            SET name = ?,
                category_ingredient_id = ?,
                measure = ?
            WHERE id = ?;
            """;

    private static final String DELETE_SQL = """
            DELETE FROM ingredient
            WHERE id = ?;
            """;

    private static final class InstanceHolder {
        public static final IngredientDao INSTANCE = new IngredientDao();
    }

    public static IngredientDao getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    @SneakyThrows(SQLException.class)
    public Long save(Ingredient entity) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, entity.getName());
            statement.setObject(2, entity.getCategoryIngredientId());
            statement.setObject(3, entity.getMeasure().name());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            entity.setId(generatedKeys.getObject("id", Long.class));

            return entity.getId();
        }
    }

    @Override
    @SneakyThrows(SQLException.class)
    public Optional<Ingredient> findById(Long id) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            Ingredient ingredient = null;

            if (resultSet.next()) {
                ingredient = buildEntity(resultSet);
            }

            return Optional.ofNullable(ingredient);
        }
    }

    @Override
    @SneakyThrows(SQLException.class)
    public List<Ingredient> findAll() {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<Ingredient> ingredients = new ArrayList<>();

            while (resultSet.next()) {
                ingredients.add(buildEntity(resultSet));
            }

            return ingredients;
        }
    }

    @SneakyThrows(SQLException.class)
    public Optional<Long> findIdByName(String name) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement statement = connection.prepareStatement(FIND_ID_BY_NAME_SQL)) {
            statement.setObject(1, name);
            ResultSet resultSet = statement.executeQuery();
            Long id = null;

            if (resultSet.next()) {
                id = resultSet.getObject("id", Long.class);
            }
            return Optional.ofNullable(id);
        }
    }

    @Override
    @SneakyThrows(SQLException.class)
    public boolean update(Ingredient entity) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setObject(1, entity.getName());
            statement.setObject(2, entity.getCategoryIngredientId());
            statement.setObject(3, entity.getMeasure().name());
            statement.setObject(4, entity.getId());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    @SneakyThrows(SQLException.class)
    public boolean delete(Long id) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private static Ingredient buildEntity(ResultSet resultSet) throws SQLException {
        return Ingredient.builder()
                .id(resultSet.getObject("id", Long.class))
                .name(resultSet.getObject("name", String.class))
                .categoryIngredientId(resultSet.getObject("category_ingredient_id", Integer.class))
                .measure(Measure.valueOf(resultSet.getObject("measure", String.class)))
                .priceForUnit(resultSet.getBigDecimal("price_for_unit"))
                .build();
    }
}
