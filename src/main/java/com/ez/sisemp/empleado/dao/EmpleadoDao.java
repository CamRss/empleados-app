package com.ez.sisemp.empleado.dao;

import com.ez.sisemp.empleado.entity.EmpleadoEntity;
import com.ez.sisemp.empleado.model.Empleado;
import com.ez.sisemp.shared.config.MySQLConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDao {
    private static final String SQL_GET_ALL_EMPLEADOS = """
            SELECT 
                e.id, 
                e.codigo_empleado, 
                e.nombres, 
                e.apellido_pat, 
                e.apellido_mat, 
                d.nombre AS departamento, 
                e.correo, 
                FLOOR(DATEDIFF(NOW(), e.fecha_nacimiento) / 365.25) AS edad, 
                e.salario 
            FROM 
                empleado e
            INNER JOIN departamentos d ON d.id = e.id_departamento  
            WHERE 
                e.activo = 1;
            """;

    private static final String SQL_GET_ALL_EMPLEADOS_JPQL = """
            Select  e
            from EmpleadoEntity e
            """;

    private static String SQL_UPDATE_EMPLEADO = "UPDATE empleado SET nombres = ?, apellido_pat = ?, apellido_mat = ?, id_departamento = ?, correo = ?, salario = ? WHERE id = ?;";
    private static String SQL_DELETE_EMPLEADO = "UPDATE empleado set activo=0 WHERE id = ?;";
    private static String SQL_INSERT_EMPLEADO = "INSERT INTO empleado (codigo_empleado, nombres, apellido_pat, apellido_mat, id_departamento, correo, fecha_nacimiento, salario) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
    private static String SQL_GET_NEW_EMPLEADO_CODE = "SELECT CONCAT('EMP', LPAD(MAX(CAST(SUBSTRING(codigo_empleado, 4) AS UNSIGNED)) + 1, 4, '0')) AS next_emp_code FROM empleado;";

    public List<Empleado> obtenerEmpleados() throws SQLException, ClassNotFoundException {
        List<Empleado> empleados = new ArrayList<>();
        PreparedStatement preparedStatement = MySQLConnection.getConnection()
                .prepareStatement(SQL_GET_ALL_EMPLEADOS);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            empleados.add(mapResultSetToEmpleado(resultSet));
        }
        return empleados;
    }


    public List<EmpleadoEntity> obtenerEmpleadosJPA() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devUnit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return entityManager.createQuery(SQL_GET_ALL_EMPLEADOS_JPQL, EmpleadoEntity.class).getResultList();
    }


    public void editarEmpleado(Empleado empleado) throws SQLException, ClassNotFoundException {
        //TODO: Implementar la edición de un empleado
    }

    public void eliminarEmpleado(int id) throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(SQL_DELETE_EMPLEADO);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    public void editarEmpleadoJPA(EmpleadoEntity empleadoEntity) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devUnit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // Busca el empleado por ID
        EmpleadoEntity empleado = entityManager.find(EmpleadoEntity.class, empleadoEntity.getId());

        if (empleado != null) {
            // Inicia una transacción
            entityManager.getTransaction().begin();

            // Actualiza los campos del empleado
            empleado.setNombres(empleadoEntity.getNombres());
            empleado.setApellidoPat(empleadoEntity.getApellidoPat());
            empleado.setApellidoMat(empleadoEntity.getApellidoMat());
            //FALTA EL IDDEPARTAMENTO
            empleado.setIdDepartamento(empleadoEntity.getIdDepartamento());

            empleado.setCorreo(empleadoEntity.getCorreo());
            empleado.setSalario(empleadoEntity.getSalario());
            empleado.setFechaNacimiento(empleadoEntity.getFechaNacimiento());


            // Commit de la transacción
            entityManager.getTransaction().commit();

            // Cerrar el entity manager y el factory
            entityManager.close();
            entityManagerFactory.close();

        } else {
            // Manejo del caso en que el empleado no existe
            //System.out.println("No se encontró un empleado con el ID proporcionado.");
        }


    }

    public void eliminarEmpleadoJPA(int id)   {

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devUnit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        // Busca el empleado por ID
        EmpleadoEntity empleado = entityManager.find(EmpleadoEntity.class, id);
        // Inicia una transacción
        entityManager.getTransaction().begin();
        // Elimina la entidad
        entityManager.remove(empleado);
        // Commit de la transacción
        entityManager.getTransaction().commit();

        // Cerrar el entity manager y el factory
        entityManager.close();
        entityManagerFactory.close();

    }


    public void agregarEmpleado(Empleado empleado) throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(SQL_INSERT_EMPLEADO);
        preparedStatement.setString(1, empleado.codigoEmpleado());
        preparedStatement.setString(2, empleado.nombres());
        preparedStatement.setString(3, empleado.apellidoPat());
        preparedStatement.setString(4, empleado.apellidoMat());
        preparedStatement.setInt(5, empleado.idDepartamento());
        preparedStatement.setString(6, empleado.correo());
        preparedStatement.setDate(7, new Date(empleado.fechaNacimiento().getTime()));
        preparedStatement.setDouble(8, empleado.salario());
        preparedStatement.executeUpdate();
    }

    //JPA

    public void agregarEmpleadoJPA(EmpleadoEntity empleadoEntity) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devUnit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // Inicia una transacción
        entityManager.getTransaction().begin();
        // Persistir la entidad
        entityManager.persist(empleadoEntity);
        // Commit de la transacción
        entityManager.getTransaction().commit();
        // Cerrar el entity manager y el factory
        entityManager.close();
        entityManagerFactory.close();

    }


    private Empleado mapResultSetToEmpleado(ResultSet resultSet) throws SQLException {
        return new Empleado(resultSet.getInt("id"),
                resultSet.getString("codigo_empleado"),
                resultSet.getString("nombres"),
                resultSet.getString("apellido_pat"),
                resultSet.getString("apellido_mat"),
                resultSet.getString("departamento"),
                resultSet.getString("correo"),
                resultSet.getInt("edad"),
                resultSet.getDouble("salario")
        );
    }
}
