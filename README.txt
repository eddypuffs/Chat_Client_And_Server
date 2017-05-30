README - CLIENT AND SERVER APP

Eduardo Nunez
nunez087@umn.edu
--------------------------------


1) Project Description


In this project, I implemented a client and server chatroom application with peer to peer file sharing capabilities.

A client can send the following commands to the server:

	* <REGISTER,user,pass>
		Registers "user" with password "pass" on the server. Server keeps a file with valid logins on file userDB.txt

	* <LOGIN,user,pass>
		Server looks up username in userDB.txt file and determines if there is a user and password match

	* <DISCONNECT>
		Send this message to disconnect from the server.

	* <MSG,insertmessagehere>
		Send a message to all clients connected to the chatroom

	* <CLIST>
		Get a list of all clients connected to the chatroom.

	* <FLIST>
		Get a list of all files currently being hosted by other clients.

	* <FPUT,filename,IP,port>
		Client can start hosting a file with name "filename" in a specified IP address and port. 

		NOTE: This currently only works if user specifies its own address and a port that is not being used. File must be inside the ‘out’ folder

	* <FGET,fileID>
		Download file with fileID that is received from <FLIST>

		NOTE: The file is downloaded in the current path directory but renamed with the prefix "received_"

2) Compiling and running the client application

	COMPILATION STEPS
	1. Open UNIX terminal
	2. cd PATH_TO_PROJECT
	3. javac -d out ./src/*.java


	RUNNING STEPS
	1. cd out
	2. java ClientApp

3) Compiling and running the server application

	COMPILATION STEPS
	1. Open UNIX terminal
	2. cd PATH_TO_PROJECT
	3. javac -d out ./src/*.java


	RUNNING STEPS
	1. cd out
	2. java ServerApp userDB.txt


	***NOTE: The file userDB.txt must be in the out folder