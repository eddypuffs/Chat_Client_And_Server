import java.io.*;
import java.util.HashMap;

/**
 * Created by ENG on 4/1/17.
 */
public class ClientLogin {
    File f;
    HashMap<String,String> clientLogins; //Maps usernames to passwords

    ClientLogin(String pathToFile){
            f = new File(pathToFile);
            clientLogins = new HashMap<>();
            initializeMap();
    }

    public void initializeMap() { //Updates hashmap entries to what exists in file
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(f));
            String line = null;
            while((line = reader.readLine()) != null){
                if(line.length()<3) break;

                String[] lineArr = line.split(",");

                if(lineArr.length!=2){
                    String msg = "ERROR: Invalid format for line containing login: " + line + System.lineSeparator();
                    msg += "Lines must only contain a login and a password, seperated by a comma.";
                    throw new IOException(msg);
                }
                clientLogins.put(lineArr[0],lineArr[1]);
                if (reader == null) reader.close();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR: File not found: " + e.getMessage());
            if (reader != null) try{reader.close();} catch(IOException e2){ }
        }
        catch (IOException e) {
            if (reader != null) try{reader.close();} catch(IOException e2){ }
            e.printStackTrace();
        }
    }

    public synchronized int registerUser(String ID, String pass){

        //Check if user already exists
        if(clientLogins.containsKey(ID)){
            System.out.println("$ ERROR: Can not register user with exisitng ID " + ID);
            return 0x02;
        }


        //Update the hashmap
        clientLogins.put(ID, pass);

        //Update the file
        FileWriter fw = null;

        try{
            String newFileLine = ID + "," + pass + "\n";

            fw = new FileWriter(f,true);
            fw.write(newFileLine);
            fw.close();
        }
        catch(IOException e){
            System.out.println("$ ERROR: Unable to update file with new user entry: " + e.getMessage());
        }
        finally {
            try
            {
                if (fw != null) fw.close();
            }
            catch (IOException e) {
                System.out.println("$ ERROR: Unable to close file writer: " + e.getMessage());
            }
            return 0x00;
        }
    }

    public synchronized int validateLogin(String ID, String pass){

        if(clientLogins.containsKey(ID) && clientLogins.get(ID).equals(pass)){
            return 0x00;
        }
        else{
            return 0x01;
        }

    }
}
