package com.pos.system.servlet.ingredient;

import com.pos.system.dto.ingredient.CreateIngredientDto;
import com.pos.system.entity.Measure;
import com.pos.system.exception.ResourceNotFoundException;
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
import java.util.List;

@WebServlet("/ingredient/update")
public class UpdateIngredientServlet extends HttpServlet {
    private final IngredientService ingredientService = IngredientService.getInstance();
    private final CategoryIngredientService categoryIngredientService = CategoryIngredientService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        req.setAttribute("ingredient", ingredientService.findById(id));
        req.setAttribute("categories", categoryIngredientService.findAll());
        req.setAttribute("measures", Measure.values());
        req.setAttribute("id", id);
        req.getRequestDispatcher(JspHelper.getPath("ingredient/update"))
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        CreateIngredientDto createDto = CreateIngredientDto.builder()
                .name(req.getParameter("name"))
                .categoryName(req.getParameter("category"))
                .measure(req.getParameter("measure"))
                .build();

        try {
            ingredientService.update(createDto, id);
            resp.sendRedirect(req.getContextPath() + "/ingredients");
        } catch (ValidationException e) {
            req.setAttribute("errors", e.getErrors());
            doGet(req, resp);
        } catch (ResourceNotFoundException | IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher(JspHelper.getPath("error-page"))
                    .forward(req, resp);
        }
    }
}
