package org.example.model;

import java.util.ArrayList;

public class Ticket {
    private String uuidCode;
    private int[] numbers;

    public Ticket(String uuidCode, int[] numbers) {
        this.uuidCode = uuidCode;
        this.numbers = numbers;
    }

    public String getUuidCode() {
        return uuidCode;
    }

    public void setUuidCode(String uuidCode) {
        this.uuidCode = uuidCode;
    }

    public int[] getNumbers() {
        return numbers;
    }

    public void setNumbers(int[] numbers) {
        this.numbers = numbers;
    }
}
