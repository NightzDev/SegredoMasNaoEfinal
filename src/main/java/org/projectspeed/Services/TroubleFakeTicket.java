package org.projectspeed.Services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
public class TroubleFakeTicket {

    private static final Logger logger = LogManager.getLogger(TroubleFakeTicket.class);

    public static QueueService queueService;
    private static ThreadPoolTaskExecutor taskExecutor;

    public TroubleFakeTicket(QueueService queueService, @Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor) {
        this.queueService = queueService;
        this.taskExecutor = taskExecutor;

    }

    /**
     * Processa os tickets presentes na fila.
     */
    public static void processTicketsFromQueue() {
        for (int i = 0; i < queueService.getQueueSize(); i++) {
            CompletableFuture.supplyAsync(() -> {
                String threadName = Thread.currentThread().getName();
                String threadPoolName = Thread.currentThread().getThreadGroup().getName();
                ThreadContext.put("ThreadPool", threadPoolName);
                logger.info("Thread {} do ThreadPool {} iniciou o processamento dos tickets.", threadName, threadPoolName);

                JSONObject ticket = queueService.removeFromQueue();

                if (ticket != null) {
                    int ticketId = ticket.getInt("id");
                    if (ticketId == 1) {
                        ticket.put("Status", "Executar");
                    } else if (ticketId == 2) {
                        ticket.put("Status", "Parar");
                    } else if (ticketId == 3) {
                        ticket.put("Status", "Agilizar");
                    }
                    logger.info("Thread {}, JSON processado: {}", threadName, ticket.toString());
                } else {
                    logger.info("Thread {} não encontrou tickets para processar.", threadName);
                }

                logger.info("Thread {} finalizou o processamento dos tickets.", threadName);

                return ticket;
            }, taskExecutor);
        }
    }




}
