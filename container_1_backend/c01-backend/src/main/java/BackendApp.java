import io.javalin.Javalin;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.io.IOException;

public class BackendApp {

    private static final String BROKER_URL = "tcp://co2-broker:61616"; // Adresa brokerului JMS

    private static String processedImageLink = "";  // salvare imagine link

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.enableCorsForAllOrigins();
        }).start(8081);

        // Endpoint fisier BMP
        app.post("/api/upload", BackendApp::handleFileUpload);

        // Endpoint notificare co3
        app.post("/api/notify", BackendApp::handleNotification);

     // Endpoint pentru a obține link-ul imaginii procesate
        app.get("/api/processed-image", BackendApp::getProcessedImageLink);

        // Endpoint status server
        app.get("/api/status", ctx -> ctx.result("Server is running"));
    }

    private static void handleFileUpload(Context ctx) {
        try {
            // Preia fisier incarcat
            UploadedFile uploadedFile = ctx.uploadedFile("file");

            // se obține valoarea zoom-ului
            String zoomParam = ctx.formParam("zoom");

            // Verificare fisier si zoom
            if (uploadedFile == null) {
                ctx.status(400).json("{\"message\": \"Fișierul nu a fost încărcat!\"}");
                return;
            }

            if (zoomParam == null || zoomParam.isEmpty()) {
                ctx.status(400).json("{\"message\": \"Nivelul de zoom nu a fost furnizat!\"}");
                return;
            }

            int zoom;
            try {
                zoom = Integer.parseInt(zoomParam);
            } catch (NumberFormatException e) {
                ctx.status(400).json("{\"message\": \"Nivelul de zoom trebuie sa fie un numar valid!\"}");
                return;
            }

            // Convertim fisierul in byte array
            byte[] imageBytes = uploadedFile.getContent().readAllBytes();

            // publica fisierul in JMS Topic
            publishToJmsTopic(imageBytes, zoom);

            ctx.status(200).json("{\"message\": \"Fisierul a fost procesat si trimis in JMS Broker!\"}");
        } catch (IOException e) {
            e.printStackTrace();
            ctx.status(500).json("{\"message\": \"Eroare la citirea fisierului: " + e.getMessage() + "\"}");
        } catch (JMSException e) {
            e.printStackTrace();
            ctx.status(500).json("{\"message\": \"Eroare la publicarea in JMS: " + e.getMessage() + "\"}");
        }
    }
    
    private static void getProcessedImageLink(Context ctx) {
        if (processedImageLink.isEmpty()) {
            ctx.status(404).json("{\"message\": \"Imaginea nu a fost procesata inca.\"}");
        } else {
            ctx.status(200).json("{\"imageLink\": \"" + processedImageLink + "\"}");
        }
    }

    private static void handleNotification(Context ctx) {
        String body = ctx.body();

        JsonObject jsonObject = new JsonParser().parse(body).getAsJsonObject();
        String imageLink = jsonObject.get("downloadLink").getAsString();

        if (imageLink == null || imageLink.isEmpty()) {
            ctx.status(400).json("{\"message\": \"Link-ul imaginii nu a fost furnizat!\"}");
            return;
        }

        System.out.println("Imagine procesata: " + imageLink);
        processedImageLink = imageLink;

        ctx.status(200).json("{\"message\": \"Notificare procesata cu succes!\", \"imageLink\": \"" + imageLink + "\"}");
    }

    private static void publishToJmsTopic(byte[] imageBytes, int zoom) throws JMSException {
        Connection connection = null;
        Session session = null;

        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);

            // conexiune si sesiune
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // topic
            Destination destination = session.createTopic("ImageProcessingTopic");
            MessageProducer producer = session.createProducer(destination);

            // mesaj binar
            BytesMessage message = session.createBytesMessage();
            message.writeBytes(imageBytes);
            message.setIntProperty("zoom", zoom);

            // se trimite mesajul in topic
            producer.send(message);
            System.out.println("Mesaj trimis in JMS Topic: ImageProcessingTopic");
        } finally {
            // inchidere resurse
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
