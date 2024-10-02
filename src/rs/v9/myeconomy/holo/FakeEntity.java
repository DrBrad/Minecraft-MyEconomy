package rs.v9.myeconomy.holo;

import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public abstract class FakeEntity {

    protected Location location;
    protected String name;
    protected Entity entity;

    public FakeEntity(Location location, String name){
        this.location = location;
        this.name = name;
    }

    public Location getLocation(){
        return location;
    }

    public int getEntityId(){
        if(entity == null){
            return -1;
        }
        return entity.an();
    }

    public abstract void display(Player player);

    public void reload(Player player){
    }

    public void kill(){
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entity.an());

        for(Player player : location.getWorld().getPlayers()){
            EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            nmsPlayer.c.sendPacket(destroy);
        }
    }
}
