package memorydao;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.InvalidAuthException;
import dataaccess.UserDAO;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {

    private final Map<String, UserData> userDatabase;

    public MemoryUserDAO() {
        userDatabase = new HashMap<>();
    }

    public void insertUser(UserData u) throws DataAccessException {
        if (userExists(u)) {
            throw new AlreadyTakenException();
        }
        userDatabase.put(u.username(), u);
    }

    public UserData getUser(String username) throws DataAccessException {
        throwExIfInvalid(username);
        return userDatabase.get(username);
    }

    public boolean userExists(UserData u) {
        return userDatabase.containsKey(u.username());
    }


    public void clear() {
        userDatabase.clear();
    }

    public void throwExIfInvalid(String username) throws DataAccessException {
        if (!userDatabase.containsKey(username)) {
            throw new InvalidAuthException();
        }
    }
}
