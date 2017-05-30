/**
 * Created by ENG on 3/31/17.
 */
public class ClientApp {

    public static void main(String[] args) {

//        if(args.length!=2){
//            System.out.println("ERROR: Arguments to server not provided correctly:");
//            System.out.println("   Usage: java ClientApp <host> <port>");
//        }
//        else{
            Client client = new Client("0.0.0.0",1421);
            client.run();
//        }

    }

}