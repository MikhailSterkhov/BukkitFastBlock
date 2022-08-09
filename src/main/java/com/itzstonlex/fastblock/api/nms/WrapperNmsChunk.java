package com.itzstonlex.fastblock.api.nms;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Chunk;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.Map;

@Getter
public class WrapperNmsChunk implements NmsWrapper {

    private final Object handle;

    private Object[] chunkSectionsArray;

    WrapperNmsChunk(@NonNull Object handle) {
        this.handle = handle;
    }

    public WrapperNmsChunk(@NonNull Chunk chunk) {
        this(NmsHelper.getNmsHandle(chunk));
    }

    @SneakyThrows
    private Object findChunkSection(boolean chunksFlag, int y) {
        if (chunkSectionsArray == null) {
            chunkSectionsArray = (Object[]) NmsHelper.METHOD_GET_CHUNK_SECTIONS.invoke(handle);
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
            section = NmsHelper.CONSTRUCTOR_CHUCK_SECTION.invoke(y >> 4 << 4, chunksFlag);

            chunkSectionsArray[sectionIndex] = section;

        }

        return section;
    }

    public void setFastBlock(boolean chunksFlag, int x, int y, int z, @NonNull MaterialData materialData) {
        WrapperNmsBlockData blockData = NmsHelper.wrap(materialData);
        Object section = findChunkSection(chunksFlag, y);

        NmsHelper.invokeSetFastBlock(section, x & 15, y & 15, z & 15, blockData.getHandle());
    }

    public void flush() {
        chunkSectionsArray = null;
    }

}