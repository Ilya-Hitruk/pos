package com.pos.system.servlet.ingcategory;

import com.pos.system.service.CategoryIngredientService;
import com.pos.system.util.JspHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/ingredients/categories")
public class CategoriesServlet extends HttpServlet {
    private final CategoryIngredientService categoryIngredientService =
            CategoryIngredientService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("categories", categoryIngredientService.findAll());
        req.getRequestDispatcher(JspHelper.getPath("/ingcategory/categories"))
                .forward(req, resp);
    }
}
