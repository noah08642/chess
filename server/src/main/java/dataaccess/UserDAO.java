package dataaccess;

import model.UserData;

public interface UserDAO {

    void insertUser(UserData u) throws DataAccessException;
}
