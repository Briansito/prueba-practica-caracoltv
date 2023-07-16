package com.mycompany.pollapitest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author briansito
 */
public class PollApiTest {

    private static final String API_URL = "https://api.crowdsignal.com/v1";
    private static final String PARTNER_GUID = "5719aa21-c585-5aa9-42dd-00005547ef78";
    private static final String USER_CODE = "$P$BzM55aacFGir8JbSGtmXdZ7WGnKXiV1";

    public static void main(String[] args) {
        obtenerResultadosEncuesta();
    }

    public static void obtenerResultadosEncuesta() {
        try {
            // URL de la API
            URL url = new URL(API_URL);
            // Cuerpo JSON
            String jsonBody = "{"
                    + "    \"pdRequest\": {"
                    + "        \"partnerGUID\": \""+ PARTNER_GUID +"\","
                    + "        \"userCode\": \""+ USER_CODE +"\","
                    + "        \"demands\": {"
                    + "            \"demand\": {"
                    + "                \"poll\": {"
                    + "                    \"id\": \"10503173\""
                    + "                }, \"id\": \"GetPollResults\""
                    + "            }"
                    + "        }"
                    + "    }"
                    + "}";

            // Establecer la conexión
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Configurar la solicitud como POST
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            // Escribir el cuerpo JSON en la solicitud
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonBody.getBytes());
            outputStream.flush();
            outputStream.close();
            // Obtener la respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Leer la respuesta del servidor
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                //Imprimir respuesta
                //System.out.println("Response: " + response.toString());
                JSONObject json = new JSONObject(response.toString());
                JSONObject pdResponse = json.getJSONObject("pdResponse");
                JSONObject demands = pdResponse.getJSONObject("demands");
                JSONArray demand = demands.getJSONArray("demand");
                JSONObject result = demand.getJSONObject(0).getJSONObject("result");
                JSONObject answers = result.getJSONObject("answers");
                JSONArray answer = answers.getJSONArray("answer");
                // Imprimir la tabla
                System.out.format("%-20s %-10s %-10s\n", "Titulo", "Votos", "Porcentaje");
                System.out.println("----------------------------------------------");
                for (int i = 0; i < answer.length(); i++) {
                    JSONObject answerObj = answer.getJSONObject(i);
                    String text = answerObj.getString("text");
                    int total = answerObj.getInt("total");
                    int percent = answerObj.getInt("percent");
                    System.out.format("%-20s %-10d %-10d\n", text, total, percent);
                }
            } else {
                System.out.println("Request failed. Response Code: " + responseCode);
            }
            // Cerrar la conexión
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
