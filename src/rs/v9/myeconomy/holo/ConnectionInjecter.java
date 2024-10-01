package rs.v9.myeconomy.holo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rs.v9.myeconomy.shop.MyShop;

import java.lang.reflect.Field;

import static rs.v9.myeconomy.shop.ShopHandler.getShopByEntityId;

public class ConnectionInjecter {

    public static void injectPlayer(Player player){
        try{
            EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            ServerCommonPacketListenerImpl connection = nmsPlayer.c;

            Field f = ServerCommonPacketListenerImpl.class.getDeclaredField("e");
            f.setAccessible(true);

            NetworkManager networkManager = (NetworkManager) f.get(connection);

            f = networkManager.getClass().getDeclaredField("n");
            f.setAccessible(true);

            Channel channel = (Channel) f.get(networkManager);

            ChannelDuplexHandler duplexHandler = new ChannelDuplexHandler(){
                @Override
                public void channelRead(ChannelHandlerContext context, Object object)throws Exception {
                    if(object instanceof PacketPlayInUseEntity){
                        PacketPlayInUseEntity packet = (PacketPlayInUseEntity) object;

                        Field f = packet.getClass().getDeclaredField("b");
                        f.setAccessible(true);

                        MyShop shop = getShopByEntityId((int) f.get(packet));
                        if(shop != null){
                            shop.openMerchant(player);
                            return;
                        }
                    }

                    super.channelRead(context, object);
                }
            };

            channel.pipeline().addBefore("packet_handler", player.getName(), duplexHandler);

        }catch(NoSuchFieldException | IllegalAccessException e){
        }
    }

    public static void removePlayer(Player player){
        try{
            EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            ServerCommonPacketListenerImpl connection = nmsPlayer.c;

            Field f = ServerCommonPacketListenerImpl.class.getDeclaredField("e");
            f.setAccessible(true);

            NetworkManager networkManager = (NetworkManager) f.get(connection);

            f = networkManager.getClass().getDeclaredField("n");
            f.setAccessible(true);

            Channel channel = (Channel) f.get(networkManager);

            channel.eventLoop().submit(new Runnable(){
                @Override
                public void run(){
                    channel.pipeline().remove(player.getName());
                }
            });

        }catch(NoSuchFieldException | IllegalAccessException e){
        }
    }
}
