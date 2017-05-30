/**
 * Created by ENG on 4/1/17.
 */
public class Message {

    ClientHandler sender;
    String mstring;

    Message(ClientHandler sender, String mstring){
        this.sender = sender;
        this.mstring = mstring;
    }
}
