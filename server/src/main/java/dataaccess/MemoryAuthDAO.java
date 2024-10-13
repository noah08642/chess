package dataaccess;
import model.AuthData;

import java.util.*;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {

    private Map<String, AuthData> authDatabase;

    MemoryAuthDAO() {
        authDatabase = new HashMap<>();
    }

    public void insertAuth(AuthData a) throws DataAccessException{
        authDatabase.put(a.authToken(), a);
    }

    public AuthData getAuth(String authToken) {
        return authDatabase.get(authToken);
    }

    public void deleteAuth(String authToken) {
        authDatabase.remove(authToken);
    }

    public void clear() {
        authDatabase.clear();
    }




    //createAuth: Create a new authorization.
    //        getAuth: Retrieve an authorization given an authToken.
   // deleteAuth: Delete an authorization so that it is no longer valid.
}
