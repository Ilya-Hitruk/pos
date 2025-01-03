package com.pos.system.servlet.ingredient;

import com.pos.system.service.IngredientService;
import com.pos.system.util.JspHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/ingredients")
public class IngredientServlet extends HttpServlet {
    private final IngredientService ingredientService = IngredientService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("ingredients", ingredientService.findAll());
        req.getRequestDispatcher(JspHelper.getPath("ingredient/ingredients"))
                .forward(req, resp);
    }
}
