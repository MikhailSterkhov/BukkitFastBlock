package com.itzstonlex.fastblock.api.nms;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.itzstonlex.fastblock.api.util.ReflectionHelper;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

// by @itzstonlex

@SuppressWarnings({"SameParameterValue", "UnusedReturnValue"})
@UtilityClass
public class NmsHelper {

    public final Cache<Object, Object> objectsHandlesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    private final Map<Object, NmsWrapper> wrappersByHandleMap = new ConcurrentHashMap<>();

    public static final String NMS_PACKAGE_VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];

    public static final String NMS_VERSION = NMS_PACKAGE_VERSION.substring(1, 3).replace("_", ".");

    public static final Class<?> CHUNK_SECTION_CLASS = getNmsType("ChunkSection");
    public static final Class<?> BLOCK_DATA_CLASS = getNmsType("IBlockData");

    public static final Class<?> DATA_PALETTE_BLOCK_CLASS = getNmsType("DataPaletteBlock");
    public static final Class<?> BLOCK_CLASS = getNmsType("Block");

    public static final Constructor<?> CHUCK_SECTION_CONSTRUCTOR;

    public static final Method SET_BLOCK_FAST_METHOD;

    static {
        try {
            CHUCK_SECTION_CONSTRUCTOR = CHUNK_SECTION_CLASS.getConstructor(int.class, boolean.class);
            SET_BLOCK_FAST_METHOD = CHUNK_SECTION_CLASS.getDeclaredMethod("setType", int.class, int.class, int.class, BLOCK_DATA_CLASS);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public WrapperNmsWorld wrap(@NonNull World world) {
        return (WrapperNmsWorld) wrappersByHandleMap.computeIfAbsent(world, (f) -> new WrapperNmsWorld(world));
    }

    public WrapperNmsChunk wrap(@NonNull Chunk chunk) {
        return (WrapperNmsChunk) wrappersByHandleMap.computeIfAbsent(chunk, (f) -> new WrapperNmsChunk(chunk));
    }

    public WrapperNmsBlockData wrap(@NonNull MaterialData materialData) {
        return (WrapperNmsBlockData) wrappersByHandleMap.computeIfAbsent(materialData, (f) -> new WrapperNmsBlockData(materialData));
    }

    @SneakyThrows
    public Class<?> getNmsType(@NonNull String className) {
        return Class.forName("net.minecraft.server." + NMS_PACKAGE_VERSION + "." + className);
    }

    @SneakyThrows
    public Object getNmsHandle(Object src) {
        objectsHandlesCache.cleanUp();
        ConcurrentMap<Object, Object> asMap = objectsHandlesCache.asMap();

        if (asMap.containsKey(src)) {
            return asMap.get(src);
        }

        Object handle = src.getClass().getDeclaredMethod("getHandle").invoke(src);
        objectsHandlesCache.put(src, handle);

        return handle;
    }

    @SneakyThrows
    public Object getNmsBlockData(MaterialData materialData) {
        return ReflectionHelper.invokeStatic(BLOCK_CLASS, "getByCombinedId", materialData.getItemTypeId() + (materialData.getData() << 12));
    }

    @SneakyThrows
    public void invokeSetFastBlock(Object src, int x, int y, int z, Object blockData) {
        SET_BLOCK_FAST_METHOD.invoke(src, x, y, z, blockData);
    }
}