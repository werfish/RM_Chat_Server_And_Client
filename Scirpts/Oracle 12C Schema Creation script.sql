-- This script is written to outomate cration of Oracle Database schema for the Tran Chat application sever:

-- Get the password from the script user(dont want to pu it here)
ACCEPT v_chat_password CHAR PROMPT 'Please type in Tran Chat Database password: ' HIDE

-- Define the table space here, in my case it is users
DEFINE Table_Space = users

-- Removes the chat application user for testing puproses
DROP USER store CASCADE;

-- Creates the Tran_Chat user for the server to use
CREATE USER Tran_Chat IDENTIFIED BY &v_chat_password;

-- give the user administrative rights to connect and to create tables, objects etc
GRANT connect, resource TO Tran_Chat;

-- Give the Tran-Chat user memory quote in the table space, in my case I am using 10M
ALTER USER store QUOTA 10M on &Table_Space;

-- Connect to the new user and schema, the below works in my case, the passowrd is concatenated here
CONNECT store/&v_store_password.@test;

--CREATING THE TABLES BELOW
CREATE TABLE Users (
  user_id INTEGER CONSTRAINT users_pk PRIMARY KEY,
  first_name VARCHAR2(10) NOT NULL,
  last_name VARCHAR2(20) NOT NULL,
  username VARCHAR2(15) NOT NULL,
  password VARCHAR2(15) NOT NULL,
);

-- In case you are loging
spool off;
