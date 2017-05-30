import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by ENG on 3/31/17.
 */
public class Client {

    private Socket socket = null;
    private Scanner console = null;
    private DataOutputStream streamOut = null;
    private boolean connectionPersists = true;
    private ListeningThread listeningThread = null;

    public Client(String serverName, int portNumber){ //Initializer
        System.out.println("Establishing connection with server " + serverName + ":" + portNumber);
        try{
            this.socket = new Socket(serverName, portNumber);
            System.out.println("Connected to " + this.socket);
        }
        catch(UnknownHostException e){
            System.out.println("Unknown hostname for server: " + e.getMessage());
            return;
        }
        catch (IOException e){
            System.out.println("Unexpected exception occured when starting up client: " + e.getMessage());
            return;
        }
        this.connectionPersists = true;
        this.listeningThread = new ListeningThread(this, socket);
    }

    public void run(){

        try{
            console = new Scanner(System.in);
            streamOut = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException e){
            System.out.println("ERROR: Failed to start client input and output stream: " + e.getMessage());
            return;
        }
        catch (NullPointerException npe){
            System.out.println("ERROR: Client socket has been closed.");
            return;
        }

        while(connectionPersists){
            try{
                String consoleLine = console.nextLine();
                streamOut.writeUTF(consoleLine + System.lineSeparator());
                streamOut.flush();
                handleOutgoingMessage(consoleLine);
            }
            catch (Exception e){
                System.out.println("Error: " + e.getMessage());
                connectionPersists = false;
            }
        }

        stopClient();
    }


    public synchronized void handleOutgoingMessage(String s){

        //Check if the user is disconnecting
        connectionPersists = !s.equals("<DISCONNECT>");

        //If the user puts up a file, we start a file server for it
        if(s.contains("FPUT")){
            String[] message = s.substring(s.indexOf("<")+1,s.indexOf(">")).split(",");

            String filename = message[1];
            String port = message[3];

            Thread t = new Thread( new FileServer(Integer.parseInt(port), filename));
            t.start();
        }
    }


    public void stopClient(){
        try{
            if(streamOut != null) streamOut.close();
            if(socket != null) socket.close();
        }
        catch(IOException e){
            System.out.println("ERROR: Failed to terminate the client instance properly: " + e.getMessage());
            System.out.println("Terminating...");
        }
        System.exit(0);
    }

}
