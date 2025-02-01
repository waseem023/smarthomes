CREATE DATABASE OnlineRetailer;
USE OnlineRetailer;

CREATE TABLE Users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('Customer', 'SalesMan', 'StoreManager') NOT NULL
);





/*SELECT * FROM Users;*/

CREATE TABLE Stores (
    StoreID INT PRIMARY KEY AUTO_INCREMENT,
    StoreName VARCHAR(100),
    Street VARCHAR(100),
    City VARCHAR(100),
    State VARCHAR(50),
    Zipcode VARCHAR(10)
);

/*CREATE TABLE Customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100),
    street VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(50),
    zipcode VARCHAR(10)
);*/
CREATE TABLE category (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) UNIQUE
);

INSERT INTO category (category_name) VALUES
('Smart Doorbells'),
('Smart Doorlocks'),
('Smart Speakers'),
('Smart Lightings'),
('Smart Thermostats');

CREATE TABLE Products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    price DECIMAL(10, 2),
    description VARCHAR(255),
    category_id INT,
    warranty BOOLEAN,
    special_discount BOOLEAN,
    manufacturer_rebate BOOLEAN,
    FOREIGN KEY (category_id) REFERENCES category(category_id)
);



/*CREATE TABLE Transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT, 
    customer_name VARCHAR(100),
    customer_address VARCHAR(255),
    credit_card_number VARCHAR(16),
    order_id INT,
    purchase_date DATE,
    ship_date DATE, 
    quantity INT,
    price DECIMAL(10, 2),
    shipping_cost DECIMAL(10, 2),
    discount DECIMAL(10, 2),
    total_sales DECIMAL(10, 2),
    store_id INT,
    items JSON,
    status VARCHAR(100),
    FOREIGN KEY (store_id) REFERENCES Stores(StoreID)
);*/

CREATE TABLE Transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT, 
    customer_name VARCHAR(100),
    customer_address VARCHAR(255),
    credit_card_number VARCHAR(16),
    order_id INT,
    purchase_date DATE,
    ship_date DATE, 
    quantity INT,
    price DECIMAL(10, 2),
    shipping_cost DECIMAL(10, 2),
    discount DECIMAL(10, 2),
    total_sales DECIMAL(10, 2),
    store_id INT,
    items JSON,
    status VARCHAR(100),
    product_id INT,
	product_name VARCHAR(555),
    category INT,
    FOREIGN KEY (store_id) REFERENCES Stores(StoreID)
);



CREATE TABLE Customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100),
    street VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(50),
    zipcode VARCHAR(10),
    user_id INT,
    transaction_id INT
);
/*Select * from Customers;*/
ALTER TABLE products
ADD COLUMN imageUrl VARCHAR(800);

ALTER TABLE transactions
MODIFY store_id INT NULL;

INSERT INTO Users (name, email, password, role) VALUES
('Sales Man', 'salesman@store.com', 'securepassword123', 'SalesMan'),
('Store Manager', 'storemanager@store.com', 'securepassword456', 'StoreManager');

INSERT INTO products (name, description, price, category_id, warranty, special_discount, manufacturer_rebate, imageUrl) VALUES
('Ring Doorbell', 'Smart Doorbell with camera', 199.99, 1, TRUE, FALSE, TRUE, 'https://mobileimages.lowes.com/productimages/e0d1883d-ea45-4e7a-94ce-3d2725ca95d1/67307024.jpeg?size=pdhism'),
('Nest Hello', 'Smart video doorbell', 229.99, 1, TRUE, TRUE, TRUE, 'https://mobileimages.lowes.com/productimages/bcd6c13f-c02a-4c20-ad4a-6d2e23b351a9/67995092.jpeg?size=pdhism'),
('August Smart Lock', 'Keyless entry smart lock', 149.99, 2, TRUE, FALSE, FALSE, 'https://mobileimages.lowes.com/productimages/aeb8cffd-0162-48e7-9a07-3458f10cf18f/67177057.jpeg?size=pdhism'),
('Yale Smart Lock', 'Smart lock with touchscreen', 179.99, 2, TRUE, TRUE, FALSE, 'https://mobileimages.lowes.com/productimages/29ac5ab5-e004-4d63-b9f3-64620fb1ccd3/49703220.png?size=pdhism'),
('Amazon Echo', 'Voice-controlled smart speaker', 99.99, 3, TRUE, TRUE, TRUE, 'https://mobileimages.lowes.com/productimages/3ba22a7b-410f-404c-abd3-4b5bfa50bf17/67447564.jpeg?size=pdhism'),
('Google Home', 'Smart speaker by Google', 89.99, 3, TRUE, FALSE, TRUE, 'https://mobileimages.lowes.com/productimages/20f9e4ec-f37b-4510-a821-6d625a878c0c/67447586.jpeg?size=pdhism'),
('Philips Hue', 'Smart lighting system', 59.99, 5, TRUE, FALSE, FALSE, 'https://mobileimages.lowes.com/productimages/8de537f9-1df5-4270-8db1-de7ef137ce1f/67105654.jpeg?size=pdhism'),
('LIFX', 'Wi-Fi smart lighting system', 49.99, 4, TRUE, TRUE, FALSE, 'https://4starelectric.com/4s24/wp-content/uploads/2022/11/4-star-smart-light-blog_1-1024x486.jpg'),
('Nest Thermostat', 'Smart thermostat', 249.99, 5, TRUE, TRUE, TRUE, 'https://mobileimages.lowes.com/productimages/8bd9a1f0-1dc2-40b4-aca6-f5115f5f8979/67917561.jpeg?size=pdhism'),
('Ecobee', 'Smart thermostat with sensors', 299.99, 5, TRUE, FALSE, TRUE, 'https://cdn.thewirecutter.com/wp-content/media/2022/11/smartthermostats-2048px-2898-3x2-1.jpg?auto=webp&quality=75&crop=3:2&width=1024'),
('Samsung SmartThings Hub', 'Connects smart devices in home', 124.99, 1, TRUE, TRUE, TRUE, 'https://images.ctfassets.net/a3qyhfznts9y/1mXJaGoRAMrPiccMsvWvjC/0b98b1d819a815a78026adcd0c7c5aba/Smart_Doorbell_Mobile_Slot_2.jpg?w=1366&h=1366&fl=progressive&q=80&fm=jpg'),
('Apple HomePod', 'Smart speaker by Apple', 299.99, 3, TRUE, TRUE, TRUE, 'https://i.pcmag.com/imagery/roundups/017S1tRIBIkr8Mfan0lnX4J-59..v1657221180.jpg'),
('Arlo Pro 3', 'Wireless security camera', 199.99, 4, TRUE, FALSE, TRUE, 'https://m.media-amazon.com/images/I/81QQT1CQqfL._AC_UF894,1000_QL80_.jpg'),
('Wyze Cam', 'Affordable smart security camera', 39.99, 2, TRUE, FALSE, FALSE, 'https://m.media-amazon.com/images/I/71r5Lm1aO1L._AC_UF894,1000_QL80_.jpg'),
('SimpliSafe', 'Complete home security system', 229.99, 3, TRUE, TRUE, TRUE, 'https://images.thdstatic.com/productImages/48668ed3-0c1d-4a7e-9b30-d42ccb85acc8/svn/chalk-google-smart-speakers-and-displays-ga00638-us-64_600.jpg'),
('Nest Protect', 'Smoke and carbon monoxide detector', 129.99, 1, TRUE, TRUE, FALSE, 'https://www.iotinsider.com/wp-content/uploads/2023/10/smart-doorbell.jpg'),
('Ring Alarm', 'Home alarm system', 249.99, 4, TRUE, TRUE, TRUE, 'https://www.powerhomebiz.com/wp-content/uploads/2022/03/smartbulbs2.png'),
('Kasa Smart Plug', 'Wi-Fi enabled smart plug', 29.99, 2, TRUE, FALSE, FALSE, 'https://images.thdstatic.com/productImages/7296aa42-bbb5-4bff-894f-a379efd247d5/svn/eufy-security-electronic-deadbolts-t8520j11-fa_600.jpg'),
('TP-Link Smart Bulb', 'Wi-Fi smart light bulb', 24.99, 3, TRUE, TRUE, FALSE, 'https://s.yimg.com/uu/api/res/1.2/WyWkTp4kEwfXl76OMM7Mfg--~B/Zmk9c3RyaW07aD03MjA7dz0xMjgwO2FwcGlkPXl0YWNoeW9u/https://s.yimg.com/os/creatr-uploaded-images/2023-04/10889f60-e29f-11ed-bdee-4377a701adff'),
('Eufy Robovac', 'Robotic vacuum cleaner', 279.99, 4, TRUE, TRUE, TRUE, 'https://i0.wp.com/simplehomecinema.com/wp-content/uploads/2023/07/smart-lights-feature-3141742416-e1689806374387.png?fit=982%2C552&ssl=1'),
('Roomba 675', 'Wi-Fi connected robot vacuum', 299.99, 5, TRUE, FALSE, TRUE, 'https://i5.walmartimages.com/seo/Honeywell-Home-Smart-Color-Thermostat_21737511-d4c6-4538-ab7c-828b974dd74d.ca4bd82b6116518cc8e3f0e07a2f721a.png?odnHeight=768&odnWidth=768&odnBg=FFFFFF'),
('Tile Mate', 'Bluetooth tracker for finding items', 24.99, 2, TRUE, TRUE, FALSE, 'https://lockly.com/cdn/shop/files/Lockly_PGD728FMB-6-CES.jpg?v=1692907306&width=2000'),
('Sonos One', 'Smart speaker with Alexa', 199.99, 4, TRUE, TRUE, TRUE, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRvtNsqeTmdUFnRRr8_wR3OksO4EZcgIlQvdw&s'),
('Logitech Harmony Elite', 'Advanced universal remote', 349.99, 5, TRUE, TRUE, TRUE, 'https://cdn11.bigcommerce.com/s-lwxam9agyb/images/stencil/1024x1024/products/213/839/RCHT9610WFSW2003-T9-and-sensor-68-degrees__99219.1716231579.png?c=2'),
('Bose SoundTouch 10', 'Wireless smart speaker', 199.99, 1, TRUE, FALSE, TRUE, 'https://cdn.thewirecutter.com/wp-content/media/2023/09/smart-doorbell-camera-2048px-0007.jpg?auto=webp&quality=75&crop=1.91:1&width=1200');


INSERT INTO Stores (StoreName, Street, City, State, Zipcode) VALUES
('SmartHomes Central', '123 Main St', 'Los Angeles', 'CA', '90001'),
('SmartHomes North', '456 North St', 'San Francisco', 'CA', '94102'),
('SmartHomes South', '789 South St', 'San Diego', 'CA', '92101'),
('SmartHomes East', '1010 East St', 'New York', 'NY', '10001'),
('SmartHomes West', '1111 West St', 'Seattle', 'WA', '98101'),
('SmartHomes Midwest', '2222 Midwest St', 'Chicago', 'IL', '60601'),
('SmartHomes Downtown', '3333 Downtown St', 'Houston', 'TX', '77001'),
('SmartHomes Uptown', '4444 Uptown St', 'Austin', 'TX', '73301'),
('SmartHomes Lakeside', '5555 Lakeside St', 'Orlando', 'FL', '32801'),
('SmartHomes Mountain', '6666 Mountain St', 'Denver', 'CO', '80201');
