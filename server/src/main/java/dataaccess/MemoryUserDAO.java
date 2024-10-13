package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {

    private Map<String, UserData> userDatabase;

    public MemoryUserDAO() {
        userDatabase = new HashMap<>();
    }

    public void insertUser(UserData u) throws DataAccessException {
        userDatabase.put(u.username(), u);
    }

    public UserData getUser(String username) {
        return userDatabase.get(username);
    }

    public boolean userExists(UserData u) {
        return userDatabase.containsKey(u.username());
    }

    public void deleteUser(UserData u) throws DataAccessException {
        if(!userExists(u)) {
            throw new DataAccessException("user doesn't exist");
        }
        else {
            userDatabase.remove(u.username());
        }
    }

    public void clear() {
        userDatabase.clear();
    }

    // didn't create an update method... Not sure what that will look like...


}
