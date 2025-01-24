package co3_consumer;

public class JMSConsumerApp {
    public static void main(String[] args) {
        JMSConsumer consumer = new JMSConsumer();
        consumer.startConsuming();
    }
}