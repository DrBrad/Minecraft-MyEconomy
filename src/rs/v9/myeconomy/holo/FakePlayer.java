package rs.v9.myeconomy.holo;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FakePlayer extends FakeEntity {

    public FakePlayer(Location location, String name){
        super(location, name);

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);

        entity = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) location.getWorld()).getHandle(),
                gameProfile,
                ClientInformation.a());

        ((EntityPlayer) entity).c = new PlayerConnection(((CraftServer) Bukkit.getServer()).getServer(),
                new NetworkManager(EnumProtocolDirection.a),
                (EntityPlayer) entity,
                CommonListenerCookie.a(((EntityPlayer) entity).fX(), false));
    }

    @Override
    public void display(Player player){
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        Class<ClientboundPlayerInfoUpdatePacket.a> enumClass = ClientboundPlayerInfoUpdatePacket.a.class;
        ClientboundPlayerInfoUpdatePacket.a[] enumConstants = enumClass.getEnumConstants();

        for(ClientboundPlayerInfoUpdatePacket.a constant : enumConstants){
            if(constant.name().equals("ADD_PLAYER")){
                ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(constant, (EntityPlayer) entity);
                nmsPlayer.c.sendPacket(packet);

                PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(
                        entity.an(),
                        entity.cz(),
                        player.getLocation().getX(),
                        player.getLocation().getY(),
                        player.getLocation().getZ(),
                        player.getLocation().getYaw(),
                        player.getLocation().getPitch(),
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

                break;
            }
        }
    }
}
