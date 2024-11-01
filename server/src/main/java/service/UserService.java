package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LogRegResult;


public class UserService {
    SQLUserDAO udb;
    SQLAuthDAO adb;

    public UserService(SQLUserDAO udb, SQLAuthDAO adb) {
        this.udb = udb;
        this.adb = adb;
    }


    public LogRegResult register(RegisterRequest request) throws DataAccessException {

        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());


        String user = request.username();
        String pass = hashedPassword;
        String mail = request.email();

        if ((user == null) || (pass == null) || (mail == null)) {
            throw new BadRequestException();
        }

        UserData userData = new UserData(user, pass, mail);
        udb.insertUser(userData);

        String authToken = returnAuth();
        AuthData authData = new AuthData(authToken, user);
        adb.insertAuth(authData);
        return new LogRegResult(user, authToken);
    }

    public LogRegResult login(LoginRequest request) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());

        String user = request.username();
        String pass = hashedPassword;


        UserData userData = udb.getUser(user);
        if (!userData.password().equals(pass)) {
            throw new InvalidAuthException();
        }

        String authToken = returnAuth();
        AuthData authData = new AuthData(authToken, user);
        adb.insertAuth(authData);
        return new LogRegResult(user, authToken);
    }

    public void logout(LogoutRequest request) throws DataAccessException {
        adb.deleteAuth(request.authToken());
    }

    private String returnAuth() {
        AuthGenerator a = new AuthGenerator();
        return a.generate();
    }


}