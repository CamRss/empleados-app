package com.ez.sisemp.empleado.servlet;

import com.ez.sisemp.empleado.business.EmpleadoBusiness;
import com.ez.sisemp.empleado.entity.EmpleadoEntity;
import com.ez.sisemp.parametro.dao.ParametroDao;
import com.ez.sisemp.parametro.model.Departamento;
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
import java.util.List;

@WebServlet("/empleado/editar")
public class EditarEmpleadoServlet extends HttpServlet {

    private EmpleadoBusiness empleadoBusiness;
    private EmpleadoEntity empleadoEntity;
    private ParametroDao parametroDao;

    @Override
    public void init() throws ServletException {
        super.init();
        empleadoBusiness = new EmpleadoBusiness();
        empleadoEntity = new EmpleadoEntity();
        parametroDao = new ParametroDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtils.validarSesion(req, resp)) {

            return;

        }

        empleadoEntity.setId(Long.parseLong(req.getParameter("id")));

        loadDepartamentos(req);
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
        //creo una variable entero para iddepartamento

       int iddepartamentotipoentero ;
       String idDepartamentotipostring;

       //asigno valor para la variable iddepartamento desde el request

        idDepartamentotipostring =  request.getParameter("idDepartamento");

        //se evalua que el valor no sea nulo

        if ( idDepartamentotipostring == null) {

           iddepartamentotipoentero = 1 ;

        } else {

            iddepartamentotipoentero = Integer.parseInt(idDepartamentotipostring);

        }


        empleadoEntity.setIdDepartamento(iddepartamentotipoentero);
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

    private void loadDepartamentos(HttpServletRequest request)  {

        List<Departamento> departamentos = parametroDao.obtenerDepartamentos();

        // Reordenar la lista para que el departamento específico esté al principio
        Departamento specificDepartamento = null;
        for (Departamento dep : departamentos) {
            if (dep.id() == (Integer.valueOf(request.getParameter("iddepartamento")))) {
                specificDepartamento = dep;
                break;
            }
        }

        if (specificDepartamento != null) {
            departamentos.remove(specificDepartamento);
            departamentos.add(0, specificDepartamento);
        }

        request.setAttribute("departamentos", departamentos);
    }
}
