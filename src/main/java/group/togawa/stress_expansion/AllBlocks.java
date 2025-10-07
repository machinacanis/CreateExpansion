package group.togawa.stress_expansion;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import group.togawa.stress_expansion.content.StressExpansionTags;
import group.togawa.stress_expansion.content.anvil.BrassAnvilBlock;
import group.togawa.stress_expansion.foundation.data.recipe.CommonMetal;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.neoforged.neoforge.common.Tags;
import org.slf4j.Logger;

import java.util.Map;

import static group.togawa.stress_expansion.foundation.data.TagGen.*;

public class AllBlocks {
    private static final CreateRegistrate REGISTRATE = StressExpansion.registrate(); // 获取全局注册实例

    // 矿石
    public static final BlockEntry<Block> NICKEL_ORE = REGISTRATE.block("nickel_ore", Block::new) // 镍矿
            .initialProperties(() -> Blocks.IRON_ORE)
            .properties(p -> p.mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
            .transform(pickaxeOnly())
            .loot((lt, b) -> {
                HolderLookup.RegistryLookup<Enchantment> enchantmentRegistryLookup = lt.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);

                lt.add(b,
                        lt.createSilkTouchDispatchTable(b,
                                lt.applyExplosionDecay(b, LootItem.lootTableItem(AllItems.RAW_NICKEL.get())
                                        .apply(ApplyBonusCount.addOreBonusCount(enchantmentRegistryLookup.getOrThrow(Enchantments.FORTUNE))))));
            })
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .tag(Tags.Blocks.ORES)
            .transform(tagBlockAndItem(Map.of(
                    CommonMetal.NICKEL.ores.blocks(), CommonMetal.NICKEL.ores.items(),
                    Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE
            )))
            .tag(Tags.Items.ORES)
            .build()
            .register();

    // 深板岩矿石
    public static final BlockEntry<Block> DEEPSLATE_NICKEL_ORE = REGISTRATE.block("deepslate_nickel_ore", Block::new) // 深层镍矿石
            .initialProperties(() -> Blocks.DEEPSLATE_IRON_ORE)
            .properties(p -> p.mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE))
            .transform(pickaxeOnly())
            .loot((lt, b) -> {
                HolderLookup.RegistryLookup<Enchantment> enchantmentRegistryLookup = lt.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);

                lt.add(b,
                        lt.createSilkTouchDispatchTable(b,
                                lt.applyExplosionDecay(b, LootItem.lootTableItem(AllItems.RAW_NICKEL.get())
                                        .apply(ApplyBonusCount.addOreBonusCount(enchantmentRegistryLookup.getOrThrow(Enchantments.FORTUNE))))));
            })
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .tag(Tags.Blocks.ORES)
            .transform(tagBlockAndItem(Map.of(
                    CommonMetal.NICKEL.ores.blocks(), CommonMetal.NICKEL.ores.items(),
                    Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE
            )))
            .tag(Tags.Items.ORES)
            .build()
            .register();

    // 粗矿块
    public static final BlockEntry<Block> RAW_NICKEL_BLOCK = REGISTRATE.block("raw_nickel_block", Block::new) // 粗镍块
            .initialProperties(() -> Blocks.RAW_IRON_BLOCK)
            .properties(p -> p.mapColor(MapColor.RAW_IRON)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
            .transform(pickaxeOnly())
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(tagBlockAndItem(CommonMetal.NICKEL.rawStorageBlocks))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .register();

    // 矿物块
    public static final BlockEntry<Block> NICKEL_BLOCK = REGISTRATE.block("nickel_block", Block::new) // 镍块
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL))
            .transform(pickaxeOnly())
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(tagBlockAndItem(CommonMetal.NICKEL.storageBlocks))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .register();

    public static final BlockEntry<Block> ALUMINUM_BLOCK = REGISTRATE.block("aluminum_block", Block::new) // 镍块
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL))
            .transform(pickaxeOnly())
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(tagBlockAndItem(CommonMetal.ALUMINUM.storageBlocks))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .register();

    // 土壤
    public static final BlockEntry<Block> LATERITE = REGISTRATE.block("laterite", Block::new) // 红土
            .initialProperties(() -> Blocks.DIRT)
            .properties(p -> p.mapColor(MapColor.DIRT)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.ROOTED_DIRT))
            .tag(BlockTags.DIRT)
            .item()
            .tag(ItemTags.DIRT)
            .build()
            .register();

    public static final BlockEntry<Block> NICKEL_RICH_LATERITE = REGISTRATE.block("nickel_rich_laterite", Block::new) // 富镍红土
            .initialProperties(() -> Blocks.DIRT)
            .properties(p -> p.mapColor(MapColor.DIRT)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.ROOTED_DIRT))
            .tag(BlockTags.DIRT)
            .transform(shovelOnly())
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .tag(Tags.Blocks.ORES)
            .item()
            .tag(ItemTags.DIRT)
            .tag(Tags.Items.ORES)
            .build()
            .register();

    // 黄铜砧
    public static final BlockEntry<BrassAnvilBlock> BRASS_ANVIL = REGISTRATE.block("brass_anvil", BrassAnvilBlock::new) // 黄铜砧
            .initialProperties(() -> Blocks.ANVIL)
            .properties(p -> p.mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.ANVIL)
                    .strength(5.0F, 2000.0F))
            .transform(pickaxeOnly())
            .tag(StressExpansionTags.BlockTags.BRASS_ANVIL.tag)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .item()
            .build()
            .register();

    public static final BlockEntry<BrassAnvilBlock> CHIPPED_BRASS_ANVIL = REGISTRATE.block("chipped_brass_anvil", BrassAnvilBlock::new) // 开裂的黄铜砧
            .initialProperties(() -> Blocks.ANVIL)
            .properties(p -> p.mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.ANVIL)
                    .strength(5.0F, 2000.0F))
            .transform(pickaxeOnly())
            .tag(StressExpansionTags.BlockTags.BRASS_ANVIL.tag)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .item()
            .build()
            .register();

    public static final BlockEntry<BrassAnvilBlock> DAMAGED_BRASS_ANVIL = REGISTRATE.block("damaged_brass_anvil", BrassAnvilBlock::new) // 损坏的黄铜砧
            .initialProperties(() -> Blocks.ANVIL)
            .properties(p -> p.mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.ANVIL)
                    .strength(5.0F, 2000.0F))
            .transform(pickaxeOnly())
            .tag(StressExpansionTags.BlockTags.BRASS_ANVIL.tag)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .item()
            .build()
            .register();

    public static void register(Logger logger) {
        logger.info("Registering blocks...");
    }
}
