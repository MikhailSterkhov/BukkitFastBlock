
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
    FastBlockPlaceSession placeSession = FastPlacePlugin.createSession(cuboidRegion.getWorld());

    for (Block block : cuboidRegion) {
        placeSession.setBlockType(block, material);
    }

    long totalSessionDelayMillis = placeSession.flush();
    System.out.println("FastBlockPlaceSession was executed full per " + totalSessionDelayMillis + "ms");
}

```
```Java
public void setFastLineX(Location begin, Material material, int length) {
    FastBlockPlaceSession placeSession = FastPlacePlugin.createSession(begin);

    for (int x = 0; x <= length; x++) {
        placeSession.setBlockType(begin.clone().add(x, 0, 0), material);
    }

    long totalSessionDelayMillis = placeSession.flush();
    System.out.println("FastBlockPlaceSession was executed full per " + totalSessionDelayMillis + "ms");
}
```

---

## SPEED TESTS RESULTS

* ![img.png](tests_screens/img3k.png)

* ![img.png](tests_screens/img35k.png)

* ![img.png](tests_screens/img70k.png)

* ![img.png](tests_screens/img175k.png)

---

## SUPPORT ME

<a href="https://www.buymeacoffee.com/itzstonlex" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a>
