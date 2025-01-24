package co3_consumer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class saveImage {

    private static final String C06_API_URL = "http://co6:3100/save-image";
    private static final String C01_NOTIFICATION_URL = "http://co1:8081/api/notify";

    public static String sendImage(byte[] imageData) {
        String imageLink = null;

        try {
            URL url = new URL(C06_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/octet-stream");

            try (OutputStream os = connection.getOutputStream()) {
                os.write(imageData);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Imaginea a fost trimisa cu succes la C06.");

                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    imageLink = response.toString();
                    System.out.println("Link-ul imaginii este: " + imageLink);

                    // notificare catre co1
                    notifyC01(imageLink);
                }
            } else {
                System.err.println("Eroare la trimiterea imaginii catre C06. Raspuns " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Eroare de conexiune sau trimitere a imaginii catre C06: " + e.getMessage());
        }

        return imageLink;
    }

    private static void notifyC01(String imageLink) {
        try {
            URL url = new URL(C01_NOTIFICATION_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            // Construire payload
            String jsonPayload = String.format("{\"status\": \"processed\", \"downloadLink\": \"%s\"}", imageLink);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("C01 a fost notificat cu succes.");
            } else {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.err.println("Eroare la notificarea C01. Cod raspuns: " + responseCode);
                    System.err.println("Raspuns de la C01: " + response.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Eroare la trimiterea notificarii catre C01: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        byte[] imageData = new byte[1024];
        String imageLink = sendImage(imageData);

        if (imageLink != null) {
            System.out.println("Imaginea a fost procesata cu succes, iar link-ul este: " + imageLink);
        } else {
            System.err.println("Eroare la procesarea imaginii.");
        }
    }
}
