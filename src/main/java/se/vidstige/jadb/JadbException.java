package se.vidstige.jadb;

import java.io.Serial;

public class JadbException extends Exception {

    public JadbException(String message) {
        super(message);
    }

    @Serial
    private static final long serialVersionUID = -3879283786835654165L;
}
