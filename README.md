# README - CLIENT AND SERVER APP

Eduardo Nunez
nunez087@umn.edu


## Project Description

In this project, I implemented a client and server chatroom application with peer to peer file sharing capabilities.

A client can send the following commands to the server:

1. &lt;REGISTER,user,pass&gt;
    * Registers "user" with password "pass" on the server. Server keeps a file with valid logins on file userDB.txt.

2. &lt;LOGIN,user,pass&gt;
    * Server looks up username in userDB.txt file and determines if there is a user and password match.

3. &lt;DISCONNECT&gt;
    * Send this message to disconnect from the server.

4. &lt;MSG,insertmessagehere&gt;
    * Send a message to all clients connected to the chatroom.

5. &lt;CLIST&gt;
    * Get a list of all clients connected to the chatroom.

6. &lt;FLIST&gt;
    * Get a list of all files currently being hosted by other clients.

7. &lt;FPUT,filename,IP,port&gt;
    * Client can start hosting a file with name "filename" in a specified IP address and port. 
    * _NOTE: This currently only works if user specifies its own address and a port that is not being used. File must be inside the ‘out’ folder_

8. &lt;FGET,fileID&gt;
  * Download file with fileID that is received from <FLIST>
  * _NOTE: The file is downloaded in the current path directory but renamed with the prefix "received"_

## Compiling and running the client application

### Compiling
1. ```$ cd PATH_TO_PROJECT```
2. ```javac -d out ./src/*.java```


### Running
1. ```cd out```
2. ```java ClientApp```

## Compiling and running the server application

### Compiling
1. ```cd PATH_TO_PROJECT```
2. ```javac -d out ./src/*.java```

### Running
1. ```cd out```
2. ```java ServerApp userDB.txt```

_NOTE: The file userDB.txt must be in the out folder_
