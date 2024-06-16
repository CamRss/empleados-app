package com.ez.sisemp.empleado.servlet;

import com.ez.sisemp.empleado.business.EmpleadoBusiness;
import com.ez.sisemp.empleado.entity.EmpleadoEntity;
import com.ez.sisemp.parametro.dao.ParametroDao;
import com.ez.sisemp.shared.enums.Routes;
import com.ez.sisemp.shared.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@WebServlet("/empleado/editar")
public class EditarEmpleadoServlet extends HttpServlet {

    private EmpleadoBusiness empleadoBusiness;
    private EmpleadoEntity empleadoEntity;

    @Override
    public void init() throws ServletException {
        super.init();
        empleadoBusiness = new EmpleadoBusiness();
        empleadoEntity = new EmpleadoEntity();

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        empleadoEntity.setId(Long.parseLong(req.getParameter("id")));

        req.getRequestDispatcher("/empleado/editar.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       // super.doPost(request, response);

        if (!SessionUtils.validarSesion(request, response)) {
            return;
        }

        empleadoEntity.setNombres(request.getParameter("nombres"));
        empleadoEntity.setApellidoPat(request.getParameter("apellidopat"));
        empleadoEntity.setApellidoMat(request.getParameter("apellidomat"));
        //FALTA RECUPERAR EL ID DEPARTAMENTO
        empleadoEntity.setIdDepartamento(1);

        empleadoEntity.setCorreo(request.getParameter("correo"));
        empleadoEntity.setSalario(Double.parseDouble(request.getParameter("salario")));

        Date fechaDate;

        try {
            // Crear un objeto SimpleDateFormat con el formato esperado
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

            // Parsear la cadena de fecha a un objeto Date
            fechaDate = formato.parse(request.getParameter("fecha_nacimiento"));

            empleadoEntity.setFechaNacimiento(fechaDate);

        } catch (Exception e) {
            //Solo para que el dato no esté vacio
            empleadoEntity.setFechaNacimiento(Date.from(Instant.now()));
        }

        empleadoBusiness.editarEmpleadoJPA(empleadoEntity);
        response.sendRedirect(Routes.EMPLEADO.getRoute());

    }
}
