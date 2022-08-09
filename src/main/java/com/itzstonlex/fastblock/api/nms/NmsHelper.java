package com.itzstonlex.fastblock.api.nms;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.material.MaterialData;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
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

    public final String NMS_PACKAGE_VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];

    public final String NMS_VERSION = NMS_PACKAGE_VERSION.substring(1, 3).replace("_", ".");

    public final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    public final Class<?> PLAYER_CHUNK_MAP_CLASS = getNmsType("PlayerChunkMap");
    public final Class<?> CHUNK_SECTION_CLASS = getNmsType("ChunkSection");
    public final Class<?> BLOCK_DATA_CLASS = getNmsType("IBlockData");
    public final Class<?> BLOCK_CLASS = getNmsType("Block");
    public final Class<?> CHUNK_CLASS = getNmsType("Chunk");
    public final Class<?> WORLD_SERVER_CLASS = getNmsType("WorldServer");
    public final Class<?> WORLD_PROVIDER_CLASS = getNmsType("WorldProvider");

    public final MethodHandle CONSTRUCTOR_CHUCK_SECTION;
    public final MethodHandle METHOD_SET_BLOCK_TYPE;
    public final MethodHandle METHOD_GET_CHUNK_SECTIONS;
    public final MethodHandle METHOD_GET_CHUNK_AT;
    public final MethodHandle METHOD_GET_BLOCK_DATA;

    static {
        try {
            CONSTRUCTOR_CHUCK_SECTION = LOOKUP.findConstructor(CHUNK_SECTION_CLASS, MethodType.methodType(void.class, int.class, boolean.class));

            METHOD_SET_BLOCK_TYPE = LOOKUP.findVirtual(CHUNK_SECTION_CLASS, "setType", MethodType.methodType(void.class, int.class, int.class, int.class, BLOCK_DATA_CLASS));
            METHOD_GET_CHUNK_SECTIONS = LOOKUP.findVirtual(CHUNK_CLASS, "getSections", MethodType.methodType(Array.newInstance(CHUNK_SECTION_CLASS, 16).getClass()));
            METHOD_GET_CHUNK_AT = LOOKUP.findVirtual(WORLD_SERVER_CLASS, "getChunkAt", MethodType.methodType(CHUNK_CLASS, int.class, int.class));

            METHOD_GET_BLOCK_DATA = LOOKUP.findStatic(BLOCK_CLASS, "getByCombinedId", MethodType.methodType(BLOCK_DATA_CLASS, int.class));
        }
        catch (NoSuchMethodException | IllegalAccessException e) {
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
        return METHOD_GET_BLOCK_DATA.invoke(materialData.getItemTypeId() + (materialData.getData() << 12));
    }

    @SneakyThrows
    public Object getNmsChunkAt(@NonNull Object nmsWorld, int x, int z) {
        return METHOD_GET_CHUNK_AT.invoke(nmsWorld, x, z);
    }

    @SneakyThrows
    public void invokeSetFastBlock(Object src, int x, int y, int z, Object blockData) {
        METHOD_SET_BLOCK_TYPE.invoke(src, x, y, z, blockData);
    }
}