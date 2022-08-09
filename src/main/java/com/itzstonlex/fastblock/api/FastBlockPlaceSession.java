package com.itzstonlex.fastblock.api;

import com.itzstonlex.fastblock.api.nms.NmsHelper;
import com.itzstonlex.fastblock.api.nms.WrapperNmsChunk;
import com.itzstonlex.fastblock.api.nms.WrapperNmsWorld;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class FastBlockPlaceSession {

    private final WrapperNmsWorld wrapperNmsWorld;

    private Long startTimeMillis;

    public FastBlockPlaceSession(@NonNull World world) {
        this.wrapperNmsWorld = NmsHelper.wrap(world);
    }

    public FastBlockPlaceSession setBlockData(@NonNull Block block, @NonNull MaterialData materialData) {
        if (startTimeMillis == null) {
            startTimeMillis = System.currentTimeMillis();
        }

        if (block.getY() > block.getWorld().getMaxHeight()) {
            return this;
        }

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        WrapperNmsChunk nmsChunk = wrapperNmsWorld.getNmsChunkAt(x, z);
        nmsChunk.setFastBlock(wrapperNmsWorld.getChunksFlag(), x, y, z, materialData);

        return this;
    }

    public FastBlockPlaceSession setBlockData(@NonNull Location location, @NonNull MaterialData materialData) {
        if (startTimeMillis == null) {
            startTimeMillis = System.currentTimeMillis();
        }

        if (location.getBlockY() > location.getWorld().getMaxHeight()) {
            return this;
        }

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        WrapperNmsChunk nmsChunk = wrapperNmsWorld.getNmsChunkAt(x, z);
        nmsChunk.setFastBlock(wrapperNmsWorld.getChunksFlag(), x, y, z, materialData);

        return this;
    }

    public FastBlockPlaceSession setBlockData(@NonNull World world, @NonNull Vector vector, @NonNull MaterialData materialData) {
        return setBlockData(vector.toLocation(world), materialData);
    }

    public FastBlockPlaceSession setBlockType(@NonNull Block block, @NonNull Material type) {
        return setBlockData(block, new MaterialData(type));
    }

    public FastBlockPlaceSession setBlockType(@NonNull Location location, @NonNull Material type) {
        return setBlockData(location, new MaterialData(type));
    }

    public FastBlockPlaceSession setBlockType(@NonNull World world, @NonNull Vector vector, @NonNull Material type) {
        return setBlockType(vector.toLocation(world), type);
    }

    public FastBlockPlaceSession setBlockTypeAndData(@NonNull Block block, @NonNull Material type, int data) {
        return setBlockData(block, new MaterialData(type, (byte) data));
    }

    public FastBlockPlaceSession setBlockTypeAndData(@NonNull Location location, @NonNull Material type, int data) {
        return setBlockData(location, new MaterialData(type, (byte) data));
    }

    public FastBlockPlaceSession setBlockTypeAndData(@NonNull World world, @NonNull Vector vector, @NonNull Material type, int data) {
        return setBlockTypeAndData(vector.toLocation(world), type, data);
    }

    public FastBlockPlaceSession setBlockTypeIdAndData(@NonNull Block block, int id, int data) {
        return setBlockData(block, new MaterialData(id, (byte) data));
    }

    public FastBlockPlaceSession setBlockTypeIdAndData(@NonNull Location location, int id, int data) {
        return setBlockData(location, new MaterialData(id, (byte) data));
    }

    public FastBlockPlaceSession setBlockTypeIdAndData(@NonNull World world, @NonNull Vector vector, int id, int data) {
        return setBlockTypeIdAndData(vector.toLocation(world), id, data);
    }

    public long flush() {
        wrapperNmsWorld.flush();

        System.gc();
        return startTimeMillis > 0 ? System.currentTimeMillis() - startTimeMillis : -1L;
    }

}
