package com.itzstonlex.fastblock.api.nms;

import com.itzstonlex.fastblock.api.util.ReflectionHelper;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class WrapperNmsWorld implements NmsWrapper {

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
        return chunksMap.computeIfAbsent((x >> 4) + (z >> 4), (sum) -> new WrapperNmsChunk(ReflectionHelper.invoke(handle, "getChunkAt", x >> 4, z >> 4)));
    }

    public boolean getChunksFlag() {
        if (chunksFlag == null) {
            chunksFlag = (Boolean) ReflectionHelper.invokeMoved(handle, "worldProvider", "m");
        }

        return chunksFlag;
    }

    public void clearChunksMap() {
        chunksMap.forEach((integer, wrapperNmsChunk) -> wrapperNmsChunk.clearSectionsArray());
        chunksMap.clear();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        chunksFlag = null;
        clearChunksMap();
    }
}