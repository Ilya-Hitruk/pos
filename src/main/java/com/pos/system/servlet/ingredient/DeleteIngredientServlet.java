package com.pos.system.servlet.ingredient;

import com.pos.system.service.IngredientService;
import com.pos.system.util.JspHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/ingredient/delete")
public class DeleteIngredientServlet extends HttpServlet {
    private final IngredientService ingredientService = IngredientService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        try {
            ingredientService.delete(id);
            resp.sendRedirect(req.getContextPath() + "/ingredients");
        } catch (IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher(JspHelper.getPath("ingredient/ingredients"));
        }
    }
}
