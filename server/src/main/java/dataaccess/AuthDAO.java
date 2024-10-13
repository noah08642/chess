package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {

    public void insertAuth(AuthData a) throws DataAccessException;
    public AuthData getAuth(String authToken);
    public void deleteAuth(String authToken);
    public void clear();
}
