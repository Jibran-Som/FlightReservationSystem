//CreditCardPayment.java
// Implements PaymentStrategy using credit card payments --> Concrete Strategy

package model;

public class CreditCardPayment implements PaymentStrategy {

    private Card card; 
    private static final double SUCCESS_RATE = 0.9;

    // === Constructors ===
    public CreditCardPayment(Card card){
        this.card = card; 
    }
    
    // === Other Methods ===
    @Override
    public String pay(double amount) {

        System.out.println("Processing card: " + card.getNumber());
        System.out.println("Processing credit card... (" + (SUCCESS_RATE * 100) + "% success rate)");

        if (Math.random() > SUCCESS_RATE) {
            System.out.println("Credit card payment failed.");
            return "Failed";
        }

        System.out.println("Paid via Credit Card: $" + amount);
        return "Success";
    }
}
