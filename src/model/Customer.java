// Customer.java
// Implements PromotionObserver to receive promotion updates
// Inherits from Person class
// Represents a customer in the flight booking system 


package model;

public class Customer extends Person implements PromotionObserver {
    private Address address;
    private String email;
    private String phoneNumber;
    private PaymentStrategy paymentStrategy;

    public Customer(String username, String firstName, String lastName, CustomDate DoB, Address address, String email, String phoneNumber) {
        super(username, firstName, lastName, DoB);
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Customer(int id, String username, String firstName, String lastName, CustomDate DoB, String email) {
        super(id, username, firstName, lastName, DoB);
        this.email = email;
        this.phoneNumber = "";
        this.address = null;
    }

    public Customer(String username, String firstName, String lastName, CustomDate DoB) {
        super(username, firstName, lastName, DoB);
        this.email = "";
        this.phoneNumber = "";
        this.address = null;
    }

    public Address getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public PaymentStrategy getPaymentStrategy() { return paymentStrategy; }

    public void setAddress(Address address) { this.address = address; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {this.paymentStrategy = paymentStrategy; }

    public String processPayment(double amount){
        return paymentStrategy.pay(amount);
    }


    @Override
    public void update(Promotion promotion) {
        System.out.println("Customer " + getUsername() + " received promotion: " + 
            promotion.getPromoCode() + " - " + promotion.getDescription());
    }


    @Override
    public String toString() {
        return "Customer{id=" + getId() + ", username='" + getUsername() + "', firstName='" + getFirstName() + 
            "', lastName='" + getLastName() + "', email='" + email + "', phone='" + phoneNumber + "'}";
    }
}