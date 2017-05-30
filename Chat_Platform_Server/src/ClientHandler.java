import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by ENG on 4/1/17.
 */

public class ClientHandler implements Runnable{
    private String ID;
    private Socket socket;
    private Scanner inputStream;
    private PrintWriter outputStream;
    private MessageQueue sharedMessageQueue;
    private String loginFilePath;
    private FHandler fm;

    public ClientHandler(Socket socket, MessageQueue sharedQueuePointer, String loginFilePath, FHandler sharedfm){
        //Instantiate new client at the specified socket and set its shared queue
        this.socket = socket;
        this.loginFilePath = loginFilePath;
        this.fm = sharedfm;

        if(this.sharedMessageQueue == null){ this.sharedMessageQueue = sharedQueuePointer; }

        //Instantiate the inbound and outbound streams
        try{
            inputStream = new Scanner(socket.getInputStream());
            outputStream = new PrintWriter(socket.getOutputStream());
        }
        catch(IOException e){
            System.out.println("$ CLIENTHANDLER ERROR: Failed to insantiate input and output streams for socket " + socket + ": " + e.getMessage());
        }

        //Attempt to establish a connection
        if(!attemptLogin()){
            stop();
            try{socket.close();}
            catch(IOException e){
                System.out.println("Failed to close socket");
            }
            return;
        }
        else{
            Thread t = new Thread(this);
            t.start();
        }
    }

    public void run(){
        //Repeatedly checks if the client has sent a message to the server
        //TODO: Handle client disconnecting unexpectedly using a timeout of sorts

        while(!isClosed()){
            try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
            if(inputStream.hasNextLine()) handleIncomingMessage();
        }
        stop();
    }

    public synchronized boolean attemptLogin() { //Waits for input to send login message, returns TRUE only if user managed to login succesfully

        System.out.println("$ Waiting for client login input");
        while(!inputStream.hasNextLine());
        String in = inputStream.nextLine();
        System.out.println("$ CLIENTHANDLER for " + socket + " received message:" + in);

        //Check format of input

        if(in.length()<3){
            sendMessageToClient(0xFF);
            System.out.println("$ Client " + socket + " did not format the command correctly");
            return false;
        }

        if((!in.contains("<") || !in.contains(">"))  || (in.indexOf("<")>in.indexOf(">"))){
            sendMessageToClient(0xFF);
            System.out.println("$ Client " + socket.toString() + " did not format the message with brackets '<' and '>'");
            return false;
        }

        //Split up message into pieces
        String[] message = in.substring(in.indexOf("<")+1,in.indexOf(">")).split(",");

        //Check if the user sends request to REGISTER or LOGIN
        if(!(message[0].equals("REGISTER") || message[0].equals("LOGIN"))){
            sendMessageToClient(0xFF);
            System.out.println("$ Client " + socket.toString() + " did not provide LOGIN or REGISTER command.");
            return false;
        }

        //Check if the command has 2 arguments

        if(message.length!=3){
            sendMessageToClient(0xFF);
            System.out.println("$ Client " + socket.toString() + " provided an incorrect number of arguments to LOGIN or REGISTER command");
            return false;
        }

        //Get the user and pass

        ClientLogin cl = new ClientLogin(loginFilePath);
        String userID = message[1];
        String pass = message[2];

        //Handle cases for LOGIN and REGISTER
        if(message[0].equals("LOGIN")){
            System.out.println("$ Client " + socket + " attempting to login as " + userID + " with password " + pass);
            int result = cl.validateLogin(userID, pass);
            if(result==0x00){
                System.out.println("$ Client " + socket + " logged in succesfully as " + userID);
                this.ID = userID;
                sendMessageToClient(0x00);
                return true;
            }
            else{
                System.out.println("$ Client failed to Login");
                sendMessageToClient(result);

                if(attemptLogin() == false){ // We give the user another chance to login. If it fails, we send out error message and quit
                    return false;
                }
                return true;
            }
        }
        else{
            int result = cl.registerUser(userID, pass);
            if(result==0x00){
                System.out.println("$ Client " + socket + " registered succesfully as " + userID);
                sendMessageToClient(0x00);
                if(attemptLogin() == false){ // We give the user another chance to login. If it fails, we send out error message and quit
                    return false;
                }
                return true;
            }
            else{
                System.out.println("$ Client failed to register");
                sendMessageToClient(result);
                return false;
            }

        }
    }

    public synchronized boolean handleIncomingMessage(){

        String in = inputStream.nextLine();
        System.out.println("$ CLIENTHANDLER for " + ID + " received message: " + in);

        //Check formatting of input
        if(in.length()<3){
            sendMessageToClient(0xFF);
            System.out.println("$ Client " + ID + " did not format the command correctly");
            return false;
        }

        if((!in.contains("<") || !in.contains(">"))  || (in.indexOf("<")>in.indexOf(">"))){
            sendMessageToClient(0xFF);
            System.out.println("$ Client " + ID + " did not format the message with brackets '<' and '>'");
            return false;
        }

        //Split up message into pieces
        String[] message = in.substring(in.indexOf("<")+1,in.indexOf(">")).split(",");

        switch (message[0]){

            case "REGISTER":
            {
                System.out.println("$ Client " + ID + " can not register after logging in");
                sendMessageToClient(0xFF);
                return false;
            }
            case "LOGIN":
            {
                System.out.println("$ Client " + ID + " is already logged in");
                sendMessageToClient(0xFF);
                return false;
            }
            case "DISCONNECT":
            {
                if (message.length != 1){
                    sendMessageToClient(0xFF);
                    System.out.println("$ Client " + ID + " provided incorrect number of arguments for DISCONNECT command");
                    return false;
                }
                stop();
                return true;
            }
            case "MSG":
            {
                if (message.length != 2){
                    sendMessageToClient(0xFF);
                    System.out.println("$ Client " + ID + " provided incorrect number of arguments for MSG command");
                    return false;
                }
                System.out.println("$ Adding message '" + message[1] + "' to queue.");
                sharedMessageQueue.addMessageToQ(this, message[1]);
                sendMessageToClient(0x00);
                return true;
            }
            case "CLIST":
            {
                if (message.length != 1){
                    sendMessageToClient(0xFF);
                    System.out.println("$ Client " + ID + " provided incorrect number of arguments for CLIST command");
                    return false;
                }
                String[] list = sharedMessageQueue.getClientList();
                sendMessageToClient(0x00, list);
                return true;

            }
            case "FLIST":
            {
                if (message.length != 1){
                    sendMessageToClient(0xFF);
                    System.out.println("$ Client " + ID + " provided incorrect number of arguments for FLIST command");
                    return false;
                }

                String[] fileList = fm.getFileList();
                sendMessageToClient(0x00, fileList);
                return true;

            }
            case "FPUT": {
                if (message.length != 4) {
                    sendMessageToClient(0xFF);
                    System.out.println("$ Client " + ID + " provided incorrect number of arguments for FPUT command");
                    return false;
                }

                String filename = message[1];
                String IP = message[2];
                String port = message[3];


                //Check if IP is valid

                boolean isIPv4 = true;
                try {
                    final InetAddress inet = InetAddress.getByName(IP);
                    isIPv4 = inet.getHostAddress().equals(IP) && inet instanceof Inet4Address;
                } catch (final UnknownHostException e) {
                    isIPv4 = false;
                }

                //TODO: Check if port is valid

                if (!isIPv4) {
                    System.out.println("$ Client " + ID + " provided an incorrect IP address in FPUT command");
                    sendMessageToClient(0x04);
                    return false;
                }

                //Add filename to table
                fm.addFile(IP + ":" + port, filename);
                sendMessageToClient(0x00);
                return true;
            }
            case "FGET": { //<FGET,fileID> --> respond with <0x00, IP, port>. Additionally we will respond with {IP,port,filename}
                if (message.length != 2){
                    sendMessageToClient(0xFF);
                    System.out.println("$ Client " + ID + " provided incorrect number of arguments for FGET command");
                    return false;
                }

                String fileIDrequested = message[1];

                if(!fm.fileExists(fileIDrequested)){
                    System.out.println("$ Client " + ID + " requested an invalid fileID " + fileIDrequested);
                    sendMessageToClient(0x03);
                    return false;

                }

                String host = fm.getHostForFileID(fileIDrequested);
                String filename = fm.fileIDtoFilename(fileIDrequested);

                String[] hostinfo = host.split(":");
                sendMessageToClient(0x00, hostinfo);
                sendMessageToClient(hostinfo[0]+","+hostinfo[1]+","+filename);

                return true;
            }


            default:
            {
                sendMessageToClient(0xFF);
                System.out.println("$ Client " + ID + " sent unrecognizable command");
                return false;
            }
        }

    }

    public String getID(){
        return ID;
    }
    public synchronized void sendMessageToClient(int error_code){ //Send a message to client that only contains an error code
        String[] blankMessage = {};
        sendMessageToClient(error_code, blankMessage);
    }
    public synchronized void sendMessageToClient(int error_code, String[] message){ //Send message to this client

        //Construct String

        String messageOut = "";
        messageOut+="<";

        messageOut+= ( "0x" + Integer.toHexString(error_code));

        for(int i=0; i<(message.length) ; i++){
            messageOut+=",";
            messageOut+=message[i];
        }

        messageOut+=">";
        messageOut+=System.lineSeparator();

        //Send string out
        outputStream.write(messageOut);
        outputStream.flush();

    }

    public synchronized  void sendMessageToClient(String message){

        //Construct String

        String messageOut = "";
        messageOut+="{";
        messageOut+=message;
        messageOut+="}";
        messageOut+=System.lineSeparator();

        //Send string out
        outputStream.write(messageOut);
        outputStream.flush();


    }

    public synchronized void stop(){
        sharedMessageQueue.clientList.removeClient(this);
    }

    public void closeConnection(){
        try {
            System.out.println("Closing connection for client " + socket.toString());
            socket.close();
            while(!isClosed());
        } catch (IOException e) {
            System.out.println("$ ERROR: Failed to close connection for client " + socket.toString() + ": " + e.getMessage());
        }
    }
    public boolean isClosed(){
        return socket.isClosed();
    }
}
