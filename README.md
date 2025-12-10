# Flight Reservation System

## Overview
A standalone Java GUI application simulating a flight booking system. Users can view flights, make and manage reservations, and maintain customer and flight information. Built with object-oriented principles and a three-layer architecture (Presentation, Business Logic, Data).

**User Roles:**
- **Customer:** Search flights, book/cancel reservations, view bookings  
- **Flight Agent:** Manage customer data, modify reservations, view schedules  
- **System Administrator:** Add/update/remove flights, manage routes and aircraft  

---

## Features
- **Flight Search & View:** Search by origin, destination, date, airline; view available seats, time, and price  
- **Booking Management:** Make, cancel, or modify bookings; generate confirmations  
- **Customer Management:** Add/view/edit customer profiles  
- **Flight Management (Admin only):** Add, update, or remove flights  
- **Database Connectivity:** MySQL database for flight and reservation data  

---

## Design Highlights
- **Activity Diagrams:** Login, browsing/selecting flights, booking, payment  
- **Use-case Diagram & Scenarios**  
- **Sequence Diagrams** for major use cases  
- **State Transition Diagrams** for system, payment, reservation, and flight objects  
- **Class Diagram** showing entities, controllers, and boundaries  
- **Package Diagram** illustrating the three-layer system  

---

## Implementation
- Java Swing GUI  
- MySQL database  
- Modular, maintainable, scalable, and reusable design  

---

## How to Run
1. **Set up MySQL database:**
   - Create a database named `flight_reservation`
   - Import the provided `schema.sql` to create tables  
2. **Update Database Config:**
   - Edit `DBConnection.java` to match your MySQL username and password  
3. **Compile & Run:**
   ```bash
   javac -d bin src/**/*.java
   java -cp bin Main
