package com.pos.system.servlet.ingredient;

import com.pos.system.dto.ingredient.CreateIngredientDto;
import com.pos.system.entity.Measure;
import com.pos.system.exception.ValidationException;
import com.pos.system.service.CategoryIngredientService;
import com.pos.system.service.IngredientService;
import com.pos.system.util.JspHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/ingredient/create")
public class CreateIngredientServlet extends HttpServlet {
    private final IngredientService ingredientService
            = IngredientService.getInstance();

    private final CategoryIngredientService categoryIngredientService
            = CategoryIngredientService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("measures", Measure.values());
        req.setAttribute("categories", categoryIngredientService.findAll());
        req.getRequestDispatcher(JspHelper.getPath("ingredient/create"))
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CreateIngredientDto createIngredientDto = CreateIngredientDto.builder()
                .name(req.getParameter("name"))
                .categoryName(req.getParameter("category"))
                .measure(req.getParameter("measure"))
                .build();

        try {
            ingredientService.create(createIngredientDto);
            resp.sendRedirect(req.getContextPath() + "/ingredients");
        } catch (ValidationException e) {
            req.setAttribute("errors", e.getErrors());
            doGet(req, resp);
        }
    }
}
