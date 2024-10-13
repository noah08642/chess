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

//For the most part, the methods on your DAO classes will be CRUD operations that:

//Create objects in the data store
//Read objects from the data store
//Update objects already in the data store
//Delete objects from the data store
//Oftentimes, the parameters and return values of your DAO methods will be the model objects described in the previous section (UserData, GameData, and AuthData). For example, your DAO classes will certainly need to provide a method for creating new UserData objects in the data store. This method might have a signature that looks like this:

//void insertUser(UserData u) throws DataAccessException


}
