import java.io.*;
import java.net.Socket;

/**
 * Created by ENG on 4/23/17.
 */
public class FileReceiver {

    public final static int FILE_SIZE = 5000; //File size is temporarily hard coded


    public void receiveFile(String senderIP, int senderPort, String filename) {

        Socket socket = null;
        InputStream in = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        byte[] bytes = new byte[16 * 1024];

        //Bind to socket
        try {
            socket = new Socket(senderIP, senderPort);
            System.out.println("$ FileReceiver - Client about to receive file:" + filename + " from " + senderIP);
        } catch (IOException ex) {
            System.out.println("ERROR: FileReceiver - Can't accept connection with fileserver. ");
        }

        //Input and output stream
        try{
            in = socket.getInputStream();
            fos = new FileOutputStream("received_" + filename); //File to save to
            bos = new BufferedOutputStream(fos);

        }
        catch (IOException e){
            System.out.println("ERROR: FileReceiver - Failed to get input/output streams ");
        }


        try{
            int bytesRead = in.read(bytes, 0, bytes.length);
            bos.write(bytes, 0, bytesRead);
            System.out.println("$ FileReceiver - File downloaded succesfully");
        }
        catch(IOException e){
            System.out.println("ERROR: FilReceiver - Failed to download file");
        }

        try{
            bos.close();
            in.close();
            socket.close();
        }
        catch(IOException e){
            System.out.println("ERROR: FileReceiver - Failed to close socket");
        }



    }

}
