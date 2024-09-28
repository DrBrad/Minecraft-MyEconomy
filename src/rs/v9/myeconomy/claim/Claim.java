package rs.v9.myeconomy.claim;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Claim implements Serializable {

    private UUID key;
    private int type;
    private List<Flags> flags;

    public Claim(UUID key, int type, List<Flags> flags){
        this.key = key;
        this.type = type;
        this.flags = flags;
    }

    public void setKey(UUID key){
        this.key = key;
    }

    public UUID getKey(){
        return key;
    }

    public void setType(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }

    public int getTotalFlags(){
        return flags.size();
    }

    public List<Flags> getFlags(){
        return flags;
    }

    public boolean addFlag(Flags flag){
        return flags.add(flag);
    }

    public boolean removeFlag(Flags flag){
        flags.remove(flag);
        return true;
    }

    public boolean hasFlag(Flags flag){
        return flags.contains(flag);
    }
}
