package org.projectspeed.Controller;

import de.codecentric.boot.admin.server.domain.values.StatusInfo;
import org.projectspeed.Config.AsyncConfig;
import org.projectspeed.ConnectionApi;
import org.projectspeed.Services.FactorialService;
import org.projectspeed.Services.TroubleFakeTicket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@EnableScheduling
public class BaseController {
    private static final Logger logger = LogManager.getLogger(BaseController.class);

    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private FactorialService factorialService;

    @GetMapping("/factorial/{number}")
    public CompletableFuture<Long> calculateFactorial(@PathVariable int number) {
        return factorialService.calculateFactorial(number).thenApply(BigInteger::longValue);
    }

    @GetMapping("/stress-test/{count}/{number}")
    public String stressTest(@PathVariable String count, @PathVariable String number) {
        int countValue = Integer.parseInt(count);
        int numberValue = Integer.parseInt(number);
        long startTime = System.nanoTime();
        List<CompletableFuture<BigInteger>> futures = new ArrayList<>();

        for (int i = 0; i < countValue; i++) {

            CompletableFuture<BigInteger> factorialFuture = CompletableFuture.supplyAsync(() -> {
                BigInteger partialFactorial = FactorialService.calculatePartialFactorial(numberValue);
                String threadName = Thread.currentThread().getName();
                String threadPoolName = Thread.currentThread().getThreadGroup().getName();
                logger.info("Thread {} do ThreadPool {} iniciou o processamento dos tickets.", threadName, threadPoolName);

                System.out.println("Partial factorial for " + numberValue + ": " + partialFactorial);
                return partialFactorial;
            }, taskExecutor);
            futures.add(factorialFuture);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.join();

        long endTime = System.nanoTime();
        long elapsedTime = (endTime - startTime) / 1000000;

        return elapsedTime + "ms";
    }

    @GetMapping("/stress-test-noThreads/{count}/{number}")
    public String stressTest_noThreads(@PathVariable int count, @PathVariable int number) {
        long startTime = System.nanoTime();

        for (int i = 0; i < count; i++) {
            BigInteger factorial = FactorialService.NoThreadcalculateFactorial(number).join();
            long threadId = Thread.currentThread().getId();
            logger.info("Thread #" + threadId + " is doing this task");
            logger.info("Fatorial de \" + number + \": \" + factorial");
        }

        long endTime = System.nanoTime();
        long elapsedTime = (endTime - startTime) / 1000000;

        return elapsedTime + "ms";
    }

    @GetMapping("/Encaminhar")
    @Scheduled(fixedDelay = 10000)
    @Async
    public void manageTicket(){
        logger.info("Coletando dados e adicionando na fila");
        ConnectionApi.fetchDataAndAddQueue();
        System.out.println("================");
        TroubleFakeTicket.processTicketsFromQueue();
        System.out.println("================");
    }


    //TODO: STATUS dar uma renovadinha hehehe...
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        //Bagunçado!
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        int numberThread = AsyncConfig.numIdealThreadPoolSizeAutomatic();
        boolean isUrlOnline = ConnectionApi.pingURL();
        String resultPing = isUrlOnline ? "API is online" : "API is offline";
        int maxThreads = taskExecutor.getMaxPoolSize();
        long usedMemory = totalMemory - freeMemory;
        long usedMemoryMB = usedMemory / (1024 * 1024);
        //====================================

        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("numberThread", numberThread);
        statusMap.put("maxThreads", maxThreads);
        statusMap.put("UsedMemory", usedMemoryMB);
        statusMap.put("resultPing", resultPing);


        return ResponseEntity.ok(statusMap);
    }





}
