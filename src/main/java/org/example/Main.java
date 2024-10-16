package org.example;

import com.google.gson.Gson;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        RedisService redisService = new RedisService("localhost", 6379);
        Gson gson = new Gson();

        HashMap<String, int[]> ticketsMap = new HashMap<>();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("Select an action:");
            System.out.println("1. Generate (Redis)");
            System.out.println("2. Play (Redis)");
            System.out.println("3. Generate (HashMap)");
            System.out.println("4. Play (HashMap)");
            System.out.println("0. Exit");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    generate(scanner, redisService, gson);
                    break;
                case 2:
                    play(redisService, gson);
                    break;
                case 3:
                    generateWithHashMap(scanner, ticketsMap);
                    break;
                case 4:
                    playWithHashMap(ticketsMap);
                    break;
                case 0:
                    System.out.println("Program ending.");
                    running = false;
                    break;
                default:
                    System.out.println("Wrong choice. Please try again.");
            }
        }
        scanner.close();

    }

    public static void generate(Scanner scanner, RedisService redisService, Gson gson) {
        System.out.println("Generating...");
        System.out.println("Enter the number of tickets to generate:");
        int quantity = scanner.nextInt();
        long startTime = System.nanoTime();
        for (int i = 0; i < quantity; i++) {
            String genUUID = generateUUID();
            int[] randomNumbers = generateRandomNumbers();
            String numbersAsJson = gson.toJson(randomNumbers);

            System.out.println("Generated ticket UUID: " + genUUID);
            System.out.println("Random numbers (JSON): " + numbersAsJson);

            redisService.put("TicketNumbers_" + genUUID, numbersAsJson);
        }
        System.out.println("Generatig and saving to Radis took " + (System.nanoTime() - startTime) / 1000000000.0 + " seconds.");
    }

    public static void generateWithHashMap(Scanner scanner, HashMap<String, int[]> ticketsMap) {
        System.out.println("Generating...");
        System.out.println("Enter the number of tickets to generate:");
        int quantity = scanner.nextInt();
        long startTime = System.nanoTime();
        for (int i = 0; i < quantity; i++) {
            String genUUID = generateUUID();
            int[] randomNumbers = generateRandomNumbers();
            ticketsMap.put("TicketNumbers_" + genUUID, randomNumbers);
            System.out.println("Generated ticket UUID: " + genUUID);
            System.out.println("Random numbers: " + Arrays.toString(randomNumbers));
        }
        System.out.println("Generatig and saving to HashMap took " + (System.nanoTime() - startTime) / 1000000000.0 + " seconds.");
    }

    public static void play(RedisService redisService, Gson gson) {
        System.out.println("Playing...");
        long startTime = System.nanoTime();
        int[] luckyNumbers = generateRandomNumbers();
        System.out.println("Lucky numbers: " + Arrays.toString(luckyNumbers));
        System.out.println();

        Set<String> keys = redisService.getKeys();
        if (keys.isEmpty()) {
            System.out.println("No tickets found in Redis.");
            return;
        }
        for (String key : keys) {
            String jsonTicket = redisService.get(key);
            if (jsonTicket != null) {
                int[] ticketNumbers = gson.fromJson(jsonTicket, int[].class);

                int matchingNumbers = countMatchingNumbers(luckyNumbers, ticketNumbers);
                double prize = calculatePrize(matchingNumbers);

                System.out.println("Ticket UUID: " + key);
                System.out.println("Ticket numbers: " + Arrays.toString(ticketNumbers));
                System.out.println("Matching numbers: " + matchingNumbers);
                System.out.println("Prize: " + (prize > 0 ? prize + " EUR" : "No prize") + "\n");
            }
        }
        System.out.println("Generatig and saving to Radis took " + (System.nanoTime() - startTime) / 1000000000.0 + " seconds.");
    }

    public static void playWithHashMap(HashMap<String, int[]> ticketsMap) {
        System.out.println("Playing...");
        long startTime = System.nanoTime();
        int[] luckyNumbers = generateRandomNumbers();
        System.out.println("Lucky numbers: " + Arrays.toString(luckyNumbers));
        System.out.println();

        for (String key : ticketsMap.keySet()) {
            int[] ticketNumbers = ticketsMap.get(key);
            int matchingNumbers = countMatchingNumbers(luckyNumbers, ticketNumbers);
            double prize = calculatePrize(matchingNumbers);
            System.out.println("Ticket UUID: " + key);
            System.out.println("Ticket numbers: " + Arrays.toString(ticketNumbers));
            System.out.println("Matching numbers: " + matchingNumbers);
            System.out.println("Prize: " + (prize > 0 ? prize + " EUR" : "No prize") + "\n");
        }
        System.out.println("Generatig and saving to HashMap took " + (System.nanoTime() - startTime) / 1000000000.0 + " seconds.");
    }

    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static int[] generateRandomNumbers() {
        Random random = new Random();
        Set<Integer> numbersSet = new HashSet<>();
        while (numbersSet.size() < 5) {
            numbersSet.add(random.nextInt(35) + 1);
        }
        int[] numbers = new int[5];
        int i = 0;
        for (int number : numbersSet) {
            numbers[i++] = number;
        }
        return numbers;
    }

    public static int countMatchingNumbers(int[] luckyNumbers, int[] ticketNumbers) {
        int matching = 0;
        for (int luckyItem : luckyNumbers) {
            for (int ticketItem : ticketNumbers) {
                matching = (luckyItem == ticketItem) ? matching + 1 : matching;
            }
        }
        return matching;
    }

    public static double calculatePrize (int matchingNumbers) {
        switch (matchingNumbers) {
            case 1:
                return 0.50;
            case 2:
                return 3.00;
            case 3:
                return 15.00;
            case 4:
                return 500.00;
            case 5:
                return 5000.00;
            default:
                return 0;
        }
    }

}