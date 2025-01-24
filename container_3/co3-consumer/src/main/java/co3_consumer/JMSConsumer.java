package co3_consumer;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import co3_consumer.saveImage;


public class JMSConsumer implements MessageListener {
    private static final String BROKER_URL = "tcp://co2-broker:61616"; // Broker URL
    private static final String TOPIC_NAME = "ImageProcessingTopic"; // Topic-ul la care se trimite imaginea

    public void startConsuming() {
        try {
            // conectare la brokerul ActiveMQ
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // creare sesiune și topic
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(TOPIC_NAME);

            // consumator mesaje
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(this);

            System.out.println("Se asteapta mesaje...");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof BytesMessage) {
            try {
                BytesMessage bytesMessage = (BytesMessage) message;
                byte[] imageData = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(imageData);
                System.out.println("Mesaj primit cu imaginea.");

                // afiseaza dimensiunea imaginii
                System.out.println("Dimensiune imagine primită: " + imageData.length);

                // salveza imaginea local
                saveImageLocally(imageData, "received_image.bmp");

                // nivel zoom
                int zoomLevel = message.getIntProperty("zoom");
                System.out.println("Nivel zoom: " + zoomLevel);

                // impartirea imaginii pe jumatate
                BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));
                int width = originalImage.getWidth();
                int height = originalImage.getHeight();

                // impartire imagine pe latime
                BufferedImage part1 = originalImage.getSubimage(0, 0, width / 2, height); // stanga
                BufferedImage part2 = originalImage.getSubimage(width / 2, 0, width / 2, height); // dreapta

                // salveaza partiile pe disc
                saveImageLocally(part1, "part1.bmp");
                saveImageLocally(part2, "part2.bmp");

                // verificare dimensiune inainte de procesare
                System.out.println("Dimensiune part1 înainte de procesare: " + part1.getWidth() + "x" + part1.getHeight());
                System.out.println("Dimensiune part2 înainte de procesare: " + part2.getWidth() + "x" + part2.getHeight());

                // trimitere catre co4 si co5
                byte[] processedPart1 = RMIClient.zoomImage(toByteArray(part1), zoomLevel, false); // C04
                byte[] processedPart2 = RMIClient.zoomImage(toByteArray(part2), zoomLevel, true);  // C05

                // verificare dupa procesare a dimensiunii
                System.out.println("Dimensiune rezultat part1 procesat: " + processedPart1.length);
                System.out.println("Dimensiune rezultat part2 procesat: " + processedPart2.length);

                // asamblare imagine dupa procesare
                byte[] combinedImage = combineImages(processedPart1, processedPart2);

                // salvare imagine procesata
                saveImageLocally(combinedImage, "processed_image.bmp");
                
                saveImage.sendImage(combinedImage);
                
                System.out.println("Procesarea imaginii a fost finalizata cu succes.");
            } catch (JMSException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] combineImages(byte[] part1, byte[] part2) {
        try {
            // Reconstruire imaginii din bitii 
            BufferedImage imagePart1 = ImageIO.read(new ByteArrayInputStream(part1));
            BufferedImage imagePart2 = ImageIO.read(new ByteArrayInputStream(part2));

            // determinam dimensiuniile
            int width = imagePart1.getWidth() + imagePart2.getWidth(); // latime totala
            int height = Math.max(imagePart1.getHeight(), imagePart2.getHeight()); // inaltime

            // creare imagine noua pentru a combina cele doua parți
            BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = combinedImage.createGraphics();

            // adaugam cele 2 imag una la alta
            g2d.drawImage(imagePart1, 0, 0, null); // Desenare partea 1 la cord ( 0 0 )
            g2d.drawImage(imagePart2, imagePart1.getWidth(), 0, null); 

            g2d.dispose(); // Eliberare resurse

            // Convertire imagine in byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(combinedImage, "BMP", outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Eroare la combinarea imaginilor: " + e.getMessage());
        }
    }


    private void saveImageLocally(byte[] imageData, String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(imageData);
            System.out.println("Imagine salvata local: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveImageLocally(BufferedImage image, String filename) {
        try {
            ImageIO.write(image, "BMP", new File(filename));  // Salvare imagine in format bmp
            System.out.println("Imagine salvata local: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metoda pentru a transforma BufferedImage într-un byte array
    private byte[] toByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "BMP", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
