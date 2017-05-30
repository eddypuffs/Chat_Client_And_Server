import java.util.LinkedList;

/**
 * Created by ENG on 4/1/17.
 */
public class ClientHandlerList {
    private LinkedList<ClientHandler> clients;

    ClientHandlerList(){
        this.clients = new LinkedList<>();
    }


    public synchronized void addClient(ClientHandler c){
        clients.add(c);
    }

    public synchronized void removeClient(ClientHandler c){
        if(clients.contains(c)) clients.remove(c);
        c.closeConnection();
    }

    public String[] getClientList(){
        String[] list = new String[clients.size()];
        for (int i = 0; i < clients.size(); i++) {
            list[i] = clients.get(i).getID();
        }
        return list;
    }


    public synchronized void broadcastMessage(Message m){
        for (ClientHandler c : clients){
            if(!c.getID().equals(m.sender.getID())){
                String[] msgArray = {m.sender.getID() + ":" + m.mstring}; //Message is broadcasted in format <sender:message>
                c.sendMessageToClient(0x00, msgArray);
            }
        }
    }
}
