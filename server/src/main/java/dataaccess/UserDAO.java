package dataaccess;

import model.UserData;

public interface UserDAO {

    void insertUser(UserData u) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    boolean userExists(UserData u);

    void deleteUser(UserData u) throws DataAccessException;

    void clear();

    void throwExIfInvalid(String username) throws DataAccessException;

}
