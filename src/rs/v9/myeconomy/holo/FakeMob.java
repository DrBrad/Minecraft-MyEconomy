package rs.v9.myeconomy.holo;

import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

import static rs.v9.myeconomy.holo.MobResolver.fromName;

public class FakeMob extends FakeEntity {

    private String type;

    public FakeMob(Location location, String type, String name){
        super(location, name);
        this.type = type;

        entity = fromName(type, location);
        entity.b(CraftChatMessage.fromStringOrNull("§a"+name));
        entity.p(true);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;

        if(name != null){
            entity.b(CraftChatMessage.fromStringOrNull("§a"+name));
            entity.p(true);
        }
    }

    public String getType(){
        return type;
    }

    @Override
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
}
