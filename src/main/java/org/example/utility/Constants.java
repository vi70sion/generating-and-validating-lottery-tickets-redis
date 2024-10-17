package org.example.utility;

public class Constants {

    public final static String URL = "jdbc:mysql://localhost:3306/ticket_queue";
    public final static String USERNAME = "root";
    public final static String PASSWORD = "*";

    private Constants() {
        throw new AssertionError("Cannot instantiate the Constants class");
    }

}
