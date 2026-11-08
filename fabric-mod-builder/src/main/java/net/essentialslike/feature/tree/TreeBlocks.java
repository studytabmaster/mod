package net.essentialslike.feature.tree;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;

import java.util.Map;
import java.util.Set;

/** ツリーブレーカーの対象（原木・巨大キノコ）と苗木の対応表。ツツジは対象外。 */
public final class TreeBlocks {
    private TreeBlocks() {}

    private static final Set<Block> MUSHROOM_BLOCKS = Set.of(
            Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.MUSHROOM_STEM,
            Blocks.CRIMSON_STEM, Blocks.WARPED_STEM);

    /** 原木 → 苗木（植え付け用）。 */
    private static final Map<Block, Item> SAPLINGS = Map.of(
            Blocks.OAK_LOG, Items.OAK_SAPLING,
            Blocks.SPRUCE_LOG, Items.SPRUCE_SAPLING,
            Blocks.BIRCH_LOG, Items.BIRCH_SAPLING,
            Blocks.JUNGLE_LOG, Items.JUNGLE_SAPLING,
            Blocks.ACACIA_LOG, Items.ACACIA_SAPLING,
            Blocks.DARK_OAK_LOG, Items.DARK_OAK_SAPLING,
            Blocks.MANGROVE_LOG, Items.MANGROVE_PROPAGULE,
            Blocks.CHERRY_LOG, Items.CHERRY_SAPLING);

    public static boolean isTreeBlock(BlockState state) {
        if (state.isOf(Blocks.AZALEA) || state.isOf(Blocks.FLOWERING_AZALEA)) return false;
        return state.isIn(BlockTags.LOGS) || MUSHROOM_BLOCKS.contains(state.getBlock());
    }

    public static Item saplingFor(Block log) {
        return SAPLINGS.get(log);
    }
}
