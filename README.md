
<div align="center">

![Logo](fastblock_logo.png)

---

# BUKKIT FAST BLOCKS
Fast Minecraft Blocks Placing Utilities

</div>

---

### Feedback

+ **[Discord Server](https://discord.gg/GmT9pUy8af)**
+ **[VKontakte Page](https://vk.com/itzstonlex)**

---

## HOW TO USE?

Fast Place Sessions Examples:
```Java
public void fillRegionByType(CuboidRegion cuboidRegion, Material material) {
    FastBlockPlaceSession placeSession = new FastBlockPlaceSession(cuboidRegion.getWorld());

    for (Block block : cuboidRegion) {
        placeSession.setBlockType(block, material);
    }

    placeSession.flush().thenAccept(delayMillis -> 
        System.out.println("FastBlockPlaceSession was executed full per " + delayMillis + "ms"));
}

```
```Java
public void setFastLineX(Location begin, Material material, int length) {
    FastBlockPlaceSession placeSession = new FastBlockPlaceSession(begin.getWorld());

    for (int x = 0; x <= length; x++) {
        placeSession.setBlockType(begin.clone().add(x, 0, 0), material);
    }

    placeSession.flush().thenAccept(delayMillis ->
        System.out.println("FastBlockPlaceSession was executed full per " + delayMillis + "ms"));
}
```
