package rs.v9.myeconomy.handlers;

import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.List;

public class MobHandler {

    public static List<EntityType> getAllowedShops(){
        EntityType[] tmp = {
                EntityType.ZOMBIE,
                EntityType.PIGLIN,
                EntityType.PIGLIN_BRUTE,
                EntityType.ZOMBIE_VILLAGER,
                EntityType.ZOMBIFIED_PIGLIN,
                EntityType.DROWNED,
                EntityType.WANDERING_TRADER,
                EntityType.PILLAGER,
                EntityType.VINDICATOR,
                EntityType.ILLUSIONER,
                EntityType.SKELETON,
                EntityType.WITHER_SKELETON,
                EntityType.SNOW_GOLEM,
                EntityType.IRON_GOLEM,
                EntityType.EVOKER,
                EntityType.WARDEN,
                EntityType.VILLAGER
        };

        return Arrays.asList(tmp);
    }
}
