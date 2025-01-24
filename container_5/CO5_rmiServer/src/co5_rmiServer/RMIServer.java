package co5_rmiServer;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import shared.RMIInterface;
import java.rmi.RemoteException;

public class RMIServer {

    public static void main(String[] args) {
        try {
            // Verifcare registry daca e creat deja
            Registry registry = null;
            try {
                registry = LocateRegistry.getRegistry(1100);
                registry.list();
            } catch (RemoteException e) {
                registry = LocateRegistry.createRegistry(1100); // Creaza registry pe 1100
                System.out.println("Registry-ul RMI creat pe portul 1100.");
            }

            // Creeare instanta
            RMIInterface serverC05 = new RMIInterfaceImp();

            // inregistrare server pe ip static ptr docker
            String rmiUrl = "rmi://172.19.0.7:1100/RMIService5";
            Naming.rebind(rmiUrl, serverC05);

            System.out.println("Serverul RMI pentru C05 a fost inregistrat cu succes!");

        } catch (Exception e) {
            System.err.println("Eroare la inregistrarea serverului RMI pentru C05:");
            e.printStackTrace();
        }
    }
}
