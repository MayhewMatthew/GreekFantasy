package greekfantasy.favor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.util.StatuePoses;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

public final class DeityManager {
  
  private static final String MODID = GreekFantasy.MODID;
  
  protected static final Map<ResourceLocation, IDeity> DEITY = new HashMap<>();
  
  public static final ResourceLocation ZEUS = new ResourceLocation(MODID, "zeus");
  
//  // init deities
//  public static final IDeity ZEUS = DeityManager.addDeity(new Deity.Builder(MODID, "zeus").setPose(StatuePoses.WALKING)
//      .addItem(Items.GOLD_INGOT, 20).addItem(Items.BONE, -5)
//      .addEntity(EntityType.COW, -10).addEntity(GFRegistry.ARA_ENTITY, 10)
//      .addEntity(GFRegistry.DRAKAINA_ENTITY, 10).addEntity(GFRegistry.ELPIS_ENTITY, -50)
//      .addEntity(GFRegistry.HARPY_ENTITY, -10).addEntity(GFRegistry.DRYAD_ENTITY, -20)
//      .addEntity(GFRegistry.UNICORN_ENTITY, -20).build());
  
  private DeityManager() { }
  
  /**
   * registers a deity
   * @param deity the deity instance
   */
  public static IDeity addDeity(final IDeity deity) {
    DEITY.put(deity.getName(), deity);
    return deity;
  }

  /**
   * @param name the name of the deity
   * @return the deity if present, or null if none is found
   */
  @Nullable
  public static IDeity getDeity(final ResourceLocation name) {
    if(!DEITY.containsKey(name)) {
      GreekFantasy.LOGGER.error("Failed to retrieve deity '" + name.toString() + "' from DeityManager map!");
      return null;
    }
    return DEITY.get(name);
  }
  
  
  /**
   * @return the the values of all deity
   */
  public static Collection<IDeity> getDeityCollection() {
    return DEITY.values();
  }
  
  /**
   * @return the entry set of all deity
   */
  public static Set<Map.Entry<ResourceLocation, IDeity>> getDeityEntries() {
    return DEITY.entrySet();
  }
}