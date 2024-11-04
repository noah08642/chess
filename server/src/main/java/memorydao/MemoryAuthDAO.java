package memorydao;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.InvalidAuthException;
import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {

    private final Map<String, AuthData> authDatabase;

    public MemoryAuthDAO() {
        authDatabase = new HashMap<>();
    }

    public void insertAuth(AuthData a) throws DataAccessException {
        authDatabase.put(a.authToken(), a);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        throwExIfInvalid(authToken);
        return authDatabase.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        throwExIfInvalid(authToken);
        authDatabase.remove(authToken);
    }

    public void clear() {
        authDatabase.clear();
    }

    public void throwExIfInvalid(String authToken) throws DataAccessException {
        if (!authDatabase.containsKey(authToken)) {
            throw new InvalidAuthException();
        }
    }
}
