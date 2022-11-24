package me.rockquiet.spawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class ConfigManger {

    public void load() {
        Spawn.getPlugin().getConfig().options().copyDefaults(true);
        Spawn.getPlugin().saveConfig();

        if (Spawn.getPlugin().getConfig().options().getHeader().isEmpty()) {
            final List<String> header = new ArrayList<>();

            header.add(0, "---------------------------------------------------- #");
            header.add(1, "                 Spawn by rockquiet                  #");
            header.add(2, "---------------------------------------------------- #");
            header.add(3, "   Wiki - https://github.com/rockquiet/Spawn/wiki    #");
            header.add(4, "---------------------------------------------------- #");

            Spawn.getPlugin().getConfig().options().setHeader(header);
            Spawn.getPlugin().saveConfig();
        }
    }

    public void reload() {
        Spawn.getPlugin().reloadConfig();
    }

    public void save() {
        Spawn.getPlugin().saveConfig();
    }

    public void set(String path, Object value) {
        Spawn.getPlugin().getConfig().set(path, value);
    }

    public String getString(String path) {
        if (Spawn.getPlugin().getConfig().contains(path) && Spawn.getPlugin().getConfig().getString(path) != null) {
            return Spawn.getPlugin().getConfig().getString(path);
        } else {
            return "";
        }
    }

    public Boolean getBoolean(String path) {
        if (Spawn.getPlugin().getConfig().contains(path)) {
            return Spawn.getPlugin().getConfig().getBoolean(path);
        }
        return null;
    }

    public Integer getInt(String path) {
        if (Spawn.getPlugin().getConfig().contains(path)) {
            return Spawn.getPlugin().getConfig().getInt(path);
        }
        return null;
    }

    public Double getDouble(String path) {
        if (Spawn.getPlugin().getConfig().contains(path)) {
            return Spawn.getPlugin().getConfig().getDouble(path);
        }
        return null;
    }

    public float getFloat(String path) {
        if (Spawn.getPlugin().getConfig().contains(path)) {
            return (float) Spawn.getPlugin().getConfig().getDouble(path);
        }
        return 0;
    }

    public World getWorld(String path) {
        if (Spawn.getPlugin().getConfig().contains(path)) {
            return Bukkit.getWorld(Spawn.getPlugin().getConfig().getString(path));
        }
        return null;
    }

    public Location getLocation(World world, Double x, Double y, Double z, Float yaw, Float pitch) {
        return new Location(world, x, y, z, yaw, pitch);
    }
}
