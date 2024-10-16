package org.example;

import com.google.gson.Gson;

public class TicketGeneratorRunnable implements Runnable{

    private final int quantity;
    private final RedisService redisService;
    private final Gson gson;

    public TicketGeneratorRunnable(int quantity, RedisService redisService, Gson gson) {
        this.quantity = quantity;
        this.redisService = redisService;
        this.gson = gson;
    }

    @Override
    public void run() {
        for (int i = 0; i < quantity; i++) {
            String genUUID = Main.generateUUID();
            int[] randomNumbers = Main.generateRandomNumbers();
            String numbersAsJson = gson.toJson(randomNumbers);
            System.out.println(Thread.currentThread().getName() + " Generated ticket UUID: " + genUUID);
            System.out.println("Random numbers (JSON): " + numbersAsJson);
            redisService.put("TicketNumbers_" + genUUID, numbersAsJson);
        }
    }
}
