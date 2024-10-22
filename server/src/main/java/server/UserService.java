package server;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LogRegResult;


public class UserService {
    MemoryUserDAO udb;
    MemoryAuthDAO adb;

    public UserService(MemoryUserDAO udb, MemoryAuthDAO adb) {
        this.udb = udb;
        this.adb = adb;
    }


    public LogRegResult register(RegisterRequest request) throws DataAccessException {
        String user = request.username();
        String pass = request.password();
        String mail = request.email();

        if((user==null) || (pass==null) || (mail==null)) {
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
        String user = request.username();
        String pass = request.password();


        UserData userData = udb.getUser(user);
        if(!userData.password().equals(pass)) {
            throw new InvalidAuthException();
        }

        String authToken = returnAuth();
        AuthData authData = new AuthData(authToken, user);
        adb.insertAuth(authData);
        return new LogRegResult(user, authToken);
    }

    public void logout(LogoutRequest request) throws DataAccessException{
        adb.deleteAuth(request.authToken());
    }

    public String returnAuth() {
        AuthGenerator a = new AuthGenerator();
        return a.generate();
    }




    //public AuthData register(UserData user) {}
    //public AuthData login(UserData user) {}
    //public void logout(AuthData auth) {}

}