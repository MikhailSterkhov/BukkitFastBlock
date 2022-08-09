package com.itzstonlex.fastblock.api.nms;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.World;

import java.lang.invoke.MethodType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class WrapperNmsWorld implements NmsWrapper {

    private final Set<Map.Entry<Integer, Integer>> cachedChunksCoordinates = new HashSet<>();
    private final Map<Integer, WrapperNmsChunk> chunksMap = new ConcurrentHashMap<>();

    private final Object handle;

    private Boolean chunksFlag;

    WrapperNmsWorld(@NonNull Object handle) {
        this.handle = handle;
    }

    public WrapperNmsWorld(@NonNull World world) {
        this(NmsHelper.getNmsHandle(world));
    }

    public WrapperNmsChunk getNmsChunkAt(int x, int z) {
        int chX = (x >> 4);
        int chZ = (z >> 4);

        return chunksMap.computeIfAbsent(chX + chZ, (sum) -> {

            cachedChunksCoordinates.add(Maps.immutableEntry(x, z));
            return new WrapperNmsChunk(NmsHelper.getNmsChunkAt(handle, chX, chZ));
        });
    }

    @SneakyThrows
    public void refreshChunkAt(int x, int z) {
        int chX = (x >> 4);
        int chZ = (z >> 4);

        Object nmsPlayerChunkMap = NmsHelper.LOOKUP.findVirtual(NmsHelper.WORLD_SERVER_CLASS, "getPlayerChunkMap", MethodType.methodType(NmsHelper.PLAYER_CHUNK_MAP_CLASS))
                .invoke(handle);

        NmsHelper.LOOKUP.findVirtual(NmsHelper.PLAYER_CHUNK_MAP_CLASS, "a", MethodType.methodType(boolean.class, int.class, int.class)).invoke(nmsPlayerChunkMap, chX, chZ);
    }

    @SneakyThrows
    public boolean getChunksFlag() {
        if (chunksFlag == null) {
            chunksFlag = (Boolean) NmsHelper.LOOKUP.findVirtual(NmsHelper.WORLD_PROVIDER_CLASS, "m", MethodType.methodType(boolean.class))
                    .invoke(NmsHelper.LOOKUP.findGetter(NmsHelper.WORLD_SERVER_CLASS, "worldProvider", NmsHelper.WORLD_PROVIDER_CLASS).invoke(handle));
        }

        return chunksFlag;
    }

    public void flush() {
        chunksMap.forEach((integer, wrapperNmsChunk) -> wrapperNmsChunk.flush());
        chunksMap.clear();

        cachedChunksCoordinates.forEach(chunkEntry -> refreshChunkAt(chunkEntry.getKey(), chunkEntry.getValue()));
        cachedChunksCoordinates.clear();
    }

}