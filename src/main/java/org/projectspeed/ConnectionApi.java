package org.projectspeed;

import org.projectspeed.Services.JsonArrayQueue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.projectspeed.Services.TroubleFakeTicket.queueService;

public class ConnectionApi {

    private static JsonArrayQueue JsonArrayQueue;

    /**
     * Verifica se é possível fazer uma requisição GET para a SigitmFake.
     *
     * @return true se a requisição foi bem-sucedida, false caso contrário
     */
    public static boolean pingURL() {
        try {
            URL urlObj = new URL("https://sigitmfake-nightzdev.vercel.app/TroubleTicketLimit/1");
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // Tempo limite de conexão em milissegundos
            conn.connect();

            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (IOException e) {
            return false;
        }
    }


    /**

     Puxa dados falsos da URL especificada usando uma requisição GET.
     @param urlString a URL da qual recuperar os dados
     @return os dados de resposta como uma string

     */
    private static String fetchDataFromUrl(String urlString) {
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    /**
     * Busca dados de uma URL especificada e adiciona os tickets obtidos na fila.
     */
    public static void fetchDataAndAddQueue() {
        String response = fetchDataFromUrl("https://sigitmfake-nightzdev.vercel.app/TroubleTicketLimit/33");

        try {
            JSONArray ticketArray = new JSONArray(response);
            for (int i = 0; i < ticketArray.length(); i++) {
                try {
                    JSONObject ticket = ticketArray.getJSONObject(i);
                    queueService.addToQueue(ticket);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
