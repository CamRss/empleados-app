package com.ez.sisemp.login.servlet;

import com.ez.sisemp.login.business.UsuarioBusiness;
import com.ez.sisemp.login.entity.UsuarioEntity;
import com.ez.sisemp.login.enumeration.Roles;
import com.ez.sisemp.login.exception.UserOrPassIncorrectException;
import com.ez.sisemp.shared.enums.Routes;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String LOGIN_JSP = "/login/login.jsp";
    private static final String ERROR_INCORRECT_CREDENTIALS = "Usuario y/o contraseña incorrectos";
    private static final String ERROR_SERVER = "Error interno en el servidor";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(LOGIN_JSP);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        UsuarioBusiness usuarioBusiness = new UsuarioBusiness();
        try {

            //Llamada por JDBC
            //Usuario usuario = usuarioBusiness.loginBusiness(request.getParameter("username"), request.getParameter("password"));

            //Llamada por JPA
            UsuarioEntity usuarioEntity = usuarioBusiness.loginBusinessJPA(request.getParameter("username"), request.getParameter("password"));

            HttpSession session = request.getSession();

            //Pasando el atributo del JDBC
            //session.setAttribute("user", usuario);

            //Pasando el atributo del JPA
            session.setAttribute("user", usuarioEntity);

            //Redirección de rutas de la respuesta del JDBC

            /*if (usuario.rolId() == Roles.ADMIN.getId()) {
                response.sendRedirect(Routes.ADMIN.getRoute());
            } else {
                response.sendRedirect(Routes.EMPLEADO.getRoute());
            }*/

            //Redirección de rutas de la respuesta del JPA

            if (usuarioEntity.getIdRol() == Roles.ADMIN.getId()) {
                response.sendRedirect(Routes.ADMIN.getRoute());
            } else {
                response.sendRedirect(Routes.EMPLEADO.getRoute());
            }

        } catch (UserOrPassIncorrectException e) {
            request.setAttribute("msj", ERROR_INCORRECT_CREDENTIALS);
            request.setAttribute("detail", e.getMessage());
            request.getRequestDispatcher(LOGIN_JSP).forward(request, response);
        } catch (Exception e) {
            throw new ServletException(ERROR_SERVER, e);
        }
    }
}
