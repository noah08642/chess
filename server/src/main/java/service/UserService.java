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

        String user = request.username();
        String pass = request.password();
        String mail = request.email();

        if ((user == null) || (pass == null) || (mail == null)) {
            throw new BadRequestException();
        }

        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());

        UserData userData = new UserData(user, hashedPassword, mail);
        udb.insertUser(userData);

        String authToken = returnAuth();
        AuthData authData = new AuthData(authToken, user);
        adb.insertAuth(authData);
        return new LogRegResult(user, authToken);
    }

    public LogRegResult login(LoginRequest request) throws DataAccessException {


        String user = request.username();

        UserData userData = udb.getUser(user);
        if (!BCrypt.checkpw(request.password(), userData.password())){
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