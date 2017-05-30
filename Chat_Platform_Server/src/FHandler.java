import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by ENG on 4/22/17.
 */
public class FHandler {


    private int counter;
    private HashMap<String, LinkedList<Integer>> filetable = new HashMap<>(); //Maps filename -> List of file IDs
    private HashMap<Integer, String> hoststable = new HashMap<>(); //Maps file IDs to host "IP:PORT"


    FHandler(){ //Constructor
        this.counter = 1000;
    }

    synchronized int addFile(String host, String filename){ //Adds a file to filetable and filehosts table. Used by FPUT


        int newFileID = counter++;

        if(filetable.containsKey(filename)){
            filetable.get(filename).add(newFileID);
        }
        else{
            LinkedList<Integer> newls = new LinkedList<>();
            newls.add(newFileID);
            filetable.put(filename, newls);
        }

        hoststable.put(newFileID, host);

        return 0;
    }

    synchronized String[] getFileList(){ //Used by FLIST

        String[] ret = new String[filetable.keySet().size()*2];

        int idx = 0;

        synchronized ()
        for(String filename : filetable.keySet()){
            for(int fileID : filetable.get(filename)){

                ret[idx++] = Integer.toString(fileID); if(idx>ret.length) break; //TODO: Fix some synchronization problem here (out of bounds exception)
                ret[idx++] = filename; if(idx>ret.length) break;
            }
        }

        return ret;

    }

    synchronized String getHostForFileID(String fileID){ //Used by FGET
        return hoststable.get(Integer.parseInt(fileID));
    }

    synchronized boolean fileExists(String fileID){
        return hoststable.containsKey(Integer.parseInt(fileID));
    }

    synchronized String fileIDtoFilename(String fileID){
        int searchid = Integer.parseInt(fileID);

        for(String filename : filetable.keySet()){
            for(Integer id: filetable.get(filename)){
                if(searchid == id) return filename;
            }
        }

        return "";

    }





}
