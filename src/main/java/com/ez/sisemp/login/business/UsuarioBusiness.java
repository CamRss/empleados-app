package com.ez.sisemp.login.business;

import com.ez.sisemp.login.dao.UsuarioDao;
import com.ez.sisemp.login.entity.UsuarioEntity;
import com.ez.sisemp.login.model.Usuario;
import java.sql.SQLException;

public class UsuarioBusiness {

    private final UsuarioDao usuarioDao;

    public UsuarioBusiness() {
        this.usuarioDao = new UsuarioDao();
    }

    public Usuario loginBusiness(String username, String password) throws SQLException, ClassNotFoundException {
        return usuarioDao.login(username, password);
    }

    public UsuarioEntity loginBusinessJPA(String username, String password) throws ClassNotFoundException {
        return usuarioDao.loginJPA(username, password);
    }
}
