package com.ez.sisemp.shared.utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SessionUtils {

    private static final String LOGIN_JSP = "/login/login.jsp";

    private SessionUtils(){}

    public static boolean validarSesion(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if(request.getSession().getAttribute("user") == null){
            request.getRequestDispatcher(LOGIN_JSP).forward(request, response);
            return false;
        }
        return true;
    }
    public static boolean cerrarSesion(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Invalidar la sesión actual
        request.getSession().invalidate();

        // Redirigir a la página de inicio de sesión u otra página deseada
        response.sendRedirect(LOGIN_JSP);

        return false; // Indicar que la sesión no está válida
    }

}
