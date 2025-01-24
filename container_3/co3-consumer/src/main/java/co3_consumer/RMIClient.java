package co3_consumer;

import shared.RMIInterface;
import java.rmi.Naming;

public class RMIClient {

    private static final String RMI_URL_C04 = "rmi://co4-rmi:1099/RMIService4";  // URL static pentru C04   
    private static final String RMI_URL_C05 = "rmi://co5-rmi:1100/RMIService5";  // URL static pentru C05      

    public static byte[] zoomImage(byte[] imageBytes, int zoomLevel, boolean isC05) {
        byte[] result = imageBytes;

        try {
            String rmiUrl = isC05 ? RMI_URL_C05 : RMI_URL_C04;
            System.out.println("Conectare la " + (isC05 ? "RMIService5" : "RMIService4") + "...");
            RMIInterface rmiService = (RMIInterface) Naming.lookup(rmiUrl);
            result = rmiService.zoomImage(imageBytes, zoomLevel);
            System.out.println("Procesare completata pe " + (isC05 ? "C05" : "C04") + ".");
        } catch (Exception e) {
            System.err.println("Eroare de conectarea la " + (isC05 ? "C05" : "C04") + ":");
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) {
        try {
            byte[] dummyImage = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}; // Exemplu de imagine dummy      
            int zoomLevel = 120;

            // impartirea imaginii
            byte[] part1 = new byte[]{0, 1, 2, 3, 4};  // Partea 1 a imaginii
            byte[] part2 = new byte[]{5, 6, 7, 8, 9};  // Partea 2 a imaginii

            // trimiterea pe bucatii
            byte[] resultPart1 = zoomImage(part1, zoomLevel, false);  // Trimite la C04
            byte[] resultPart2 = zoomImage(part2, zoomLevel, true);   // Trimite la C05

            // reconstruirea imaginii
            byte[] combinedImage = combineImages(resultPart1, resultPart2);
            System.out.println("Procesarea imaginii a fost finalizata. Dimensiune rezultat: " + combinedImage.length);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] combineImages(byte[] part1, byte[] part2) {
        byte[] combinedImage = new byte[part1.length + part2.length];
        System.arraycopy(part1, 0, combinedImage, 0, part1.length);
        System.arraycopy(part2, 0, combinedImage, part1.length, part2.length);
        return combinedImage;
    }
}