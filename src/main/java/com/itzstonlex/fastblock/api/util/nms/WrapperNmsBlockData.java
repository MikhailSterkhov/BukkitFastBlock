package com.itzstonlex.fastblock.api.util.nms;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.material.MaterialData;

@Getter
public class WrapperNmsBlockData implements NmsWrapper {

    private final Object handle;

    WrapperNmsBlockData(@NonNull Object handle) {
        this.handle = handle;
    }

    WrapperNmsBlockData(@NonNull MaterialData materialData) {
        this(NmsHelper.BLOCK_DATA_CLASS.cast(NmsHelper.getNmsBlockData(materialData)));
    }
}