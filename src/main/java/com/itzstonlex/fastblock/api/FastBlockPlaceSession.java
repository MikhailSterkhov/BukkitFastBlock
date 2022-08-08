package com.itzstonlex.fastblock.api;

import com.itzstonlex.fastblock.api.util.nms.NmsHelper;
import com.itzstonlex.fastblock.api.util.nms.WrapperNmsChunk;
import com.itzstonlex.fastblock.api.util.nms.WrapperNmsWorld;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.concurrent.CompletableFuture;

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

        WrapperNmsChunk nmsChunk = wrapperNmsWorld.getNmsChunkAt(block.getX(), block.getZ());
        nmsChunk.setFastBlock(wrapperNmsWorld.getChunksFlag(), block.getLocation().toVector(), materialData);

        block.getState().update();

        return this;
    }

    public FastBlockPlaceSession setBlockData(@NonNull Location location, @NonNull MaterialData materialData) {
        return setBlockData(location.getBlock(), materialData);
    }

    public FastBlockPlaceSession setBlockData(@NonNull World world, @NonNull Vector vector, @NonNull MaterialData materialData) {
        return setBlockData(vector.toLocation(world), materialData);
    }

    public FastBlockPlaceSession setBlockType(@NonNull Block block, @NonNull Material type) {
        return setBlockData(block, new MaterialData(type));
    }

    public FastBlockPlaceSession setBlockType(@NonNull Location location, @NonNull Material type) {
        return setBlockType(location.getBlock(), type);
    }

    public FastBlockPlaceSession setBlockType(@NonNull World world, @NonNull Vector vector, @NonNull Material type) {
        return setBlockType(vector.toLocation(world), type);
    }

    public FastBlockPlaceSession setBlockTypeAndData(@NonNull Block block, @NonNull Material type, int data) {
        return setBlockData(block, new MaterialData(type, (byte) data));
    }

    public FastBlockPlaceSession setBlockTypeAndData(@NonNull Location location, @NonNull Material type, int data) {
        return setBlockTypeAndData(location.getBlock(), type, data);
    }

    public FastBlockPlaceSession setBlockTypeAndData(@NonNull World world, @NonNull Vector vector, @NonNull Material type, int data) {
        return setBlockTypeAndData(vector.toLocation(world), type, data);
    }

    public FastBlockPlaceSession setBlockTypeIdAndData(@NonNull Block block, int id, int data) {
        return setBlockData(block, new MaterialData(id, (byte) data));
    }

    public FastBlockPlaceSession setBlockTypeIdAndData(@NonNull Location location, int id, int data) {
        return setBlockTypeIdAndData(location.getBlock(), id, data);
    }

    public FastBlockPlaceSession setBlockTypeIdAndData(@NonNull World world, @NonNull Vector vector, int id, int data) {
        return setBlockTypeIdAndData(vector.toLocation(world), id, data);
    }
    
    public CompletableFuture<Long> flush() {
        wrapperNmsWorld.clearChunksMap();
        return CompletableFuture.completedFuture(System.currentTimeMillis() - startTimeMillis);
    }
}
