package rs.v9.myeconomy.holo;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ambient.EntityBat;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.*;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglinBrute;
import net.minecraft.world.entity.npc.EntityVillager;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;

import java.util.Arrays;
import java.util.List;

public class MobResolver {

    public static Entity fromName(String type, Location location){
        Entity entity = null;
        switch(type){
            case "bat":
                entity = new EntityBat(EntityTypes.g, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "cat":
                entity = new EntityCat(EntityTypes.p, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "chicken":
                entity = new EntityChicken(EntityTypes.t, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "cod":
                entity = new EntityCod(EntityTypes.u, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "cow":
                entity = new EntityCow(EntityTypes.w, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "donkey":
                entity = new EntityHorseDonkey(EntityTypes.z, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "fox":
                entity = new EntityFox(EntityTypes.Q, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "horse":
                entity = new EntityHorse(EntityTypes.ab, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "mooshroom":
                entity = new EntityMushroomCow(EntityTypes.as, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "mule":
                entity = new EntityHorseMule(EntityTypes.at, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "ocelot":
                entity = new EntityOcelot(EntityTypes.au, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "parrot":
                entity = new EntityParrot(EntityTypes.ax, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "pig":
                entity = new EntityPig(EntityTypes.az, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "piglin":
                entity = new EntityPiglin(EntityTypes.aA, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "polar_bear":
                entity = new EntityPolarBear(EntityTypes.aD, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "rabbit":
                entity = new EntityRabbit(EntityTypes.aG, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "salmon":
                entity = new EntitySalmon(EntityTypes.aI, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "sheep":
                entity = new EntitySheep(EntityTypes.aJ, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "skeleton_horse":
                entity = new EntityHorseSkeleton(EntityTypes.aO, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "snow_golem":
                entity = new EntitySnowman(EntityTypes.aS, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "squid":
                entity = new EntitySquid(EntityTypes.aX, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "strider":
                entity = new EntityStrider(EntityTypes.aZ, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "tropical_fish":
                entity = new EntityTropicalFish(EntityTypes.bg, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "turtle":
                entity = new EntityTurtle(EntityTypes.bh, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "villager":
                entity = new EntityVillager(EntityTypes.bj, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "wandering_trader":
                //entity = new EntityChicken(EntityTypes.CHICKEN, ((CraftWorld) location.getWorld()).getHandle());
                break;

            case "bee":
                entity = new EntityBee(EntityTypes.h, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "cave_spider":
                entity = new EntityCaveSpider(EntityTypes.q, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "dolphin":
                entity = new EntityDolphin(EntityTypes.y, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "enderman":
                entity = new EntityEnderman(EntityTypes.H, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "iron_golem":
                entity = new EntityIronGolem(EntityTypes.af, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "llama":
                entity = new EntityLlama(EntityTypes.an, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "panda":
                entity = new EntityPanda(EntityTypes.aw, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "pufferfish":
                entity = new EntityPufferFish(EntityTypes.aF, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "spider":
                entity = new EntitySpider(EntityTypes.aW, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "wolf":
                entity = new EntityWolf(EntityTypes.bs, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "zombified_piglin":
                //entity = new EntityZombi(EntityTypes.CHICKEN, ((CraftWorld) location.getWorld()).getHandle());
                break;

            case "blaze":
                entity = new EntityBlaze(EntityTypes.i, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "creeper":
                entity = new EntityCreeper(EntityTypes.x, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "drowned":
                entity = new EntityDrowned(EntityTypes.B, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "elder_guardian":
                entity = new EntityGuardianElder(EntityTypes.D, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "endermite":
                entity = new EntityEndermite(EntityTypes.I, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "evoker":
                entity = new EntityEvoker(EntityTypes.J, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "ghast":
                entity = new EntityGhast(EntityTypes.T, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "guardian":
                entity = new EntityGuardian(EntityTypes.Y, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "hoglin":
                entity = new EntityHoglin(EntityTypes.Z, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "husk":
                entity = new EntityZombieHusk(EntityTypes.ac, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "magma_cube":
                entity = new EntityMagmaCube(EntityTypes.ap, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "phantom":
                entity = new EntityPhantom(EntityTypes.ay, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "piglin_brute":
                entity = new EntityPiglinBrute(EntityTypes.aB, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "pillager":
                entity = new EntityPillager(EntityTypes.aC, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "ravager":
                entity = new EntityRavager(EntityTypes.aH, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "shulker":
                entity = new EntityShulker(EntityTypes.aK, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "silverfish":
                entity = new EntitySilverfish(EntityTypes.aM, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "skeleton":
                entity = new EntitySkeleton(EntityTypes.aN, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "slime":
                entity = new EntitySlime(EntityTypes.aP, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "stray":
                entity = new EntitySkeletonStray(EntityTypes.aY, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "vindicator":
                entity = new EntityVindicator(EntityTypes.bk, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "witch":
                entity = new EntityWitch(EntityTypes.bo, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "wither_skeleton":
                entity = new EntitySkeletonWither(EntityTypes.bq, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "zoglin":
                entity = new EntityZoglin(EntityTypes.bt, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "zombie":
                entity = new EntityZombie(EntityTypes.bu, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "zombie_villager":
                entity = new EntityZombieVillager(EntityTypes.bw, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "ender_dragon":
                entity = new EntityEnderDragon(EntityTypes.F, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "wither":
                entity = new EntityWither(EntityTypes.bp, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "zombie_horse":
                entity = new EntityHorseZombie(EntityTypes.bv, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "giant":
                entity = new EntityGiantZombie(EntityTypes.U, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "illusioner":
                entity = new EntityIllagerIllusioner(EntityTypes.ad, ((CraftWorld) location.getWorld()).getHandle());
                break;
            case "armor_stand":
                entity = new EntityArmorStand(EntityTypes.d, ((CraftWorld) location.getWorld()).getHandle());
                break;
        }
        return entity;
    }

    public static List<String> getMobs(){
        String[] mobs = new String[]{
                "bat",
                "cat",
                "chicken",
                "cod",
                "cow",
                "donkey",
                "fox",
                "horse",
                "mooshroom",
                "mule",
                "ocelot",
                "parrot",
                "pig",
                "piglin",
                "polar_bear",
                "rabbit",
                "salmon",
                "sheep",
                "skeleton_horse",
                "snow_golem",
                "squid",
                "strider",
                "tropical_fish",
                "turtle",
                "villager",
                "wandering_trader",   ////////

                "bee",
                "cave_spider",
                "dolphin",
                "enderman",
                "iron_golem",
                "llama",
                "panda",
                "pufferfish",
                "spider",
                "wolf",
                "zombified_piglin",   //////

                "blaze",
                "creeper",
                "drowned",
                "elder_guardian",
                "endermite",
                "evoker",
                "ghast",
                "guardian",
                "hoglin",
                "husk",
                "magma_cube",
                "phantom",
                "piglin_brute",
                "pillager",
                "ravager",
                "shulker",
                "silverfish",
                "skeleton",
                "slime",
                "stray",
                "vindicator",
                "witch",
                "wither_skeleton",
                "zoglin",
                "zombie",
                "zombie_villager",
                "ender_dragon",
                "wither",
                "zombie_horse",
                "giant",
                "illusioner",
                "armor_stand"
        };

        return Arrays.asList(mobs);
    }
}
