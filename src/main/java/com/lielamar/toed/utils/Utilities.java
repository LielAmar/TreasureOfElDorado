package com.lielamar.toed.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Random;

public class Utilities {

    public static Random random = new Random();

    public static boolean isSimilar(ItemStack first, ItemStack second) {
        if(first.getType() != second.getType()) return false;
        if(first.hasItemMeta() && !second.hasItemMeta() || !first.hasItemMeta() && second.hasItemMeta()) return false;
        if(first.getItemMeta().hasDisplayName() && !second.getItemMeta().hasDisplayName() || !first.getItemMeta().hasDisplayName() && second.getItemMeta().hasDisplayName()) return false;
        if(!first.getItemMeta().getDisplayName().equalsIgnoreCase(second.getItemMeta().getDisplayName())) return false;
        return first.getItemMeta().getLore().equals(second.getItemMeta().getLore());
    }

    public static ItemStack treasureOfElDoradoItem() {
        ItemStack toedItem = new ItemStack(Material.MAP);
        ItemMeta toedMeta = toedItem.getItemMeta();

        toedMeta.setDisplayName(ChatColor.GREEN + "Map to Treasure of El Dorado");
        toedMeta.setLore(Arrays.asList(ChatColor.GRAY + "Follow the beacon to your", ChatColor.GRAY + "Treasure of El Dorado!"));
        toedItem.setItemMeta(toedMeta);

        return toedItem;
    }

    public static ItemStack treasureOfElDoradoCraftItem() {
        ItemStack toedItem = new ItemStack(Material.MAP);
        ItemMeta toedMeta = toedItem.getItemMeta();

        toedMeta.setDisplayName(ChatColor.GREEN + "Treasure of El Dorado");
        toedMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Follow the map to your", ChatColor.GRAY + "Treasure of El Dorado", ChatColor.GRAY + "location", ChatColor.GRAY + "Your Treasure of El",
                ChatColor.GRAY + "Dorado will spawn on the", ChatColor.GRAY + "Opposite side of the map.", ChatColor.GRAY + "It will contain healing",
                ChatColor.GRAY + "items and materials to help", ChatColor.GRAY + "you", ChatColor.GRAY + "Can only be crafted below 5", ChatColor.GRAY + "hearts"));
        toedItem.setItemMeta(toedMeta);

        return toedItem;
    }

    public static ItemStack instantHealth3Item() {
        ItemStack item = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Splash Potion of Healing");
        meta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 1, 2), true);
        item.setItemMeta(meta);

        Potion pot = new Potion(1);
        pot.setSplash(true);
        pot.apply(item);

        return item;
    }

    public static ShapedRecipe treasureOfElDoradoRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(treasureOfElDoradoCraftItem());
        recipe.shape(" R ", "WCW", "S S");
        recipe.setIngredient('R', Material.WOOL, (short)14);
        recipe.setIngredient('W', Material.WOOL, (short)0);
        recipe.setIngredient('C', Material.CHEST);
        recipe.setIngredient('S', Material.STRING);

        return recipe;
    }

}
