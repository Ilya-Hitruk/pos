package com.pos.system.servlet.ingcategory;

import com.pos.system.exception.ResourceNotFoundException;
import com.pos.system.service.CategoryIngredientService;
import com.pos.system.util.JspHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/ingredients/category/delete")
public class DeleteCategoryIngredientServlet extends HttpServlet {
    private final CategoryIngredientService categoryIngredientService =
            CategoryIngredientService.getInstance();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        try {
            categoryIngredientService.delete(id);
            resp.sendRedirect(req.getContextPath() + "/ingredients/categories");
        } catch (IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher(JspHelper.getPath("error-page"))
                    .forward(req, resp);
        }
    }
}
