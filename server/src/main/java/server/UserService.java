package server;

import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import request.RegisterRequest;
import request.LoginRequest;
import request.LogoutRequest;
import model.AuthData;
import model.UserData;
import result.LoginResult;

public class UserService {

    public LoginResult register(RegisterRequest register) {
        String user = register.username();
        String pass = register.password();
        String mail = register.email();

        UserDAO userDAO = new MemoryUserDAO();


    }



    //public AuthData register(UserData user) {}
    //public AuthData login(UserData user) {}
    //public void logout(AuthData auth) {}

}