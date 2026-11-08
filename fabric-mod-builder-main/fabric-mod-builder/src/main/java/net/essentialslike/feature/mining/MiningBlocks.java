package net.essentialslike.feature.mining;

import net.essentialslike.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;

import java.util.Set;

/** 採掘系ブレーカーの対象ブロック定義。 */
public final class MiningBlocks {
    private MiningBlocks() {}

    public static final Set<Block> ORES = Set.of(
            Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE,
            Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
            Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.NETHER_GOLD_ORE,
            Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE,
            Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE,
            Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.NETHER_QUARTZ_ORE, Blocks.ANCIENT_DEBRIS);

    /** 石系（黒曜石は設定で別途判定）。 */
    public static final Set<Block> STONE = Set.of(
            Blocks.ANDESITE, Blocks.BASALT, Blocks.BLACKSTONE, Blocks.COBBLED_DEEPSLATE,
            Blocks.COBBLESTONE, Blocks.CRIMSON_NYLIUM, Blocks.DEEPSLATE, Blocks.DIORITE,
            Blocks.END_STONE, Blocks.GLOWSTONE, Blocks.GRANITE, Blocks.MAGMA_BLOCK,
            Blocks.NETHERRACK, Blocks.RED_SANDSTONE, Blocks.SANDSTONE, Blocks.STONE,
            Blocks.TUFF, Blocks.WARPED_NYLIUM);

    public static final Set<Block> SAND = Set.of(
            Blocks.CLAY, Blocks.GRAVEL, Blocks.MUD, Blocks.GRASS_BLOCK, Blocks.DIRT,
            Blocks.COARSE_DIRT, Blocks.SAND, Blocks.RED_SAND, Blocks.SOUL_SAND, Blocks.SOUL_SOIL);

    public static boolean isOre(BlockState state) {
        return ORES.contains(state.getBlock());
    }

    /** 石系ブレーカーの対象判定（黒曜石は設定 ON かつダイヤモンド以上のツルハシのみ）。 */
    public static boolean isStoneTarget(BlockState state, ModConfig cfg, ItemStack tool) {
        if (state.isOf(Blocks.OBSIDIAN)) {
            return cfg.global.obsidianInStoneBreaker && hasDiamondOrBetter(tool);
        }
        return STONE.contains(state.getBlock());
    }

    /** 砂系ブレーカーの対象判定（砂利・ソウル系は個別設定）。 */
    public static boolean isSandTarget(BlockState state, ModConfig cfg) {
        if (state.isOf(Blocks.GRAVEL)) return cfg.player.sandGravel;
        if (state.isOf(Blocks.SOUL_SAND) || state.isOf(Blocks.SOUL_SOIL)) return cfg.player.sandSoulSandAndSoil;
        return SAND.contains(state.getBlock());
    }

    /** 個別設定で無効化されたブロックか。 */
    public static boolean isDisabledByPlayer(BlockState state, ModConfig cfg) {
        String id = net.minecraft.registry.Registries.BLOCK.getId(state.getBlock()).toString();
        return cfg.player.stoneDisabledBlocks.contains(id);
    }

    private static boolean hasDiamondOrBetter(ItemStack tool) {
        return tool.getItem() instanceof ToolItem toolItem && toolItem.getMaterial().getMiningLevel() >= 3;
    }
}
