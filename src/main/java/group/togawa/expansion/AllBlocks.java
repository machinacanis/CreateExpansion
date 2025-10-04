package group.togawa.expansion;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import group.togawa.expansion.foundation.data.recipe.CommonMetal;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.Tags;
import org.slf4j.Logger;

import java.util.Map;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static group.togawa.expansion.foundation.data.TagGen.tagBlockAndItem;

public class AllBlocks {
    private static final CreateRegistrate REGISTRATE = StressExpansion.registrate(); // 获取全局注册实例
    public static final BlockEntry<Block> NICKEL_ORE = REGISTRATE.block("nickel_ore", Block::new)
            .initialProperties(() -> Blocks.IRON_ORE)
            .properties(p -> p.mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .tag(Tags.Blocks.ORES)
            .transform(tagBlockAndItem(Map.of(
                    CommonMetal.NICKEL.ores.blocks(), CommonMetal.NICKEL.ores.items(),
                    Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE
            )))
            .tag(Tags.Items.ORES)
            .build()
            .register();
    public static final BlockEntry<Block> DEEPSLATE_NICKEL_ORE = REGISTRATE.block("deepslate_nickel_ore", Block::new)
            .initialProperties(() -> Blocks.DEEPSLATE_IRON_ORE)
            .properties(p -> p.mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE))
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_STONE_TOOL)
            .tag(Tags.Blocks.ORES)
            .transform(tagBlockAndItem(Map.of(
                    CommonMetal.NICKEL.ores.blocks(), CommonMetal.NICKEL.ores.items(),
                    Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE
            )))
            .tag(Tags.Items.ORES)
            .build()
            .register();

    public static void register(Logger logger) {
        logger.info("Registering blocks...");
    }
}
