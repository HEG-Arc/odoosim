package ch.hearc.ig.tb.odoosim.business;

public class Demand {
    private Consumer consumer;
    private int quantity;

    public Demand(Consumer consumer, int quantity) {
        this.consumer = consumer;
        this.quantity = quantity;
    }
    
    @Override
    public String toString(){
        return "(" + String.valueOf(consumer.getIdentifier()) + ") " + 
                consumer.getName() + " : " + String.valueOf(quantity);
    }
    
}