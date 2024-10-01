package rs.v9.myeconomy.holo;

import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import static rs.v9.myeconomy.holo.MobResolver.fromName;

public class FakeMob {

    private Location location;
    private String type, name, command;
    private Entity entity;

    public FakeMob(Location location, String type, String name){
        this.location = location;
        this.type = type;
        this.name = name;
    }

    public Location getLocation(){
        return location;
    }

    public String getType(){
        return type;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;

        if(name != null){
            entity.b(name);
            entity.p(true);
        }
    }

    public String getCommand(){
        return command;
    }

    public void setCommand(String command){
        this.command = command;
    }

    public int getEntityId(){
        if(entity == null){
            return -1;
        }
        return entity.an();
    }

    public void display(Player player){
        PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(
                entity.an(),
                entity.cz(),//.cd,
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch(),
                entity.am(),
                0,
                new Vec3D(0, 0, 0),
                0
        );

        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.c.sendPacket(packet);
    }

    public void reload(Player player){
        /*
        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(entity.getEntityId(), entity.getDataWatcher(), true);

        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.playerConnection.sendPacket(metadata);
        if(equipmentList.size() > 0){
            PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(entity.getId(), equipmentList);
            nmsPlayer.playerConnection.sendPacket(equipment);
        }
        */
    }

    public int createEntity(){
        if(entity != null){
            return entity.an();
        }

        entity = fromName(type, location);

        return entity.an();
    }

    public void kill(){
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entity.an());

        for(Player player : location.getWorld().getPlayers()){
            EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            nmsPlayer.c.sendPacket(destroy);
        }
    }
}
