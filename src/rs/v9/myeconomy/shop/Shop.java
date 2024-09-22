package rs.v9.myeconomy.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class Shop {

    private Merchant merchant;
    private Inventory stock, received;

    public Shop(Inventory stock, Inventory received){
        this.stock = stock;
        this.received = received;

        Merchant merchant = Bukkit.createMerchant("Shop");

        List<MerchantRecipe> merchantRecipes = new ArrayList<MerchantRecipe>();
        MerchantRecipe recipe = new MerchantRecipe(new ItemStack(Material.ROTTEN_FLESH), 10000);
        recipe.setExperienceReward(false); // no experience rewards
        recipe.addIngredient(new ItemStack(Material.CHEST, 2));
        //recipe.addIngredient(buyItem2);
        merchantRecipes.add(recipe);

        merchant.setRecipes(merchantRecipes);
    }

    public Inventory getStock(){
        return stock;

        /*
        */
    }

    public Inventory getReceived(){
        return received;
    }
}
