
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by ENG on 4/2/17.
 */
public class ListeningThread implements Runnable{
    private Socket socket = null;
    private Client client = null;
    private Scanner streamIn = null;

    public ListeningThread(Client client, Socket socket){

        this.client = client;
        this.socket = socket;
        try{
            streamIn = new Scanner(socket.getInputStream());
        }
        catch(IOException ioe){
            System.out.println("Error getting input stream: " + ioe);
            client.stopClient();
        }
        catch (NullPointerException npe){
            System.out.println("Nullpointer exception reading from input stream: socket has been closed.");
        }

        Thread t = new Thread(this);
        t.start();
    }

    public synchronized void handleIncomingMessage(String s){
        //If the message is not enclosed in '{' and '}' print out to the console
        if(s.contains("{") && s.contains("}")){

            //This is a hack to get the filename for FGET


            String[] message = s.substring(s.indexOf("{")+1,s.indexOf("}")).split(",");
            String IP = message[0];
            String port = message[1];
            String filename = message[2];

            System.out.println("$ RECEIVING FGET RESPONSE FOR FILENAME " + filename);

                    FileReceiver fr = new FileReceiver();
            fr.receiveFile(IP, Integer.parseInt(port), filename);

        }
        else{
            System.out.println(s);
        }

    }

    public void run(){

        while(true){
            try{
                if(streamIn.hasNextLine()) handleIncomingMessage(streamIn.nextLine());
            }
            catch(Exception e){
                System.out.println("$ Failed to read from input stream. Closing...");
                client.stopClient();
                return;
            }
        }

    }
}
