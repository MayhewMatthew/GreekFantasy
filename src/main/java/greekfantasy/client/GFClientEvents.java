package greekfantasy.client;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.client.blockentity.VaseBlockEntityRenderer;
import greekfantasy.client.entity.BabySpiderRenderer;
import greekfantasy.client.entity.DrakainaRenderer;
import greekfantasy.client.entity.SpartiRenderer;
import greekfantasy.client.entity.SpearRenderer;
import greekfantasy.client.entity.SpellRenderer;
import greekfantasy.client.entity.model.BabySpiderModel;
import greekfantasy.client.entity.model.DrakainaModel;
import greekfantasy.client.entity.model.SpellModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class GFClientEvents {

    public static final class ForgeHandler {

        /**
         * Used to hide the third-person view of an entity that is
         * wearing the Helm of Darkness. Next, attempts to render
         * any entity that is under the Curse of Circe as a pig.
         * @param event the Pre RenderLivingEvent
         */
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onRenderLiving(final RenderLivingEvent.Pre<?, ?> event) {
            // cancel rendering when helm of darkness is equipped
            if(GreekFantasy.CONFIG.helmHidesArmor() && event.getEntity().getItemBySlot(EquipmentSlot.HEAD).is(GFRegistry.ItemReg.HELM_OF_DARKNESS.get())) {
                event.setCanceled(true);
                return;
            }
            // render pig when curse of circe is applied
            if(GreekFantasy.CONFIG.isCurseOfCirceEnabled() && event.getEntity().hasEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get())) {
                // TODO
            }
        }

        /**
         * Used to hide the first-person view of held items
         * while a player is using the Helm of Darkness or under
         * the Curse of Circe.
         *
         * @param event the RenderHandEvent
         **/
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onRenderHand(final RenderHandEvent event) {
            final Minecraft mc = Minecraft.getInstance();
            if(GreekFantasy.CONFIG.helmHidesArmor() && mc.player.getItemBySlot(EquipmentSlot.HEAD).is(GFRegistry.ItemReg.HELM_OF_DARKNESS.get())) {
                event.setCanceled(true);
                return;
            }
            // render pig when curse of circe is applied
            if(GreekFantasy.CONFIG.isCurseOfCirceEnabled()
                    && mc.player.hasEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get())) {
                event.setCanceled(true);
                return;
            }
        }
    }

    public static final class ModHandler {

        private static final String MODID = GreekFantasy.MODID;

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(ModHandler::registerRenderLayers);
            event.enqueueWork(ModHandler::registerContainerRenders);
            event.enqueueWork(ModHandler::registerModelProperties);
        }

        @SubscribeEvent
        public static void registerEntityLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            // register layer definitions
            // creature
            event.registerLayerDefinition(BabySpiderRenderer.BABY_SPIDER_MODEL_RESOURCE, BabySpiderModel::createBodyLayer);
            event.registerLayerDefinition(DrakainaRenderer.DRAKAINA_MODEL_RESOURCE, DrakainaModel::createBodyLayer);
            // other
            event.registerLayerDefinition(SpearRenderer.SPEAR_MODEL_RESOURCE, TridentModel::createLayer);
            event.registerLayerDefinition(SpellRenderer.SPELL_MODEL_RESOURCE, SpellModel::createLayer);
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            // register entities
            // creature
            event.registerEntityRenderer(GFRegistry.EntityReg.BABY_SPIDER.get(), BabySpiderRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.DRAKAINA.get(), DrakainaRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.SPARTI.get(), SpartiRenderer::new);
            // other
            event.registerEntityRenderer(GFRegistry.EntityReg.CURSE.get(), SpellRenderer.CurseRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.CURSE_OF_CIRCE.get(), SpellRenderer.CurseOfCirceRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.DISCUS.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.DRAGON_TOOTH.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.HEALING_SPELL.get(), SpellRenderer.HealingSpellRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.POISON_SPIT.get(), SpellRenderer.PoisonSpitRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.SPEAR.get(), SpearRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.WEB_BALL.get(), ThrownItemRenderer::new);
            // register block entities
            event.registerBlockEntityRenderer(GFRegistry.BlockEntityReg.VASE.get(), VaseBlockEntityRenderer::new);
        }

        /**
         * Used to register block color handlers.
         * Currently used to color leaves.
         *
         * @param event the ColorHandlerEvent (Block)
         **/
        @SubscribeEvent
        public static void registerBlockColors(final ColorHandlerEvent.Block event) {
            event.getBlockColors().register(
                    (BlockState stateIn, BlockAndTintGetter level, BlockPos pos, int color) -> 0xD8E3D0,
                    RegistryObject.create(new ResourceLocation(GreekFantasy.MODID, "olive_leaves"), ForgeRegistries.BLOCKS).get());
            event.getBlockColors().register(
                    (BlockState stateIn, BlockAndTintGetter level, BlockPos pos, int color) -> 0x80f66b,
                    RegistryObject.create(new ResourceLocation(GreekFantasy.MODID, "golden_leaves"), ForgeRegistries.BLOCKS).get());
        }

        /**
         * Used to register item color handlers.
         * Currently used to color leaves.
         *
         * @param event the ColorHandlerEvent (Item)
         **/
        @SubscribeEvent
        public static void registerItemColors(final ColorHandlerEvent.Item event) {
            event.getItemColors().register((ItemStack item, int i) -> 0xD8E3D0,
                    RegistryObject.create(new ResourceLocation(GreekFantasy.MODID, "olive_leaves"), ForgeRegistries.ITEMS).get());
            event.getItemColors().register((ItemStack item, int i) -> 0x80f66b,
                    RegistryObject.create(new ResourceLocation(GreekFantasy.MODID, "golden_leaves"), ForgeRegistries.ITEMS).get());
        }

        private static void registerRenderLayers() {
            // cutout mipped
            registerRenderLayer("olive_leaves", RenderType.cutoutMipped());
            registerRenderLayer("pomegranate_leaves", RenderType.cutoutMipped());
            registerRenderLayer("golden_leaves", RenderType.cutoutMipped());
            // cutout
            registerCutout("olive_sapling");
            registerCutout("olive_door");
            registerCutout("olive_trapdoor");
            registerCutout("pomegranate_sapling");
            registerCutout("pomegranate_door");
            registerCutout("pomegranate_trapdoor");
            registerCutout("golden_sapling");
            registerCutout("olive_oil");
            registerCutout("golden_string");
            registerCutout("wild_rose");
        }

        private static void registerCutout(final String blockName) {
            registerRenderLayer(blockName, RenderType.cutout());
        }

        private static void registerRenderLayer(final String blockName, RenderType renderType) {
            ItemBlockRenderTypes.setRenderLayer(RegistryObject.create(new ResourceLocation(MODID, blockName), ForgeRegistries.BLOCKS).get(), renderType);
        }

        private static void registerContainerRenders() {

        }

        private static void registerModelProperties() {
            // register bows
            registerBowProperties(GFRegistry.ItemReg.AVERNAL_BOW.get());
            registerBowProperties(GFRegistry.ItemReg.APOLLO_BOW.get());
            registerBowProperties(GFRegistry.ItemReg.ARTEMIS_BOW.get());
            // register spears
            registerSpearProperties(GFRegistry.ItemReg.BIDENT.get());
            registerSpearProperties(GFRegistry.ItemReg.WOODEN_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.FLINT_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.STONE_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.GOLDEN_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.IRON_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.DIAMOND_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.NETHERITE_SPEAR.get());
        }

        private static void registerBowProperties(final Item bow) {
            ItemProperties.register(bow, new ResourceLocation("pull"),
                    (item, world, entity, tintIndex) -> {
                        if (entity == null) return 0.0F;
                        if (entity.getUseItem() != item) return 0.0F;
                        return (item.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
                    });
            ItemProperties.register(bow, new ResourceLocation("pulling"),
                    (item, world, entity, tintIndex) -> (entity != null && entity.isUsingItem() && entity.getUseItem() == item) ? 1.0F : 0.0F);
        }

        private static void registerSpearProperties(final Item spear) {
            ItemProperties.register(spear, new ResourceLocation("throwing"), (item, world, entity, tintIndex) ->
                    (entity != null && entity.isUsingItem() && entity.getUseItem() == item) ? 1.0F : 0.0F);
        }

    }
}
