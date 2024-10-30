package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTest {

    private SQLUserDAO udb;

    @BeforeEach
    public void setUp() throws DataAccessException {
        this.udb = new SQLUserDAO();
        udb.clear();
    }

    @Test
    public void getNonexistentUser() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> udb.getUser("Luke"));
        assertEquals("Error: unauthorized", exception.getMessage());
    }


    @Test
    public void getExistingUser() throws DataAccessException {
        UserData u1 = new UserData("a", "b", "c");
        udb.insertUser(u1);
        UserData u2 = udb.getUser("a");

        assertNotNull(u2);
        assertEquals(u1, u2);
    }

    @Test
    public void insertGood() throws DataAccessException {
        UserData u1 = new UserData("a", "b", "c");
        udb.insertUser(u1);
        assertNotNull(u1);
    }

    @Test
    public void insertBad() throws DataAccessException {
        UserData u1 = new UserData("a", "b", "c");
        UserData u2 = new UserData("a", "b", "c");

        udb.insertUser(u1);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> udb.insertUser(u2));
        assertEquals("Error: already taken", exception.getMessage());
    }

    @Test
    public void clearGood() throws DataAccessException {
        UserData u1 = new UserData("a", "b", "c");
        UserData u2 = new UserData("d", "e", "f");

        udb.insertUser(u1);
        udb.insertUser(u2);

        udb.clear();

        DataAccessException exception = assertThrows(DataAccessException.class, () -> udb.getUser("a"));
        assertEquals("Error: unauthorized", exception.getMessage());
    }
}
