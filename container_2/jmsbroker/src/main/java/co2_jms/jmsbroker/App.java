package co2_jms.jmsbroker;

import org.apache.activemq.broker.BrokerService;

public class App {

    public static void initBroker(String ip, String port) throws Exception {
        BrokerService broker = new BrokerService();
        // asculta pe toate interfetele
        broker.addConnector("tcp://0.0.0.0:" + port);
        broker.start(); // Pornire
        System.out.println("ActiveMQ broker started on tcp://0.0.0.0:" + port);

        // rulare continua
        synchronized (App.class) {
            App.class.wait();
        }
    }

    public static void main(String[] args) {
        // Setăm valorile implicite pentru IP și port
        String ip = "127.0.0.1";
        String port = "61616";

        // Daca utilizatorul ofera argumente, le folosim in loc de valorile implicite
        if (args.length >= 2) {
            ip = args[0];
            port = args[1];
        } else {
            System.out.println("Using default IP and port: " + ip + ":" + port);
        }

        try {
            // Initiere broker
            initBroker(ip, port);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}
