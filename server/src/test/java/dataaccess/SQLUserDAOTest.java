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
}
