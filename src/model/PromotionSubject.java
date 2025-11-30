// PromotionSubject.java
package model;

public interface PromotionSubject {
    void registerObserver(PromotionObserver observer);
    void removeObserver(PromotionObserver observer);
    void notifyObservers(Promotion promotion);
}