package co5_rmiServer;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import shared.RMIInterface;

public class RMIInterfaceImp extends UnicastRemoteObject implements RMIInterface {

    // Constructor care lanseaza RemoteException
    public RMIInterfaceImp() throws RemoteException {
        super();
    }

    @Override
    public byte[] zoomImage(byte[] imageBytes, int zoomLevel) throws RemoteException {
        System.out.println("Se proceseaza imaginea cu nivelul de zoom: " + zoomLevel);

        BufferedImage originalImage = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
            originalImage = ImageIO.read(byteArrayInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Eroare la citirea imaginii.", e);
        }

        double scale = (zoomLevel / 100.0);
        int newWidth = (int) (originalImage.getWidth() * scale);
        int newHeight = (int) (originalImage.getHeight() * scale);

        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage zoomedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = zoomedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(zoomedImage, "png", byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Eroare la salvarea imaginii zoom.", e);
        }

        return byteArrayOutputStream.toByteArray();
    }
}