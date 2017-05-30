/**
 * Created by ENG on 3/31/17.
 */
public class ServerApp {
    public static void main(String[] args) {
        if(args.length!=1){
            System.out.println("$ ERROR: Arguments to server not provided correctly:");
            System.out.println("$    Usage: ./server <filename> ");
            return;
        }

        String loginFilePath = args[0];
        System.out.println("$ Opening filename: " + loginFilePath);
        ConnectionHandler s = new ConnectionHandler(1421, loginFilePath);

    }

}
