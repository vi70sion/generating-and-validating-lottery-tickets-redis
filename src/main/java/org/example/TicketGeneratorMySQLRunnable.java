package org.example;

import com.google.gson.Gson;
import org.example.model.Ticket;

public class TicketGeneratorMySQLRunnable implements Runnable{

    TicketsRepository ticketsRepository = new TicketsRepository();
    private final int quantity;
    private final Gson gson;

    public TicketGeneratorMySQLRunnable(int quantity, Gson gson) {
        this.quantity = quantity;
        this.gson = gson;
    }

    @Override
    public void run() {
        for (int i = 0; i < quantity; i++) {
            String genUUID = Main.generateUUID();
            int[] randomNumbers = Main.generateRandomNumbers();
            Ticket ticket = new Ticket(genUUID, randomNumbers);
            String numbersAsJson = gson.toJson(randomNumbers);
            //System.out.println(Thread.currentThread().getName() + " Generated ticket UUID: " + genUUID);
            //System.out.println("Random numbers (JSON): " + numbersAsJson);
            ticketsRepository.addTicket(ticket, gson);
        }
    }


}
