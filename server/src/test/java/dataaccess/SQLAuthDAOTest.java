package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTest {

    private SQLAuthDAO adb;

    @BeforeEach
    public void setUp() throws DataAccessException {
        this.adb = new SQLAuthDAO();
        adb.clear();
    }

    @Test
    public void getNonexistentAuth() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> adb.getAuth("luke"));
        assertEquals("Error: unauthorized", exception.getMessage());
    }


    @Test
    public void getExistingAuth() throws DataAccessException {
        AuthData a1 = new AuthData("blah", "lukerichards8");
        adb.insertAuth(a1);
        AuthData a2 = adb.getAuth("blah");

        assertNotNull(a2);
        assertEquals(a1, a2);
    }

    @Test
    public void insertGood() throws DataAccessException {
        AuthData a1 = new AuthData("blah", "lukerichards8");
        adb.insertAuth(a1);
        assertNotNull(a1);
    }

    @Test
    public void insertBad() throws DataAccessException {
        AuthData a1 = new AuthData("blah", "lukerichards8");
        AuthData a2 = new AuthData("blah", "lukerichards8");

        adb.insertAuth(a1);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> adb.insertAuth(a2));
        assertEquals("Error: already taken", exception.getMessage());
    }

    @Test
    public void deleteGood() throws DataAccessException {
        AuthData a1 = new AuthData("blah", "lukerichards8");
        adb.insertAuth(a1);
        adb.deleteAuth(a1.authToken());
        DataAccessException exception = assertThrows(DataAccessException.class, () -> adb.getAuth("a"));
        assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    public void deleteBad() throws DataAccessException {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> adb.deleteAuth("a"));
        assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    public void clearGood() throws DataAccessException {
        AuthData a1 = new AuthData("a", "b");
        AuthData a2 = new AuthData("d", "e");

        adb.insertAuth(a1);
        adb.insertAuth(a2);

        adb.clear();

        DataAccessException exception = assertThrows(DataAccessException.class, () -> adb.getAuth("a"));
        assertEquals("Error: unauthorized", exception.getMessage());
    }
}
