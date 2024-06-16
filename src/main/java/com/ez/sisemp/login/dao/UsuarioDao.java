package com.ez.sisemp.login.dao;

import com.ez.sisemp.login.entity.UsuarioEntity;
import com.ez.sisemp.login.exception.UserOrPassIncorrectException;
import com.ez.sisemp.login.model.Usuario;
import com.ez.sisemp.shared.config.MySQLConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UsuarioDao {


    private final String SQL_GET_USER_JPA ="SELECT u FROM UsuarioEntity u WHERE u.nombreUsuario = :nombreUsuario AND u.contrasena = :contrasena";

    public UsuarioEntity loginJPA(String nombreUsuario, String contrasena) {
        //Persistencia de datos
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devUnit");
        //Manegador
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //Consulta

        // Crea la consulta con parámetros
        TypedQuery<UsuarioEntity> query = entityManager.createQuery(SQL_GET_USER_JPA, UsuarioEntity.class);

        // Establece los valores de los parámetros
        query.setParameter("nombreUsuario", nombreUsuario);
        query.setParameter("contrasena", contrasena);

        //Primera forma
        List<UsuarioEntity> listUsuarioEntity;
        UsuarioEntity user;
        listUsuarioEntity =  query.getResultList();
        user = listUsuarioEntity.get(0);
        return user;

        //Segunda forma
        //return query.getResultList().get(0);

    }


    private static final String SQL_GET_USER = "SELECT * FROM usuario WHERE nombre_usuario = ? AND contrasena = ?";

    public Usuario login(String username, String password) throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = MySQLConnection.getConnection()
                .prepareStatement(SQL_GET_USER);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return mapResultSetToUsuario(resultSet);
        } else {
            throw new UserOrPassIncorrectException("Usuario o contraseña incorrectos");
        }
    }


    private Usuario mapResultSetToUsuario(ResultSet resultSet) throws SQLException {
        return new Usuario(resultSet.getInt("id"),
                resultSet.getString("nombre_usuario"),
                resultSet.getString("contrasena"),
                resultSet.getString("primer_nombre"),
                resultSet.getString("apellido_pat"),
                resultSet.getString("foto_perfil"),
                resultSet.getInt("id_rol")
        );
    }
}
