CREATE TABLE users(
    userId serial PRIMARY KEY,
    userName varchar(128),
    password varchar(255),
    email varchar(128),
    createdDate timestamp,
    enabled boolean


)