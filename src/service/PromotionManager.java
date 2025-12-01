// PromotionManager.java
package service;
import java.util.ArrayList;
import java.util.List;
import model.Promotion;
import model.PromotionObserver;
import model.PromotionSubject;

public class PromotionManager implements PromotionSubject {
    private static PromotionManager instance; // Singleton instance of PromotionManager
    private List<PromotionObserver> observers; // List of registered observers
    private List<Promotion> activePromotions; // List of currently active promotions

    // Private constructor for Singleton pattern to ensure only one instance of PromotionManager
    private PromotionManager() {
        this.observers = new ArrayList<>();
        this.activePromotions = new ArrayList<>();
    }

    // Returns the single instance of PromotionManager (Singleton pattern)
    public static PromotionManager getInstance() {
        if (instance == null) {
            instance = new PromotionManager();
        }
        return instance;
    }

    // Registers an observer to receive updates when promotions change
    @Override
    public void registerObserver(PromotionObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer); // Add the observer if not already registered
        }
    }

    // Removes an observer from the list of observers
    @Override
    public void removeObserver(PromotionObserver observer) {
        observers.remove(observer); // Remove the observer from the list
    }

    // Notifies all registered observers of a new or updated promotion
    @Override
    public void notifyObservers(Promotion promotion) {
        for (PromotionObserver observer : observers) {
            observer.update(promotion); // Notify each observer with the updated promotion
        }
    }

    // Adds a new promotion to the list of active promotions and notifies observers
    public void addPromotion(Promotion promotion) {
        activePromotions.add(promotion); // Add the promotion to active promotions list
        notifyObservers(promotion); // Notify observers of the new promotion
    }

    // Removes a promotion from the list of active promotions by its promoCode
    public void removePromotion(String promoCode) {
        activePromotions.removeIf(p -> p.getPromoCode().equals(promoCode)); // Remove promotion by promoCode
    }

    // Retrieves a promotion by its promoCode from the list of active promotions
    // Returns null if no matching promotion is found
    public Promotion getPromotionByCode(String promoCode) {
        for (Promotion promotion : activePromotions) {
            if (promotion.getPromoCode().equalsIgnoreCase(promoCode)) {
                return promotion; // Return the matching promotion
            }
        }
        return null; // Return null if no matching promotion is found
    }

    // Returns a new list containing all the active promotions
    public List<Promotion> getActivePromotions() {
        return new ArrayList<>(activePromotions); // Return a copy of the active promotions list
    }

    // Loads promotions from the database and updates the active promotions list
    public void loadPromotionsFromDatabase() {
        try {
            backend.DatabaseManager db = backend.DatabaseManager.getInstance();
            ArrayList<Promotion> promotions = db.getAllPromotions(); // Retrieve promotions from the database
            activePromotions.clear(); // Clear current active promotions
            activePromotions.addAll(promotions); // Add all retrieved promotions to the active list
        } catch (Exception e) {
            System.err.println("Error loading promotions from database: " + e.getMessage()); // Log error if database retrieval fails
        }
    }
}