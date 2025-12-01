// Promotion.java
// Entity class for promotions
// Represents a promotional offer with code, discount rate, description, and start date
package model;

public class Promotion {
    private String promoCode;
    private double discountRate;
    private String description;
    private CustomDate startDate;

    // === Constructors ===
    public Promotion(String promoCode, double discountRate, String description, 
                    CustomDate startDate) {
        this.promoCode = promoCode;
        this.discountRate = discountRate;
        this.description = description;
        this.startDate = startDate;
    }

    // === Getters ===
    public String getPromoCode() { return promoCode; }
    public double getDiscountRate() { return discountRate; }
    public String getDescription() { return description; }
    public CustomDate getStartDate() { return startDate; }

    // === Setters ===
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
    public void setDiscountRate(double discountRate) { this.discountRate = discountRate; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDate(CustomDate startDate) { this.startDate = startDate; }

    // === Other Methods ===
    @Override
    public String toString() {
        return String.format("Promotion{code='%s', discount=%.2f%%, description='%s', start_date= %s}",
                promoCode, discountRate * 100, description, startDate.toString());
    }
}