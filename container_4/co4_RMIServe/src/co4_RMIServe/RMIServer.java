package co4_RMIServe;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import shared.RMIInterface;

public class RMIServer {

    public static void main(String[] args) {
        try {
            // Verificare registry
            Registry registry = null;
            try {
                registry = LocateRegistry.getRegistry(1099);
                registry.list();
            } catch (RemoteException e) {
                // creare registry
                registry = LocateRegistry.createRegistry(1099);
                System.out.println("Registry-ul RMI creat pe portul 1099.");
            }

            // Creează instanța serverului RMI
            RMIInterface serverC04 = new RMIInterfaceImpl();

            // inregistrare server
            String rmiUrl = "rmi://172.19.0.6:1099/RMIService4";
            Naming.rebind(rmiUrl, serverC04);

            System.out.println("Serverul RMI pentru C04 a fost inregistrat cu succes!");

        } catch (Exception e) {
            System.err.println("Eroare la inregistrarea serverului RMI pentru C04:");
            e.printStackTrace();
        }
    }
}

