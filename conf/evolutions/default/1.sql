create table posts ( 
    post_id integer primary key, 
    title varchar(140) not null, 
    slug varchar(140) not null, 
    author carchar(20) not null,
    modified_at date, 
    body text
);
