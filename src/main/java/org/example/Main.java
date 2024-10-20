package org.example;

import com.google.gson.Gson;
import org.example.service.RabbitMQService;
import org.example.service.RedisService;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        RedisService redisService = new RedisService("localhost", 6379);
        RabbitMQService rabbitMQService = new RabbitMQService();

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
            System.out.println("5. Generate (Redis Runnable)");
            System.out.println("6. Play (Redis Runnable)");
            System.out.println("7. Generate (MySQL Runnable)");
            System.out.println("8. Play (MySQL Runnable)");
            System.out.println("9. Generate (RabbitMQ)");
            System.out.println("10. Play (RabbitMQ)");
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
                case 5:
                    generateWithRedisRunnable(scanner, redisService, gson);
                    break;
                case 6:
                    playRunnable(redisService, gson);
                    break;
                case 7:
                    generateWithMySQLRunnable(scanner, gson);
                    break;
                case 8:
                    playWithMySQLRunnable(gson);
                    break;
                case 9:
                    generateWithRabbitMQ(scanner, rabbitMQService, gson);
                    break;
                case 10:
                    playWithRabbitMQ(scanner, rabbitMQService, gson);
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
        System.out.println("Play from Radis took " + (System.nanoTime() - startTime) / 1000000000.0 + " seconds.");
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
        System.out.println("Play from HashMap took " + (System.nanoTime() - startTime) / 1000000000.0 + " seconds.");
    }


    public static void generateWithRedisRunnable(Scanner scanner, RedisService redisService, Gson gson) {
        System.out.println("Enter the number of tickets to generate:");
        int quantity = scanner.nextInt();
        long startTime = System.nanoTime();
        TicketGeneratorRunnable generatorRunnable = new TicketGeneratorRunnable(quantity, redisService, gson);
        Thread generatorThread = new Thread(generatorRunnable);
        generatorThread.start();
        try {
            generatorThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread().getName() + " was interrupted.");
        }
        System.out.println("Generatig and saving to Redis (using Threads) took " + (System.nanoTime() - startTime) / 1000000000.0 + " seconds.");

    }


    public static void playRunnable(RedisService redisService, Gson gson) {
        long startTime = System.nanoTime();
        int portionNumber = 5;
        int[] luckyNumbers = Main.generateRandomNumbers();
        System.out.println("Lucky numbers: " + Arrays.toString(luckyNumbers));
        Set<String> keys = redisService.getKeys();
        if (keys.isEmpty()) {
            System.out.println("No tickets found in Redis.");
            return;
        }

        // Define the size of each subset
        int subsetSize = keys.size() / portionNumber;

        List<Set<String>> listOfSets = new ArrayList<>();
        Iterator<String> iterator = keys.iterator();

        // Split the set into portionNumber sets
        for (int i = 0; i < portionNumber; i++) {
            Set<String> subset = new HashSet<>();
            while (iterator.hasNext() && subset.size() < subsetSize) {
                subset.add(iterator.next());
            }
            listOfSets.add(subset);
        }

        // Thread for each portion
        List<Thread> threads = new ArrayList<>();
        for (Set<String> keySubset : listOfSets) {
            TicketsPlayRunnable checkTask = new TicketsPlayRunnable(redisService, gson, keySubset, luckyNumbers);
            Thread playRunnableThread = new Thread(checkTask);
            playRunnableThread.start();
            threads.add(playRunnableThread);
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Thread " + Thread.currentThread().getName() + " was interrupted.");
            }
        }
        System.out.println("Play from Redis (using Threads) took " + (System.nanoTime() - startTime) / 1000000000.0 + " seconds.");
    }


    public static void generateWithMySQLRunnable(Scanner scanner, Gson gson) {
        System.out.println("Enter the number of tickets to generate:");
        int quantity = scanner.nextInt();
        long startTime = System.nanoTime();
        TicketGeneratorMySQLRunnable taskProcessorRunnable = new TicketGeneratorMySQLRunnable(quantity, gson);
        Thread generatorThread = new Thread(taskProcessorRunnable);
        generatorThread.start();
        try {
            generatorThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread().getName() + " was interrupted.");
        }
        System.out.println("Generatig and saving to MySQL DB (using Threads) took " + (System.nanoTime() - startTime) / 1000000000.0 + " seconds.");

    }


    public static void playWithMySQLRunnable(Gson gson) throws InterruptedException {
        System.out.println("Playing...");
        long startTime = System.nanoTime();
        int[] luckyNumbers = generateRandomNumbers();
        System.out.println("Lucky numbers: " + Arrays.toString(luckyNumbers));
        System.out.println();

        TicketPlayMySQLRunnable checkMySQLTask1 = new TicketPlayMySQLRunnable(gson, luckyNumbers);
        //TicketPlayMySQLRunnable checkMySQLTask2 = new TicketPlayMySQLRunnable(gson, luckyNumbers);
        Thread playMySQLThread1 = new Thread(checkMySQLTask1);
        //Thread playMySQLThread2 = new Thread(checkMySQLTask2);
        playMySQLThread1.start();
        //playMySQLThread2.start();
        playMySQLThread1.join();
        //playMySQLThread2.join();
        System.out.println("Play with MySQL took " + (System.nanoTime() - startTime) / 1000000000.0 + " seconds.");
    }


    public static void generateWithRabbitMQ(Scanner scanner, RabbitMQService rabbitMQService, Gson gson) {
        System.out.println("Enter the number of tickets to generate:");
        int quantity = scanner.nextInt();
        long startTime = System.nanoTime();
        TicketsGeneratorRabbitRunnable generatorRabbitRunnable = new TicketsGeneratorRabbitRunnable(quantity, rabbitMQService, gson);
        Thread generatorThread = new Thread(generatorRabbitRunnable);
        generatorThread.start();
        try {
            generatorThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread().getName() + " was interrupted.");
        }
        System.out.println("Generatig and saving to RabbitMQ took " + (System.nanoTime() - startTime) / 1000000000.0 + " seconds.");
        rabbitMQService.close();
    }


    public static void playWithRabbitMQ(Scanner scanner, RabbitMQService rabbitMQService, Gson gson) throws Exception {
        System.out.println("Playing...");
        long startTime = System.nanoTime();
        int[] luckyNumbers = generateRandomNumbers();
        System.out.println("Lucky numbers: " + Arrays.toString(luckyNumbers));
        System.out.println();
        rabbitMQService.receiveAndProcessOneMessageAtATime("tickets_queue", luckyNumbers);

        System.out.println("Play with RabbitMQ took " + (System.nanoTime() - startTime) / 1000000000.0 + " seconds.");

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