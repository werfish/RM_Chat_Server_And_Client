# RM_Chat_Server_And_Client
## 1 Overview of the project (Version 1.0)
The goal of the project is to create a multithreaded, multiclient and statefull chat server with accompanying client which lets multiple users exchange chat messeges between each other through a network with encryption capabilities. The server should provide a way to connect to commercial SQL databases, but provide a custom storage solution if none is selected.  Server should provide configuration cababilities in terms of encryption, major and minor server settings and a storage solution. Some configuration should be possible during the server operation.

In the future the application should provide multiple chatrooms support, chatroom creation and deletion and instant messaging and all communication capabilities should be with IBM Watson based ad hoc message translation Engine.
## 2 Requirements
### 2.1.1 User
Users will be interacting with the chat application client. The chat application client should provide the user with:
**Login capabilities
**Registration capabilities
**Error notification capabilities
**Connection status capabilities
User of the application should be presented with a login screen containing space for inputing their login name and password, Login button and Register button, in case the user does not have an account in the app. The login screen should contain validation checking if the username exists, and if the password has been inputted correctly. User should be notified in the case of an error in the login process or if the server is down. In case of the server being down  There should be 
In case the user does not have an acc
### 2.1.2 Admin
## 3 Custom "Message" Protocol
### 3.1 The structure of the Message
### 3.2 Message Types
### 3.3 Command MessageType and the commands
### 3.4 Flow of the Protocol on the Server Side
### 3.5 Flow of the Protocol on the Client Side
## 4 Server Architecture
## 5 Client Architecture
## 6 Storage
### 6.1 Database Interface
### 6.2 Custom Storage
### 6.3 Oracle Connectivity
### 6.4 MySql Connectivity
### 6.5 MongoDB Connectivity
## 7 Encryption and Security
