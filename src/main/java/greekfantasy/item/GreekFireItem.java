package greekfantasy.item;

import greekfantasy.entity.misc.GreekFireEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class GreekFireItem extends Item {

  public GreekFireItem(final Properties properties) {
    super(properties);
  }
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    player.getCooldownTracker().setCooldown(this, 20);
    // spawn a Greek Fire entity that will cause an explosion
    if(!world.isRemote()) {
      GreekFireEntity dragonTooth = GreekFireEntity.create(world, player);
      // this unmapped method from ProjectileEntity does some math, then calls #shoot
      dragonTooth.setDirectionAndMovement(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
      world.addEntity(dragonTooth);
    }
    
    // shrink the item stack
    if(!player.isCreative()) {
      stack.shrink(1);
    }
    
    return ActionResult.func_233538_a_(stack, world.isRemote());
  }
}
