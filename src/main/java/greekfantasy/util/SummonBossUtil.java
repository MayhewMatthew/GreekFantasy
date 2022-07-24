package greekfantasy.util;

import greekfantasy.GreekFantasy;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.UUID;

public final class SummonBossUtil {

    public static final TagKey<Block> BRONZE_BLOCK = ForgeRegistries.BLOCKS.tags().createTagKey(new ResourceLocation("forge", "storage_blocks/bronze"));
    public static final TagKey<Block> COPPER_BLOCK = ForgeRegistries.BLOCKS.tags().createTagKey(new ResourceLocation("forge", "storage_blocks/copper"));
    public static final TagKey<Block> CERBERUS_FRAME = ForgeRegistries.BLOCKS.tags().createTagKey(new ResourceLocation(GreekFantasy.MODID, "cerberus_frame"));

    /**
     * BlockPattern for Talos boss
     **/
    private static final BlockPattern talosPattern = BlockPatternBuilder.start()
            .aisle("~^~", "###", "###")
            .where('^', BlockInWorld.hasState(state -> state.is(BRONZE_BLOCK)))
            .where('#', BlockInWorld.hasState(state -> state.is(COPPER_BLOCK)))
            .where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();

    /**
     * BlockPattern for Bronze Bull boss
     **/
    private static final BlockPattern bronzeBullPattern = BlockPatternBuilder.start()
            .aisle("^##^", "~##~")
            .aisle("~##~", "~##~")
            .where('^', BlockInWorld.hasState(state -> state.is(BRONZE_BLOCK)))
            .where('#', BlockInWorld.hasState(state -> state.is(COPPER_BLOCK)))
            .where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();

    /**
     * BlockPattern for Cerberus boss
     **/
    private static final BlockPattern cerberusPattern = BlockPatternBuilder.start()
            .aisle("~~~~", "~~~~", "~##~")
            .aisle("~OO~", "~OO~", "#^^#")
            .aisle("~OO~", "~OO~", "#^^#")
            .aisle("~~~~", "~~~~", "~##~")
            .where('#', BlockInWorld.hasState(state -> state.is(CERBERUS_FRAME)))
            .where('^', BlockInWorld.hasState(state -> state.is(Blocks.LAVA)))
            .where('O', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR)))
            .where('~', BlockInWorld.hasState(state -> true)).build();


    /**
     * Called when a block in the {@link #BRONZE_BLOCK} tag is placed. Checks if a boss stucture was formed,
     * and if so, removes the blocks and summons the boss.
     *
     * @param level  the level
     * @param pos    the position of the block
     * @param state  the block state
     * @param placer the entity who placed the block, if any
     * @return true if the boss was summoned and the structure was replaced
     */
    public static boolean onPlaceBronzeBlock(Level level, BlockPos pos, BlockState state, @Nullable Entity placer) {
        if(!state.is(BRONZE_BLOCK)) {
            return false;
        }
        // check if a talos was built
        BlockPattern pattern = talosPattern;
        BlockPattern.BlockPatternMatch helper = pattern.find(level, pos);
        if (helper != null) {
            // remove the blocks that were used
            for (int i = 0; i < pattern.getWidth(); ++i) {
                for (int j = 0; j < pattern.getHeight(); ++j) {
                    for (int k = 0; k < pattern.getDepth(); ++k) {
                        BlockInWorld cachedblockinfo1 = helper.getBlock(i, j, k);
                        level.destroyBlock(cachedblockinfo1.getPos(), false);
                    }
                }
            }
            // spawn the talos
            // TODO TalosEntity.spawnTalos(worldIn, helper.getBlock(1, 2, 0).getPos(), 0);
            return true;
        }
        // check if a bronze bull was built
        pattern = bronzeBullPattern;
        helper = pattern.find(level, pos);
        if (helper != null) {
            // remove the blocks that were used
            for (int i = 0; i < pattern.getWidth(); ++i) {
                for (int j = 0; j < pattern.getHeight(); ++j) {
                    for (int k = 0; k < pattern.getDepth(); ++k) {
                        BlockInWorld cachedblockinfo1 = helper.getBlock(i, j, k);
                        level.destroyBlock(cachedblockinfo1.getPos(), false);
                    }
                }
            }
            // spawn the bronze bull
            // TODO BronzeBullEntity.spawnBronzeBull(worldIn, helper.getBlock(1, 1, 0).getPos(), 0);
            return true;
        }
        return false;
    }


    /**
     * Called when an Orthus Head item entity is removed while on fire. Checks if a boss structure was formed,
     * and if so, removes the blocks and summons the boss.
     * @param level the level
     * @param pos the block position
     * @param thrower the entity that burned the orthus head, may be null
     * @return true if the boss was summoned and the structure was replaced
     */
    public static boolean onOrthusHeadBurned(Level level, BlockPos pos, @Nullable UUID thrower) {
        // check if a cerberus frame was built
        BlockPattern pattern = cerberusPattern;
        BlockPattern.BlockPatternMatch helper = pattern.find(level, pos);
        if (helper != null) {
            // replace the lava blocks that were used
            for (int i = 1; i < pattern.getWidth() - 1; ++i) {
                for (int k = 1; k < pattern.getDepth() - 1; ++k) {
                    BlockInWorld cachedblockinfo1 = helper.getBlock(i, 2, k);
                    level.setBlock(cachedblockinfo1.getPos(), Blocks.MAGMA_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
            // spawn the cerberus
            // TODO CerberusEntity.spawnCerberus(level, pos.add(0, 1.0D, 0));
            return true;
        }
        return false;
    }
}