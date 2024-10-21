package org.example;

import com.google.gson.Gson;
import org.example.service.RedisService;

import java.util.Arrays;
import java.util.Set;

public class TicketsPlayRunnable implements Runnable{

    private final RedisService redisService;
    private final Gson gson;
    private final Set<String> keys;
    private int[] luckyNumbers;

    public TicketsPlayRunnable(RedisService redisService, Gson gson, Set<String> keys, int[] luckyNumbers) {
        this.redisService = redisService;
        this.gson = gson;
        this.keys = keys;
        this.luckyNumbers = luckyNumbers;
    }

    @Override
    public void run() {
        for (String key : keys) {
            String jsonTicket = redisService.get(key);
            if (jsonTicket != null) {
                int[] ticketNumbers = gson.fromJson(jsonTicket, int[].class);

                int matchingNumbers = Main.countMatchingNumbers(luckyNumbers, ticketNumbers);
                double prize = Main.calculatePrize(matchingNumbers);

                System.out.println(Thread.currentThread().getName() + " Ticket UUID: " + key);
                System.out.println("Ticket numbers: " + Arrays.toString(ticketNumbers));
                System.out.println("Matching numbers: " + matchingNumbers);
                System.out.println("Prize: " + (prize > 0 ? prize + " EUR" : "No prize") + "\n");
            }
        }
    }


}
