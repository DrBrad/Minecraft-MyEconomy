package rs.v9.myeconomy.holo;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NPC {

    public NPC(Player player){
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        UUID uuid = UUID.randomUUID();

        GameProfile gameProfile = new GameProfile(uuid, "PLAYER");

        EntityPlayer npc = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) player.getWorld()).getHandle(),
                gameProfile,
                ClientInformation.a());

        npc.c = new PlayerConnection(((CraftServer) Bukkit.getServer()).getServer(),
                new NetworkManager(EnumProtocolDirection.a),
                npc,
                CommonListenerCookie.a(gameProfile, false));

        Class<ClientboundPlayerInfoUpdatePacket.a> enumClass = ClientboundPlayerInfoUpdatePacket.a.class;

        ClientboundPlayerInfoUpdatePacket.a[] enumConstants = enumClass.getEnumConstants();

        for(ClientboundPlayerInfoUpdatePacket.a constant : enumConstants){
            if(constant.name().equals("ADD_PLAYER")){
                ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(constant, npc);
                nmsPlayer.c.sendPacket(packet);

                PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(
                        npc.an(),
                        npc.cz(),
                        player.getLocation().getX(),
                        player.getLocation().getY(),
                        player.getLocation().getZ(),
                        player.getLocation().getYaw(),
                        player.getLocation().getPitch(),
                        npc.am(),
                        0,
                        new Vec3D(0, 0, 0),
                        0
                );
                nmsPlayer.c.sendPacket(spawnPacket);

                break;
            }
        }
    }
}
