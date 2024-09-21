package rs.v9.myeconomy.claim;

import rs.v9.myeconomy.group.Group;

public class AutoClaim {

    private Group group;
    private boolean claiming;
    private String key;

    public AutoClaim(Group group, boolean claiming, String key){
        this.group = group;
        this.claiming = claiming;
        this.key = key;
    }

    public Group getGroup(){
        return group;
    }

    public boolean isClaiming(){
        return claiming;
    }

    public void setLastLocation(String key){
        this.key = key;
    }

    public String getLastLocation(){
        return key;
    }
}