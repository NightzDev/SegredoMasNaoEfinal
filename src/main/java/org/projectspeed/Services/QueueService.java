package org.projectspeed.Services;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;

@Service
public class QueueService {
    private static final Logger logger = LogManager.getLogger(QueueService.class);

    private LinkedBlockingQueue<JSONObject> queue;

    public QueueService() {
        queue = new LinkedBlockingQueue<>(32);
    }

    public void addToQueue(JSONObject json) {
        if (queue.size() < 32) {
            queue.offer(json);
        } else {
            logger.warn("A fila está cheia, nova tentativa será feita mais tarde [EST 2min]");
        }
    }

    public JSONObject removeFromQueue() {
        return queue.poll();
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    public int getQueueSize() {
        return queue.size();
    }
}
