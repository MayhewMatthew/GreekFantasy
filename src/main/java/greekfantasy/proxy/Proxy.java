package greekfantasy.proxy;

import java.util.ArrayList;
import java.util.Collection;

import greekfantasy.GreekFantasy;
import greekfantasy.events.CommonForgeEventHandler;
import greekfantasy.favor.Deity;
import greekfantasy.favor.FavorRangeTarget;
import greekfantasy.favor.IDeity;
import greekfantasy.network.SDeityPacket;
import greekfantasy.network.SFavorRangeTargetPacket;
import greekfantasy.network.SPanfluteSongPacket;
import greekfantasy.util.GenericJsonReloadListener;
import greekfantasy.util.PanfluteSong;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.PacketDistributor;

public class Proxy {
  
  public final GenericJsonReloadListener<PanfluteSong> PANFLUTE_SONGS = new GenericJsonReloadListener<>("songs", PanfluteSong.class, PanfluteSong.CODEC, 
      l -> l.getEntries().forEach(e -> GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SPanfluteSongPacket(e.getKey(), e.getValue().get()))));
  public final GenericJsonReloadListener<Deity> DEITY = new GenericJsonReloadListener<>("deity", Deity.class, Deity.CODEC, 
      l -> l.getEntries().forEach(e -> GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SDeityPacket(e.getKey(), e.getValue().get()))));
  public final GenericJsonReloadListener<FavorRangeTarget> FAVOR_RANGE_TARGET = new GenericJsonReloadListener<>("deity_favor_target_map", FavorRangeTarget.class, FavorRangeTarget.CODEC, 
      l -> GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SFavorRangeTargetPacket(l.get(FavorRangeTarget.NAME).get())));
  
  public void registerReloadListeners() { }

  public void registerEntityRenders() { }
  
  public void registerTileEntityRenders() { }
  
  public void registerContainerRenders() { }
  
  public void registerRenderLayers() { }
    
  public void registerEventHandlers() {
    GreekFantasy.LOGGER.debug("registerEventHandlers");
    MinecraftForge.EVENT_BUS.register(CommonForgeEventHandler.class);
  }
  
  /**
   * @param enabledOnly whether to only return enabled Deity
   * @return the collection of IDeity objects (may be empty)
   */
  public Collection<IDeity> getDeityCollection(final boolean enabledOnly) {
    final Collection<IDeity> collection = new ArrayList<>();
    GreekFantasy.PROXY.DEITY.getValues().forEach(d -> {
      if(d.isPresent() && (d.get().isEnabled() || !enabledOnly)) {
        collection.add(d.get());
      }
    });
    return collection;
  }

  /** @return the favor range target map **/
  public FavorRangeTarget getFavorRangeTarget() {
    return FAVOR_RANGE_TARGET.get(FavorRangeTarget.NAME).orElse(FavorRangeTarget.EMPTY);
  }
}
