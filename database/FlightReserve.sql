DROP DATABASE IF EXISTS FLIGHTRESERVE;
CREATE DATABASE FLIGHTRESERVE;
USE FLIGHTRESERVE;

DROP TABLE IF EXISTS person;

CREATE TABLE person
(
    person_id            INT NOT NULL AUTO_INCREMENT,
    first_name           VARCHAR(20) NOT NULL,
    last_name            VARCHAR(20) NOT NULL,
    date_born            DATE,
    username             VARCHAR(30),
    password             VARCHAR(30),
    role                 CHAR(20) NOT NULL,
    PRIMARY KEY (person_id)
);

DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
                          customer_id INT NOT NULL,
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
                       airplane_id    INT NOT NULL,
                       origin_id      INT NOT NULL,
                       destination_id INT NOT NULL,
                       FOREIGN KEY (airplane_id) REFERENCES airplane(airplane_id),
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
                        flight_length    INT,
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
