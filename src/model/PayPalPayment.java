// PayPalPayment.java
// Concrete implementation of PaymentStrategy for PayPal payments

package model;


public class PayPalPayment implements PaymentStrategy {
    private Card card; 
    private static final double SUCCESS_RATE = 0.8;

    // === Constructor ===
    public PayPalPayment(Card card){
        this.card = card; 
    }

    // === Other Methods ===
    @Override
    public String pay(double amount) {

        System.out.println("Processing card: " + card.getNumber());
        System.out.println("Contacting PayPal... (" + (SUCCESS_RATE * 100) + "% success rate)");

        if (Math.random() > SUCCESS_RATE) {
            System.out.println("PayPal payment failed.");
            return "Failed";
        }

        System.out.println("Paid via PayPal: $" + amount);
        return "Success";
    }

}
