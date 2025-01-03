package com.pos.system.integration;

import com.pos.system.util.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class IntegrationTestBase {
    private static final String CLEAN_SQL = """
                        SET REFERENTIAL_INTEGRITY FALSE;
    TRUNCATE TABLE storage RESTART IDENTITY;
    TRUNCATE TABLE dishes_ingredients RESTART IDENTITY;
    TRUNCATE TABLE dish RESTART IDENTITY;
    TRUNCATE TABLE product RESTART IDENTITY;
    TRUNCATE TABLE ingredient RESTART IDENTITY;
    TRUNCATE TABLE category_ingredient RESTART IDENTITY;
    TRUNCATE TABLE category_product RESTART IDENTITY;
    SET REFERENTIAL_INTEGRITY TRUE;""";
    private static final String CREATE_SQL = """
            CREATE TABLE IF NOT EXISTS category_ingredient
            (
                id     SERIAL PRIMARY KEY,
                name   VARCHAR(128) UNIQUE NOT NULL,
                status VARCHAR(32)         NOT NULL DEFAULT 'ACTIVE'
            );
            
            CREATE TABLE IF NOT EXISTS category_product
            (
                id     SERIAL PRIMARY KEY,
                name   VARCHAR(128) UNIQUE NOT NULL,
                image  VARCHAR(128),
                status VARCHAR(32)         NOT NULL DEFAULT 'ACTIVE'
            );
            
            CREATE TABLE IF NOT EXISTS ingredient
            (
                id                     BIGSERIAL PRIMARY KEY,
                name                   VARCHAR(128) UNIQUE NOT NULL,
                category_ingredient_id INT REFERENCES category_ingredient (id) ON DELETE SET NULL ,
                measure                VARCHAR(16)         NOT NULL,
                price_for_unit         DECIMAL(10, 2)      NOT NULL DEFAULT 0.00
            );
            
            ALTER TABLE ingredient
                ADD CONSTRAINT unique_name UNIQUE (name);
            
            CREATE TABLE IF NOT EXISTS product
            (
                id                  SERIAL PRIMARY KEY,
                name                VARCHAR(128) UNIQUE                  NOT NULL,
                category_product_id INT REFERENCES category_product (id) NOT NULL,
                image               VARCHAR(128),
                bought_price        DECIMAL(10, 2)                       NOT NULL DEFAULT 0.00,
                sale_price          DECIMAL(10, 2)                       NOT NULL DEFAULT 0.00,
                status              VARCHAR(32)                          NOT NULL DEFAULT 'ACTIVE'
            );
            
            CREATE TABLE IF NOT EXISTS dish
            (
                id                  SERIAL PRIMARY KEY,
                name                VARCHAR(128) UNIQUE                  NOT NULL,
                category_product_id INT REFERENCES category_product (id) NOT NULL,
                image               VARCHAR(128),
                price               DECIMAL(10, 2)                       NOT NULL DEFAULT 0.00,
                cost_price          DECIMAL(10, 2)                       NOT NULL DEFAULT 0.00,
                description         TEXT,
                updated_at          TIMESTAMP                                     DEFAULT now(),
                status              VARCHAR(32)                          NOT NULL DEFAULT 'ACTIVE'
            );
            
            CREATE TABLE IF NOT EXISTS dishes_ingredients
            (
                dish_id       INT            NOT NULL,
                ingredient_id INT            NOT NULL,
                quantity      DECIMAL(10, 2) NOT NULL,
                PRIMARY KEY (dish_id, ingredient_id),
                FOREIGN KEY (dish_id) REFERENCES dish (id) ON DELETE CASCADE,
                FOREIGN KEY (ingredient_id) REFERENCES ingredient (id) ON DELETE CASCADE
            );
            
            CREATE TABLE IF NOT EXISTS storage
            (
                ingredient_id BIGINT REFERENCES ingredient (id) ON DELETE CASCADE,
                quantity      DECIMAL(10, 2) NOT NULL,
                PRIMARY KEY (ingredient_id)
            );
            """;

    @BeforeAll
    static void prepareDatabase() throws SQLException {
        try (Connection connection = ConnectionPool.get();
             Statement statement = connection.createStatement()) {
            statement.execute(CREATE_SQL);
        }
    }

    @BeforeEach
    void cleanData() throws SQLException {
        try (Connection connection = ConnectionPool.get();
             Statement statement = connection.createStatement()) {
            statement.execute(CLEAN_SQL);
        }
    }

}
