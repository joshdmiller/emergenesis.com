# Posts schema
 
# --- !Ups

create table posts ( 
    post_id integer primary key, 
    title varchar(140) not null, 
    slug varchar(140) not null, 
    author varchar(20) not null,
    modified_at date, 
    body text
);

# --- !Downs
 
DROP TABLE task;

