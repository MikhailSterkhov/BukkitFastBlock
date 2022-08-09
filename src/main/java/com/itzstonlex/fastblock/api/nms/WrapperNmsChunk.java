package com.itzstonlex.fastblock.api.nms;

import com.itzstonlex.fastblock.api.util.ReflectionHelper;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Chunk;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Map;

@Getter
public class WrapperNmsChunk implements NmsWrapper {

    private final Object handle;

    private Object[] chunkSectionsArray;

    private Map<Object, Object> palettesByChunkMap;

    WrapperNmsChunk(@NonNull Object handle) {
        this.handle = handle;
    }

    public WrapperNmsChunk(@NonNull Chunk chunk) {
        this(NmsHelper.getNmsHandle(chunk));
    }

    public void clearSectionsArray() {
        chunkSectionsArray = null;
        palettesByChunkMap = null;
    }

    @SneakyThrows
    private Object findChunkSection(boolean chunksFlag, int y) {
        if (chunkSectionsArray == null) {
            chunkSectionsArray = (Object[]) ReflectionHelper.invoke(handle, "getSections");
        }

        int sectionIndex = y >> 4;

        Object section = null;

        if (sectionIndex < chunkSectionsArray.length) {
            section = chunkSectionsArray[sectionIndex];
        }
        else {
            int add = chunkSectionsArray.length == sectionIndex ? 1 : sectionIndex - chunkSectionsArray.length;

            chunkSectionsArray = Arrays.copyOf(chunkSectionsArray, chunkSectionsArray.length + add);
        }

        if (section == null) {
            section = NmsHelper.CHUCK_SECTION_CONSTRUCTOR.newInstance(y >> 4 << 4, chunksFlag);

            chunkSectionsArray[sectionIndex] = section;
        }

        return section;
    }

    public void setFastBlock(boolean chunksFlag, int x, int y, int z, @NonNull MaterialData materialData) {
        WrapperNmsBlockData blockData = NmsHelper.wrap(materialData);
        Object section = findChunkSection(chunksFlag, y);

        NmsHelper.invokeSetFastBlock(section, x & 15, y & 15, z & 15, blockData.getHandle());
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