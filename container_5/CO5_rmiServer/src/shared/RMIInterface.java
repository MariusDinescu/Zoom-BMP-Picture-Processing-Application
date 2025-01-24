package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {
    byte[] zoomImage(byte[] imageBytes, int zoomLevel) throws RemoteException;
}