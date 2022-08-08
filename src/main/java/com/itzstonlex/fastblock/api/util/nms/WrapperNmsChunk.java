package com.itzstonlex.fastblock.api.util.nms;

import com.google.common.collect.Iterables;
import com.itzstonlex.fastblock.api.util.ReflectionHelper;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Chunk;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public class WrapperNmsChunk implements NmsWrapper {

    private final Object handle;

    private List<Object> chunkSectionsList;

    private Map<Object, Object> palettesByChunkMap;

    WrapperNmsChunk(@NonNull Object handle) {
        this.handle = handle;
    }

    public WrapperNmsChunk(@NonNull Chunk chunk) {
        this(NmsHelper.getNmsHandle(chunk));
    }

    public void clearSectionsArray() {
        chunkSectionsList = null;
        palettesByChunkMap = null;
    }

    @SneakyThrows
    private Object findChunkSection(boolean chunksFlag, int y) {
        if (chunkSectionsList == null) {

            palettesByChunkMap = new ConcurrentHashMap<>();

            chunkSectionsList = Arrays.stream((Object[]) ReflectionHelper.invoke(handle, "getSections")).filter(Objects::nonNull).collect(Collectors.toList());
            chunkSectionsList.forEach(chunkSection -> palettesByChunkMap.put(chunkSection, ReflectionHelper.invoke(chunkSection, "getBlocks")));
        }

        int chunkIndex = y >> 4;
        if (chunkIndex < 0) {
            chunkIndex = 0;
        }

        Object section = null;

        if (chunkIndex < chunkSectionsList.size()) {
            section = Iterables.get(chunkSectionsList, chunkIndex, null);
        }

        if (section == null) {
            section = NmsHelper.CHUCK_SECTION_CONSTRUCTOR.newInstance(y >> 4 << 4, chunksFlag);

            chunkSectionsList.add(chunkIndex, section);
            palettesByChunkMap.put(section, ReflectionHelper.invoke(section, "getBlocks"));
        }

        return section;
    }

    public Object getPalette(Object chunkSection) {
        return palettesByChunkMap.get(chunkSection);
    }

    public void setFastBlock(boolean chunksFlag, int x, int y, int z, @NonNull MaterialData materialData) {
        WrapperNmsBlockData blockData = NmsHelper.wrap(materialData);
        Object section = findChunkSection(chunksFlag, y);

        NmsHelper.invokeSetFastBlock(palettesByChunkMap.get(section), x & 15, y & 15, z & 15, blockData.getHandle());
    }

    public void setFastBlock(boolean chunksFlag, @NonNull Vector vector, @NonNull MaterialData materialData) {
        setFastBlock(chunksFlag, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ(), materialData);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        clearSectionsArray();
    }
}