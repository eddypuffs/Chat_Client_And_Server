import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ENG on 3/31/17.
 */
public class ConnectionHandler implements Runnable{

    private ServerSocket server = null;
    private MessageQueue messageQueue; //Message queue shared by all the clients
    private FHandler filehandler; //Shared file hander shared by all clients
    private ClientHandlerList clientHandlerList;
    private String loginFilePath;

    public ConnectionHandler(int portNumber, String loginFilePath){ //Class initializer takes care of starting server instance

        try{
            System.out.println("$ Instantiating server");
            System.out.println("$ Binding to port " + portNumber);
            this.server = new ServerSocket(portNumber);
            System.out.println("$ Connection Handler started: " + server);
        }
        catch(IOException e){
            System.out.println("$ ERROR: Failed to instantiate server at port " + portNumber + ": " + e.getMessage());
        }

        this.clientHandlerList = new ClientHandlerList();
        this.messageQueue = new MessageQueue(clientHandlerList);
        this.loginFilePath = loginFilePath;
        this.filehandler = new FHandler();

        Thread t = new Thread(this);
        t.start();
    }

    public void run(){

        //Continually try to form a connection with a new client

        while (!server.isClosed()) {
            try {
                //Try to get a new socket connection
                Socket newSocket = server.accept();

                //Create a client handler associated with this socket
                System.out.println("$ New client trying to connect: " + newSocket.toString());
                ClientHandler newClient = new ClientHandler(newSocket, messageQueue, loginFilePath, filehandler);

                //Add the new client to the list
                clientHandlerList.addClient(newClient);

            } catch (IOException e) {
                System.out.println("$ ERROR: Client handler crashed :  " + e.getMessage());
            }
        }

    }



}
