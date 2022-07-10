package greekfantasy;

import greekfantasy.enchantment.SmashingEnchantment;
import greekfantasy.integration.RGCompat;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public final class GFEvents {

    public static final class ForgeHandler {

        @SubscribeEvent
        public static void onAttackEntity(final AttackEntityEvent event) {
            // only handle event on server
            if(event.getEntityLiving().level.isClientSide()) {
                return;
            }
            // check if held item has Smashing enchantment
            ItemStack itemStack = event.getPlayer().getMainHandItem();
            int smashing = EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.SMASHING.get(), itemStack);
            if(smashing > 0) {
                // apply Smashing enchantment
                //SmashingEnchantment.apply(event.getPlayer(), event.getTarget(), smashing, event.getPlayer().getAttackStrengthScale(0.0F));
            }

        }

        @SubscribeEvent
        public static void onLivingTick(final LivingEvent.LivingUpdateEvent event) {
            // only handle event on server
            if(event.getEntityLiving().level.isClientSide()) {
                return;
            }
            // handle Prisoner of Hades mob effect
            final MobEffect prisonerOfHades = GFRegistry.MobEffectReg.PRISONER_OF_HADES.get();
            if(event.getEntityLiving().hasEffect(prisonerOfHades)) {
                // remove when not in nether
                if (event.getEntityLiving().level.dimension() != Level.NETHER
                        || (GreekFantasy.isRGLoaded() && event.getEntityLiving() instanceof Player player
                            && RGCompat.getInstance().canRemovePrisonerEffect(player))) {
                    GreekFantasy.LOGGER.debug("Removing effect from " + event.getEntityLiving());
                    event.getEntityLiving().removeEffect(prisonerOfHades);
                } else {
                    // set portal cooldown
                    event.getEntityLiving().setPortalCooldown();
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
            if(event.phase != TickEvent.Phase.START || !event.player.isAlive()) {
                return;
            }
            // update pose when player is under curse of circe
            if(GreekFantasy.CONFIG.isCurseOfCirceEnabled()) {
                final boolean curseOfCirce = event.player.hasEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get());
                final Pose forcedPose = event.player.getForcedPose();
                // update the forced pose
                if (curseOfCirce && forcedPose != Pose.FALL_FLYING) {
                    // apply the forced pose
                    event.player.setForcedPose(Pose.FALL_FLYING);
                    event.player.setPose(Pose.FALL_FLYING);
                } else if (!curseOfCirce && Pose.FALL_FLYING == forcedPose) {
                    // clear the forced pose
                    event.player.setForcedPose(null);
                }
            }
        }

        /**
         * Used to prevent players from using items while stunned.
         * Note: PlayerInteractEvent has several children but
         * we receive and cancel all of the ones that can be cancelled
         *
         * @param event a PlayerInteractEvent or any of its children
         **/
        @SubscribeEvent
        public static void onPlayerInteract(final PlayerInteractEvent event) {
            if (event.isCancelable() && event.getPlayer().isAlive()
                    && (event.getPlayer().hasEffect(GFRegistry.MobEffectReg.STUNNED.get()))
                        || event.getPlayer().hasEffect(GFRegistry.MobEffectReg.PETRIFIED.get())) {
                // cancel the event
                event.setCanceled(true);
            }
        }

        /**
         * Used to prevent players from using items while stunned.
         *
         * @param event the living attack event
         **/
        @SubscribeEvent
        public static void onPlayerAttack(final AttackEntityEvent event) {
            if (event.isCancelable() && event.getPlayer().isAlive()
                    && (event.getPlayer().hasEffect(GFRegistry.MobEffectReg.STUNNED.get()))
                    || event.getPlayer().hasEffect(GFRegistry.MobEffectReg.PETRIFIED.get())) {
                // cancel the event
                event.setCanceled(true);
            }
        }

        /**
         * Used to prevent certain mobs from attacking players when either the player
         * or the mob are under Curse of Circe
         *
         * @param event the living target event
         **/
        @SubscribeEvent
        public static void onLivingTarget(final LivingSetAttackTargetEvent event) {
            if (!event.getEntityLiving().level.isClientSide()
                    && event.getEntityLiving() instanceof Mob mob
                    && GreekFantasy.CONFIG.isCurseOfCirceEnabled()
                    && (mob.hasEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get())
                        || event.getTarget().hasEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get()))) {
                // remove attack target
                mob.setTarget(null);
            }
        }
    }

    public static final class ModHandler {

    }
}
