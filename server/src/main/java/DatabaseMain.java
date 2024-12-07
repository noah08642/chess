import dataaccess.DatabaseManager;

public class DatabaseMain {

    public static void main(String[] args) throws Exception {
        var serverUrl = "http://localhost:8080";
        DatabaseManager.createTables();
    }
}