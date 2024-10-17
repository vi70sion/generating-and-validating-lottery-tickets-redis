package org.example;

import com.google.gson.Gson;
import org.example.model.Ticket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class TicketPlayMySQLRunnable implements Runnable{

    TicketsRepository ticketsRepository = new TicketsRepository();
    private final Gson gson;
    private final int[] luckyNumbers;
    ConcurrentHashMap<String, int[]> ticketMap = new ConcurrentHashMap<>();

    public TicketPlayMySQLRunnable(Gson gson, int[] luckyNumbers) {
        this.gson = gson;
        this.luckyNumbers = luckyNumbers;
    }

    @Override
    public void run() {
        boolean running = true;
        while (running) {
            Ticket ticket = ticketsRepository.getOneTicket(gson);
            if (ticket != null) {
                if (!ticketMap.containsKey(ticket.getUuidCode())) {
                    //there is no such ticket yet in mailMap
                    ticketMap.putIfAbsent(ticket.getUuidCode(),ticket.getNumbers());
                    int matchingNumbers = Main.countMatchingNumbers(luckyNumbers, ticket.getNumbers());
                    double prize = Main.calculatePrize(matchingNumbers);
//                    System.out.println(Thread.currentThread().getName() + "- Ticket UUID: " + ticket.getUuidCode());
//                    System.out.println("Ticket numbers: " + Arrays.toString(ticket.getNumbers()));
//                    System.out.println("Matching numbers: " + matchingNumbers);
//                    System.out.println("Prize: " + (prize > 0 ? prize + " EUR" : "No prize") + "\n");

                    ticketsRepository.updateTicket(ticket);
                    ticketMap.remove(ticket.getUuidCode());
                } else {
                    System.out.println(Thread.currentThread().getName() + " Ticket with ID: " + ticket.getUuidCode() + " already processing by another thread.");
                }
            } else {
                running = false;
            }
        }
    }


}
