package rs.v9.myeconomy.holo;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

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

    public int getEntityId(){
        if(entity == null){
            return -1;
        }
        return entity.an();
    }

    public void display(Player player){
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(
                entity.an(),
                entity.cz(),
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

        PacketPlayOutEntity.PacketPlayOutEntityLook lookPacket = new PacketPlayOutEntity.PacketPlayOutEntityLook(entity.an(), (byte) 180, (byte) 0, true);

        PacketPlayOutEntityHeadRotation rotationPacket = new PacketPlayOutEntityHeadRotation(entity, (byte) (location.getYaw() * 255F / 360F));

        nmsPlayer.c.sendPacket(spawnPacket);
        nmsPlayer.c.sendPacket(lookPacket);
        nmsPlayer.c.sendPacket(rotationPacket);

        try{
            Field f = Entity.class.getDeclaredField("ao");
            f.setAccessible(true);

            DataWatcher watcher = (DataWatcher) f.get(entity);
            PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(entity.an(), watcher.c());
            nmsPlayer.c.sendPacket(metaPacket);

        }catch(NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
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
