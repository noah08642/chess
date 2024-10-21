package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import server.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryUserDAOTest {

    private MemoryUserDAO udb;

    @BeforeEach
    public void setUp() {
        this.udb = new MemoryUserDAO();
    }

    @Test
    public void getNonexistentUser()  {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> udb.getUser("Luke"));
        assertEquals("user is not in database", exception.getMessage());
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
