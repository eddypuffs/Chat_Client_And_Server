import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * Created by ENG on 4/1/17.
 */

/*
    This message queue will only be handling the outgoing messages of type "MSG"
    REGISTER, LOGIN, DISCONNECT and CLIST is handled by ClientHandler.java
 */

public class MessageQueue implements Runnable{

    private Deque<Message> queue;
    public ClientHandlerList clientList;

    MessageQueue(ClientHandlerList clientList){
        this.queue = new LinkedList<>();
        this.clientList = clientList;
        Thread t = new Thread(this);
        t.start();
    }

    public void run(){ //The runnable will take continuously be taking the first element in the queue and sending it to clients

        while(true){
            try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
                if(!queue.isEmpty()){
                    Message messageToBroadcast = queue.remove();
                    if(messageToBroadcast!=null){
                        System.out.println("$ Broadcasting message '" + messageToBroadcast.mstring + "' from " + messageToBroadcast.sender);
                        clientList.broadcastMessage(messageToBroadcast);
                    }
            }
        }
    }



    public synchronized void addMessageToQ(ClientHandler sender, String msg){
        Message m = new Message(sender, msg);
        queue.add(m);
    }

    public synchronized String[] getClientList(){
        return clientList.getClientList();
    }



}