create database logindb;
use logindb;

 CREATE TABLE userlogin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    gmail VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL
);
select *from userlogin;