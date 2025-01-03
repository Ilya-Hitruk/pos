package com.pos.system.dao;

import com.pos.system.entity.CategoryIngredient;
import com.pos.system.entity.Status;
import com.pos.system.util.ConnectionPool;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryIngredientDao implements Dao<Integer, CategoryIngredient> {
    private static final String SAVE_CATEGORY_SQL = """
            INSERT INTO category_ingredient(name)
            VALUES (?);
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT id,
                   name,
                   status
            FROM category_ingredient
            WHERE id = ?;
            """;

    private static final String FIND_ALL_SQL = """
            SELECT
                id,
                name,
                status
            FROM category_ingredient;
            """;

    private static final String FIND_CATEGORY_ID_BY_NAME_SQL = """
            SELECT
                id
            FROM category_ingredient
            WHERE name LIKE ?;
            """;

    private static final String FIND_NAME_BY_ID_SQL = """
            SELECT
                name
            FROM category_ingredient
            WHERE id = ?;
            """;
    private static final String UPDATE_CATEGORY_SQL = """
            UPDATE category_ingredient
            SET name = ?
            WHERE id = ?;
            """;

    private static final String DELETE_CATEGORY_SQL = """
            DELETE FROM category_ingredient
            WHERE id = ?;
            """;

    private static final class InstanceHolder {
        public static final CategoryIngredientDao INSTANCE = new CategoryIngredientDao();
    }

    public static CategoryIngredientDao getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    @SneakyThrows(SQLException.class)
    public Integer save(CategoryIngredient entity) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement statement = connection.prepareStatement(SAVE_CATEGORY_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, entity.getName());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            entity.setId(generatedKeys.getObject("id", Integer.class));

            return entity.getId();
        }
    }

    @Override
    @SneakyThrows(SQLException.class)
    public Optional<CategoryIngredient> findById(Integer id) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            CategoryIngredient entity = null;

            if (resultSet.next()) {
                entity = buildCategoryIngredient(resultSet);
            }
            return Optional.ofNullable(entity);
        }
    }

    @SneakyThrows(SQLException.class)
    public Optional<Integer> findIdByName(String name) {
        try(Connection connection = ConnectionPool.get();
            PreparedStatement statement = connection.prepareStatement(FIND_CATEGORY_ID_BY_NAME_SQL)) {
            statement.setObject(1, name);

            ResultSet resultSet = statement.executeQuery();
            Integer categoryId = null;

            if (resultSet.next()) {
                categoryId = resultSet.getObject("id", Integer.class);
            }

            return Optional.ofNullable(categoryId);
        }
    }

    @Override
    @SneakyThrows(SQLException.class)
    public List<CategoryIngredient> findAll() {
        try(Connection connection = ConnectionPool.get();
            PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<CategoryIngredient> categoryIngredients = new ArrayList<>();

            while (resultSet.next()) {
                categoryIngredients.add(buildCategoryIngredient(resultSet));
            }

            return categoryIngredients;
        }
    }

    @SneakyThrows(SQLException.class)
    public Optional<String> findNameById(Integer id) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement statement = connection.prepareStatement(FIND_NAME_BY_ID_SQL)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            String name = null;

            if (resultSet.next()) {
                name = resultSet.getObject("name", String.class);
            }

            return Optional.ofNullable(name);
        }
    }

    @Override
    @SneakyThrows(SQLException.class)
    public boolean update(CategoryIngredient entity) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement statement = connection.prepareStatement(UPDATE_CATEGORY_SQL)) {
            statement.setObject(1, entity.getName());
            statement.setObject(2, entity.getId());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    @SneakyThrows(SQLException.class)
    public boolean delete(Integer id) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement statement = connection.prepareStatement(DELETE_CATEGORY_SQL)) {
            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private static CategoryIngredient buildCategoryIngredient(ResultSet resultSet) throws SQLException {
        return CategoryIngredient.builder()
                .id(resultSet.getObject("id", Integer.class))
                .name(resultSet.getObject("name", String.class))
                .status(Status.valueOf(resultSet.getObject("status", String.class)))
                .build();
    }
}
