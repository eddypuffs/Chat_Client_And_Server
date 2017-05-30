import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by ENG on 4/23/17.
 */

public class FileServer implements Runnable{
    private ServerSocket server = null;
    private Socket sock = null;
    private String filename = null; //Name of the file that is hosted by this server

    public FileServer(int port, String filename) {


        try {
            System.out.println("$ Instantiating file server for file:" + filename + " on port " + port);
            this.server = new ServerSocket(port);
            System.out.println("$ FileServer created: " + server);
        } catch (IOException e) {
            System.out.println("$ ERROR - FileServer Failed to instantiate server socket at port " + port + ": " + e.getMessage());

        }

        this.filename = filename;
    }


    public void run(){

        //Continually try to form a connection with a new client

        File projectDir = new File(System.getProperty("user.dir"));
        File f = new File(projectDir.getAbsolutePath() + "/" + filename);

        System.out.println("$ FileServer hosting file in path " + f.getAbsolutePath());

        while (!server.isClosed()) {
            System.out.println("$ File server waiting for another client to bind");

            try {
                //Try to get a new socket connection
                try {
                    sock = server.accept();
                    System.out.println("$ Accepted connection with " + sock + " requesting file " + filename);
                } catch (IOException ex) {
                    System.out.println("Can't accept client connection. ");
                }



                byte[] bytearray = new byte[(int) f.length()];
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
                bis.read(bytearray, 0, bytearray.length);
                OutputStream os = sock.getOutputStream();
                os.write(bytearray, 0, bytearray.length);
                os.flush();


                System.out.println("$ File server sent " + bytearray.length + " bytes if all went well");

                sock.close();

            } catch (IOException e) {
                System.out.println("$ ERROR: File sender crashed :  " + e.getMessage());
            }
        }

    }

}
