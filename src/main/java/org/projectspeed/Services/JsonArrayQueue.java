package org.projectspeed.Services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class JsonArrayQueue {
    private final QueueService queueService;

    public JsonArrayQueue(QueueService queueService) {
        this.queueService = queueService;
    }

    public void processJSONArray(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length() -1; i++) {
            try {
                JSONObject ticket = jsonArray.getJSONObject(i);
                queueService.addToQueue(ticket);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }}
