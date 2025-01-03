package com.pos.system.servlet.ingcategory;

import com.pos.system.dto.ingcategory.CreateCategoryIngredientDto;
import com.pos.system.exception.ResourceNotFoundException;
import com.pos.system.exception.ValidationException;
import com.pos.system.service.CategoryIngredientService;
import com.pos.system.util.JspHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/ingredients/category/update")
public class UpdateCategoryIngredientServlet extends HttpServlet {
    private final CategoryIngredientService categoryIngredientService =
            CategoryIngredientService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        req.setAttribute("id", id);
        req.setAttribute("category", categoryIngredientService.findById(id));
        req.getRequestDispatcher(JspHelper.getPath("/ingcategory/update"))
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        CreateCategoryIngredientDto dto
                = CreateCategoryIngredientDto.of(req.getParameter("name"));

        try {
            categoryIngredientService.update(dto, id);
            resp.sendRedirect(req.getContextPath() + "/ingredients/categories");
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
