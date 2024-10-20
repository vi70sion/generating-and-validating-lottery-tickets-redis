package org.example;

import com.google.gson.Gson;
import org.example.model.Ticket;
import org.example.service.RabbitMQService;

public class TicketsGeneratorRabbitRunnable implements Runnable{

    RabbitMQService rabbitMQService;
    private final int quantity;
    private final Gson gson;

    public TicketsGeneratorRabbitRunnable(int quantity, RabbitMQService rabbitMQService, Gson gson) {
        this.quantity = quantity;
        this.rabbitMQService = rabbitMQService;
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
            try {
                rabbitMQService.sendObjectToQueue(new Ticket(genUUID, randomNumbers));
            } catch (Exception e) {
                System.out.println("Error.");;
            }
        }
    }


}
