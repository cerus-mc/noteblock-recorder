/*
 *  Copyright (c) 2018 Cerus
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Cerus
 *
 */

package de.cerus.noteblockrecorder.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ItemBuilder {

    private ItemStack stack;
    private Map<Character, Color> leatherColors;

    
    public ItemBuilder(Material mat) {
        this();
        this.stack = new ItemStack(mat, 1);
    }

    
    public ItemBuilder(Material mat, int subID) {
        this();
        this.stack = new ItemStack(mat, (short) subID);
    }

    
    public ItemBuilder(ItemStack stack) {
        this();
        this.stack = stack;
    }

    private ItemBuilder() {
        this.leatherColors = new HashMap<>();
        leatherColors.put('1', Color.BLUE);
        leatherColors.put('2', Color.GREEN);
        leatherColors.put('3', Color.TEAL);
        leatherColors.put('4', Color.RED);
        leatherColors.put('5', Color.PURPLE);
        leatherColors.put('6', Color.ORANGE);
        leatherColors.put('7', Color.SILVER);
        leatherColors.put('8', Color.GRAY);
        leatherColors.put('9', Color.NAVY);
        leatherColors.put('0', Color.BLACK);
        leatherColors.put('e', Color.YELLOW);
        leatherColors.put('a', Color.LIME);
        leatherColors.put('d', Color.FUCHSIA);
        leatherColors.put('f', Color.WHITE);
        leatherColors.put('c', Color.MAROON);
        leatherColors.put('b', Color.AQUA);
    }

    
    @Deprecated
    public ItemBuilder setAmount(int amount) {
        return withAmount(amount);
    }

    
    public ItemBuilder withAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    
    @Deprecated
    public ItemBuilder setSubID(int subID) {
        return withSubId(subID);
    }

    
    public ItemBuilder withSubId(int subID) {
        stack.setDurability((short) subID);
        return this;
    }

    
    @Deprecated
    public ItemBuilder addEnchantmentGlowing() {
        return withGlowing();
    }

    
    public ItemBuilder withGlowing() {
        stack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        addFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    
    @Deprecated
    public ItemBuilder setMeta(ItemMeta meta) {
        return withItemMeta(meta);
    }

    
    public ItemBuilder withItemMeta(ItemMeta meta) {
        stack.setItemMeta(meta);
        return this;
    }

    @Deprecated
    public ItemBuilder setLeatherColor(Color color) {
        return withLeatherColor(color);
    }

    @Deprecated
    public ItemBuilder setLeatherColor(char colorKey) {
        return withLeatherColor(colorKey);
    }

    public ItemBuilder withLeatherColor(Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
        meta.setColor(color);
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withLeatherColor(char colorKey) {
        Color color = leatherColors.get(colorKey);
        LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
        meta.setColor(color);
        stack.setItemMeta(meta);
        return this;
    }

    
    @Deprecated
    public ItemBuilder setSkullOwnerID(String id) {
        try {
            Class<?> gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
            Constructor<?> constr = gameProfileClass.getDeclaredConstructor(UUID.class, String.class);
            Object gameProfile = constr.newInstance(UUID.randomUUID(), "");

            Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
            Object property = gameProfileClass.getDeclaredMethod("getProperties").invoke(gameProfile);
            propertyClass.getDeclaredMethod("removeAll", String.class).invoke(property, "textures");
            propertyClass.getDeclaredMethod("put", String.class, propertyClass).invoke(property, "textures",
                    propertyClass.getDeclaredConstructor(String.class, String.class, String.class).newInstance("textures", id, null));

            SkullMeta im = (SkullMeta) stack.getItemMeta();

            Field profileField = null;
            profileField = im.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(im, gameProfile);

            stack.setItemMeta(im);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException
                | IllegalAccessException | NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return this;
    }

    
    @Deprecated
    public ItemBuilder setSkullOwner(String owner) {
        SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();
        skullMeta.setOwner(owner);
        stack.setItemMeta(skullMeta);
        return this;
    }

    
    @Deprecated
    public ItemBuilder setDisplayname(String name) {
        return withName(name);
    }

    
    public ItemBuilder withName(String name) {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        return this;
    }

    
    @Deprecated
    public ItemBuilder setMaterial(Material material) {
        return withMaterial(material);
    }

    
    public ItemBuilder withMaterial(Material material) {
        stack.setType(material);
        return this;
    }

    
    @Deprecated
    public ItemBuilder setLore(List<String> lore) {
        return withLore(lore);
    }

    
    @Deprecated
    public ItemBuilder setLore(String lore) {
        return withLore(lore);
    }

    
    @Deprecated
    public ItemBuilder setLore(String[] lore) {
        return withLore(lore);
    }


    
    public ItemBuilder withLore(List<String> lore) {
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return this;
    }

    
    public ItemBuilder withLore(String lore) {
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(Collections.singletonList(lore));
        stack.setItemMeta(meta);
        return this;
    }

    
    public ItemBuilder withLore(String... lore) {
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
        return this;
    }

    
    @Deprecated
    public ItemBuilder addEchantment(Enchantment ench, int level) {
        return withUnsafeEnchantment(ench, level);
    }

    
    public ItemBuilder withUnsafeEnchantment(Enchantment ench, int level) {
        stack.addUnsafeEnchantment(ench, level);
        return this;
    }

    
    @Deprecated
    public ItemBuilder addFlags(ItemFlag flag) {
        return withFlags(flag);
    }

    
    public ItemBuilder withFlags(ItemFlag... flags) {
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(flags);
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addBookEnchantment(Enchantment enchantment) {
        return addBookEnchantment(enchantment, 1, true);
    }

    public ItemBuilder addBookEnchantment(Enchantment enchantment, int level) {
        return addBookEnchantment(enchantment, level, true);
    }

    public ItemBuilder addBookEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
        meta.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearBookEnchantments() {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
        meta.getStoredEnchants().forEach((enchantment, integer) -> meta.removeStoredEnchant(enchantment));
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeBookEnchantment(Enchantment enchantment) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
        meta.removeStoredEnchant(enchantment);
        stack.setItemMeta(meta);
        return this;
    }

    
    public ItemStack build() {
        return stack;
    }

}
