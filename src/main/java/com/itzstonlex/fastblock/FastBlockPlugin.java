package com.itzstonlex.fastblock;

import com.itzstonlex.fastblock.api.FastBlockPlaceSession;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public final class FastBlockPlugin extends JavaPlugin {

    public static FastBlockPlaceSession createSession(@NonNull World world) {
        return new FastBlockPlaceSession(world);
    }

    public static FastBlockPlaceSession createSession(@NonNull Location location) {
        return createSession(location.getWorld());
    }

    public static FastBlockPlaceSession createSession(@NonNull Block block) {
        return createSession(block.getWorld());
    }

    public static FastBlockPlaceSession createSession(@NonNull Entity entity) {
        return createSession(entity.getWorld());
    }

    @Override
    public void onEnable() {
        getLogger().info(ChatColor.GREEN + "FastBlockAPI was enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "FastBlockAPI was disabled!");
    }

}
