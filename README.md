**Example Code:**

```java
public void spawn(Player player){
    HologramAPI.HologramManager hologramManager=HologramAPI.getHologramManager();
    hologramManager.setId("sa");
    hologramManager.setLines(Arrays.asList("xx","yy","zz","sad","w","das","w"));
    hologramManager.setLocation(player.getLocation().add(0,1,0));
    Hologram hologram=hologramManager.create();

    hologram.send(player);
}
```