DROP DATABASE IF EXISTS FLIGHTRESERVE;
CREATE DATABASE FLIGHTRESERVE;
USE FLIGHTRESERVE;

DROP TABLE IF EXISTS person;

CREATE TABLE person
(
    person_id            INT NOT NULL AUTO_INCREMENT,
    username             VARCHAR(20) NOT NULL UNIQUE,
    first_name           VARCHAR(20) NOT NULL,
    last_name            VARCHAR(20) NOT NULL,
    date_born            DATE,
    password             VARCHAR(30),
    role                 CHAR(20) NOT NULL,
    PRIMARY KEY (person_id)
);

DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
                          customer_id          INT NOT NULL,
                          email                VARCHAR(30),
                          PRIMARY KEY (customer_id),
                          FOREIGN KEY (customer_id) REFERENCES person (person_id)
);

DROP TABLE IF EXISTS agent;

CREATE TABLE agent (
                       agent_id INT NOT NULL,
                       PRIMARY KEY (agent_id),
                       FOREIGN KEY (agent_id) REFERENCES person(person_id)
);

DROP TABLE IF EXISTS agent_customer;

CREATE TABLE agent_customer (
                                agent_id     INT NOT NULL,
                                customer_id  INT NOT NULL,
                                PRIMARY KEY (agent_id, customer_id),
                                FOREIGN KEY (agent_id) REFERENCES agent(agent_id),
                                FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

DROP TABLE IF EXISTS airline;

CREATE TABLE airline
(
    airline_name        VARCHAR(50) NOT NULL,
    PRIMARY KEY (airline_name)
);

DROP TABLE IF EXISTS airplane;

CREATE TABLE airplane
(
    airplane_id    INT NOT NULL AUTO_INCREMENT,
    airline_name   VARCHAR(50) NOT NULL,
    name           VARCHAR(20),
    flight_number  INT,
    PRIMARY KEY (airplane_id),
    FOREIGN KEY (airline_name) REFERENCES airline(airline_name)
);

DROP TABLE IF EXISTS address;

CREATE TABLE address (
                         address_id   INT AUTO_INCREMENT PRIMARY KEY,
                         postal_code  VARCHAR(20),
                         number       INT,
                         street       VARCHAR(50),
                         city         VARCHAR(30),
                         state        VARCHAR(20),
                         country      VARCHAR(20)
);

DROP TABLE IF EXISTS route;

CREATE TABLE route (
                       route_id       INT AUTO_INCREMENT PRIMARY KEY,
                       origin_id      INT NOT NULL,
                       destination_id INT NOT NULL,
                       FOREIGN KEY (origin_id) REFERENCES address(address_id),
                       FOREIGN KEY (destination_id) REFERENCES address(address_id)
);

DROP TABLE IF EXISTS flight;

CREATE TABLE flight (
                        flight_id        INT AUTO_INCREMENT PRIMARY KEY,
                        airplane_id      INT NOT NULL,
                        route_id         INT NOT NULL,
                        departure_date   DATE NOT NULL,
                        arrival_date     DATE NOT NULL,
                        available_seats  INT,
                        flight_length    VARCHAR(20),
                        price            FLOAT,
                        FOREIGN KEY (airplane_id) REFERENCES airplane(airplane_id),
                        FOREIGN KEY (route_id) REFERENCES route(route_id)
);

DROP TABLE IF EXISTS booking;

CREATE TABLE booking (
                         booking_id    INT AUTO_INCREMENT PRIMARY KEY,
                         customer_id   INT NOT NULL,
                         flight_id     INT NOT NULL,
                         seat_number   INT NOT NULL,
                         FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
                         FOREIGN KEY (flight_id) REFERENCES flight(flight_id)
);




-- Insert sample data into FLIGHTRESERVE database


-- Insert into person and customer
INSERT INTO person (username, first_name, last_name, date_born, password, role) VALUES
                                                                          ('johns', 'John', 'Smith', '1985-03-15', 'pass123', 'Customer'),
                                                                          ('sarahj', 'Sarah', 'Johnson', '1990-07-22', 'pass456', 'Customer'),
                                                                          ('mb', 'Michael', 'Brown', '1988-11-30', 'pass789', 'Customer'),
                                                                          ('edavis', 'Emily', 'Davis', '1992-05-18', 'pass101', 'Customer'),
                                                                          ('dwilson', 'David', 'Wilson', '1983-09-12', 'pass202', 'Customer'),
                                                                          ('jenagent', 'Jennifer', 'Miller', '1995-02-28', 'agent123', 'FlightAgent'),
                                                                          ('robadmin', 'Robert', 'Taylor', '1980-12-05', 'admin123', 'Admin');

-- Insert into customer (emails)
INSERT INTO customer (customer_id, email) VALUES
                                              (1, 'john.smith@email.com'),
                                              (2, 'sarah.j@email.com'),
                                              (3, 'mike.brown@email.com'),
                                              (4, 'emily.davis@email.com'),
                                              (5, 'david.wilson@email.com');

-- Insert into agent
INSERT INTO agent (agent_id) VALUES (6);

-- Insert into agent_customer (assign customers to agent)
INSERT INTO agent_customer (agent_id, customer_id) VALUES
                                                       (6, 1), (6, 2), (6, 3), (6, 4), (6, 5);

-- Insert into airline
INSERT INTO airline (airline_name) VALUES
                                       ('SkyWings Airlines'),
                                       ('Global Airways'),
                                       ('Pacific Express'),
                                       ('Northern Star'),
                                       ('Sunset Airlines');

-- Insert into address (airport addresses)
INSERT INTO address (postal_code, number, street, city, state, country) VALUES
                                                                            ('T2G 0B4', 2000, 'Airport Road NE', 'Calgary', 'AB', 'Canada'),
                                                                            ('V7B 1B3', 3211, 'Grant McConachie Way', 'Richmond', 'BC', 'Canada'),
                                                                            ('M5V 1A1', 6301, 'Silver Dart Dr', 'Mississauga', 'ON', 'Canada'),
                                                                            ('H4Y 1A1', 975, 'Roméo-Vachon Blvd N', 'Dorval', 'QC', 'Canada'),
                                                                            ('T3E 7W1', 2000, 'Airport Road NE', 'Calgary', 'AB', 'Canada'),
                                                                            ('V6C 0A1', 999, 'Canada Place', 'Vancouver', 'BC', 'Canada'),
                                                                            ('M5J 1A1', 290, 'Bremner Blvd', 'Toronto', 'ON', 'Canada'),
                                                                            ('H2Z 1A1', 155, 'Rue De l''Université', 'Montreal', 'QC', 'Canada'),
                                                                            ('YEG 1A1', 1, 'Edmonton International Airport', 'Edmonton', 'AB', 'Canada'),
                                                                            ('YOW 1A1', 1000, 'Airport Parkway Private', 'Ottawa', 'ON', 'Canada');

-- Insert into airplane
INSERT INTO airplane (airline_name, name, flight_number) VALUES
                                                             ('SkyWings Airlines', 'Boeing 737', 101),
                                                             ('SkyWings Airlines', 'Airbus A320', 102),
                                                             ('Global Airways', 'Boeing 777', 201),
                                                             ('Global Airways', 'Airbus A330', 202),
                                                             ('Pacific Express', 'Boeing 787', 301),
                                                             ('Pacific Express', 'Airbus A350', 302),
                                                             ('Northern Star', 'Bombardier CRJ', 401),
                                                             ('Northern Star', 'Embraer E175', 402),
                                                             ('Sunset Airlines', 'Airbus A320', 501);

-- Insert into route
INSERT INTO route (origin_id, destination_id) VALUES
                                                  (1, 2),   -- Calgary to Vancouver
                                                  (2, 1),   -- Vancouver to Calgary
                                                  (1, 3),   -- Calgary to Toronto
                                                  (3, 1),   -- Toronto to Calgary
                                                  (2, 3),   -- Vancouver to Toronto
                                                  (3, 2),   -- Toronto to Vancouver
                                                  (1, 4),   -- Calgary to Montreal
                                                  (4, 1),   -- Montreal to Calgary
                                                  (2, 4),   -- Vancouver to Montreal
                                                  (4, 2),   -- Montreal to Vancouver
                                                  (3, 4),   -- Toronto to Montreal
                                                  (4, 3);   -- Montreal to Toronto

-- Insert into flight
INSERT INTO flight (airplane_id, route_id, departure_date, arrival_date, available_seats, flight_length, price) VALUES
                                                                                                                    (1, 1, '2024-12-20', '2024-12-20', 150, '01:30', 299.99),
                                                                                                                    (2, 2, '2024-12-21', '2024-12-21', 180, '01:30', 289.99),
                                                                                                                    (3, 3, '2024-12-20', '2024-12-20', 300, '03:45', 459.99),
                                                                                                                    (4, 4, '2024-12-21', '2024-12-21', 250, '03:45', 449.99),
                                                                                                                    (5, 5, '2024-12-22', '2024-12-22', 280, '04:15', 399.99),
                                                                                                                    (6, 6, '2024-12-23', '2024-12-23', 240, '04:15', 389.99),
                                                                                                                    (7, 7, '2024-12-20', '2024-12-20', 80, '03:30', 349.99),
                                                                                                                    (8, 8, '2024-12-21', '2024-12-21', 76, '03:30', 339.99),
                                                                                                                    (9, 9, '2024-12-22', '2024-12-22', 180, '04:45', 419.99),
                                                                                                                    (1, 10, '2024-12-23', '2024-12-23', 150, '04:45', 409.99),
                                                                                                                    (2, 11, '2024-12-24', '2024-12-24', 180, '01:15', 199.99),
                                                                                                                    (3, 12, '2024-12-25', '2024-12-25', 300, '01:15', 189.99);

-- Insert into booking
INSERT INTO booking (customer_id, flight_id, seat_number) VALUES
                                                              (1, 1, 15),
                                                              (1, 3, 22),
                                                              (2, 2, 8),
                                                              (3, 5, 33),
                                                              (4, 7, 12),
                                                              (5, 9, 45),
                                                              (2, 4, 18),
                                                              (3, 6, 27);

-- Insert more sample addresses for variety
INSERT INTO address (postal_code, number, street, city, state, country) VALUES
                                                                            ('90210', 123, 'Sunset Blvd', 'Beverly Hills', 'CA', 'USA'),
                                                                            ('10001', 456, '5th Avenue', 'New York', 'NY', 'USA'),
                                                                            ('W1B 4A1', 78, 'Oxford Street', 'London', 'England', 'UK'),
                                                                            ('75001', 32, 'Champs-Élysées', 'Paris', 'Île-de-France', 'France'),
                                                                            ('10115', 89, 'Friedrichstraße', 'Berlin', 'Berlin', 'Germany');

-- Insert international routes
INSERT INTO route (origin_id, destination_id) VALUES
                                                  (3, 11),  -- Toronto to New York
                                                  (11, 3),  -- New York to Toronto
                                                  (3, 12),  -- Toronto to London
                                                  (12, 3),  -- London to Toronto
                                                  (4, 13),  -- Montreal to Paris
                                                  (13, 4),  -- Paris to Montreal
                                                  (2, 14),  -- Vancouver to Berlin
                                                  (14, 2);  -- Berlin to Vancouver

-- Insert international flights
INSERT INTO flight (airplane_id, route_id, departure_date, arrival_date, available_seats, flight_length, price) VALUES
                                                                                                                    (3, 13, '2024-12-26', '2024-12-26', 280, '01:45', 349.99),
                                                                                                                    (4, 14, '2024-12-27', '2024-12-27', 250, '01:45', 339.99),
                                                                                                                    (5, 15, '2024-12-28', '2024-12-28', 260, '07:30', 899.99),
                                                                                                                    (6, 16, '2024-12-29', '2024-12-29', 240, '07:30', 879.99),
                                                                                                                    (7, 17, '2024-12-30', '2024-12-30', 80, '06:45', 799.99),
                                                                                                                    (8, 18, '2024-12-31', '2024-12-31', 76, '06:45', 789.99),
                                                                                                                    (9, 19, '2025-01-01', '2025-01-01', 180, '09:15', 1099.99),
                                                                                                                    (1, 20, '2025-01-02', '2025-01-02', 150, '09:15', 1079.99);

-- Verify data insertion
SELECT 'Data insertion completed successfully!' as Status;

-- Show counts of each table
SELECT
    'person' as table_name, COUNT(*) as count FROM person
UNION ALL SELECT 'customer', COUNT(*) FROM customer
UNION ALL SELECT 'agent', COUNT(*) FROM agent
UNION ALL SELECT 'agent_customer', COUNT(*) FROM agent_customer
UNION ALL SELECT 'airline', COUNT(*) FROM airline
UNION ALL SELECT 'airplane', COUNT(*) FROM airplane
UNION ALL SELECT 'address', COUNT(*) FROM address
UNION ALL SELECT 'route', COUNT(*) FROM route
UNION ALL SELECT 'flight', COUNT(*) FROM flight
UNION ALL SELECT 'booking', COUNT(*) FROM booking;



-- Drop roles if they exist
DROP ROLE IF EXISTS db_admin@localhost, db_agent@localhost, db_customer@localhost;

-- Create roles
CREATE ROLE db_admin@localhost, db_agent@localhost, db_customer@localhost;

-- Grant privileges to roles
GRANT ALL PRIVILEGES ON FLIGHTRESERVE.* TO db_admin@localhost;
GRANT SELECT, INSERT, DELETE, UPDATE ON FLIGHTRESERVE.* TO db_agent@localhost;
GRANT SELECT, INSERT, DELETE ON FLIGHTRESERVE.booking TO db_customer@localhost;
GRANT SELECT ON FLIGHTRESERVE.flight TO db_customer@localhost;
GRANT SELECT ON FLIGHTRESERVE.airplane TO db_customer@localhost;
GRANT SELECT ON FLIGHTRESERVE.route TO db_customer@localhost;
GRANT SELECT ON FLIGHTRESERVE.address TO db_customer@localhost;

-- Drop users if they exist
DROP USER IF EXISTS admin_user@localhost;
DROP USER IF EXISTS customer_user@localhost;
DROP USER IF EXISTS agent_user@localhost;
DROP USER IF EXISTS guest@localhost;
DROP USER IF EXISTS data_entry_user@localhost;

-- Create users
CREATE USER admin_user@localhost IDENTIFIED BY 'admin_password';
CREATE USER customer_user@localhost IDENTIFIED BY 'customer_password';
CREATE USER agent_user@localhost IDENTIFIED BY 'agent_password';
CREATE USER guest@localhost;
CREATE USER data_entry_user@localhost IDENTIFIED BY 'data_password';

-- Grant roles to users
GRANT db_admin@localhost TO admin_user@localhost;
GRANT db_agent@localhost TO agent_user@localhost;
GRANT db_customer@localhost TO customer_user@localhost;

-- Set default roles (this activates the roles)
SET DEFAULT ROLE ALL TO admin_user@localhost;
SET DEFAULT ROLE ALL TO agent_user@localhost;
SET DEFAULT ROLE ALL TO customer_user@localhost;

-- Additional direct grants if needed
GRANT SELECT ON FLIGHTRESERVE.* TO guest@localhost;
GRANT INSERT, UPDATE ON FLIGHTRESERVE.airline TO data_entry_user@localhost;
GRANT INSERT, UPDATE ON FLIGHTRESERVE.airplane TO data_entry_user@localhost;
GRANT INSERT, UPDATE ON FLIGHTRESERVE.flight TO data_entry_user@localhost;

-- Flush privileges to apply changes
FLUSH PRIVILEGES;
