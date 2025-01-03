package com.pos.system.servlet.ingcategory;

import com.pos.system.dto.ingcategory.CreateCategoryIngredientDto;
import com.pos.system.exception.ValidationException;
import com.pos.system.service.CategoryIngredientService;
import com.pos.system.util.JspHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/ingredients/category/create")
public class CreateCategoryIngredientServlet extends HttpServlet {
    private final CategoryIngredientService categoryIngredientService =
            CategoryIngredientService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspHelper.getPath("/ingcategory/create"))
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        CreateCategoryIngredientDto dto =
                CreateCategoryIngredientDto.of(name);

        try {
            categoryIngredientService.create(dto);
            resp.sendRedirect(req.getContextPath() + "/ingredients/categories");
        } catch (ValidationException e) {
            req.setAttribute("errors", e.getErrors());
            doGet(req, resp);
        }


    }
}
