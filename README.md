# RM_Chat_Server_And_Client
## 1 Overview of the project (Version 1.0)
The goal of the project is to create a multithreaded, multiclient and statefull chat server with accompanying client which lets multiple users exchange chat messeges between each other through a network with encryption capabilities. The server should provide a way to connect to commercial SQL databases, but provide a custom storage solution if none is selected.  Server should provide configuration cababilities in terms of encryption, major and minor server settings and a storage solution. Some configuration should be possible during the server operation.

In the future the application should provide multiple chatrooms support, chatroom creation and deletion and instant messaging and all communication capabilities should be with IBM Watson based ad hoc message translation Engine.
## 2 Requirements
### 2.1.1 User
Users will be interacting with the chat application client. The chat application client should provide the user with:
* Loging in and loging out capability
* Registration possibility
* Error notification
* Connection status display
* Possibility of sending and receiving messeges while logged in
* Display of all connected users

**Login**

User of the application should be presented with a login screen containing space for inputing their login name and password, Login button and Register button, in case the user does not have an account in the app. The login screen should contain validation checking if the username exists, and if the password has been inputted correctly. User should be notified in the case of an error in the login process or if the server is down. In case of a server being down there should be a constant notification showing the connection status as well as actions performed by the client. If the user chooses to login and all the login details are correct, the user should be presented with the chat application screen. If the user chooses to register, there should be a register button present which takes the user into the register.

**Register**

In the use case when the user does not have an account, after clicking the Register button on the main screen, user should be taken into a registration screen.
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
**Not yet implemented**
### 6.4 MySql Connectivity
**Not yet implemented**
### 6.5 MongoDB Connectivity
**Not yet implemented**
## 7 Encryption and Security
