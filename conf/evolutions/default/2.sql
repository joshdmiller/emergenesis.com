# Users schema
 
# --- !Ups

CREATE TABLE users ( 
    user_id SERIAL PRIMARY KEY,
    fullname VARCHAR(50) NOT NULL, 
    username VARCHAR(30) NOT NULL, 
    password VARCHAR(30) NOT NULL
);

ALTER TABLE posts DROP author;
ALTER TABLE posts ADD user_id INTEGER REFERENCES users(user_id);

# --- !Downs
 
ALTER TABLE posts DROP user_id;
DROP TABLE users;
ALTER TABLE posts ADD author VARCHAR(20);

