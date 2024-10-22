package dataaccess;

import java.util.UUID;

public class AuthGenerator {

    public String generate() {
        return UUID.randomUUID().toString();
    }
}
