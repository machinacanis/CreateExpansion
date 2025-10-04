package group.togawa.stress_expansion;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import group.togawa.stress_expansion.foundation.data.recipe.CommonMetal;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;
import org.slf4j.Logger;

import static com.simibubi.create.AllTags.AllItemTags.PLATES;

public class AllItems {
    private static final CreateRegistrate REGISTRATE = StressExpansion.registrate(); // 获取全局注册实例

    public static final ItemEntry<Item> // 粗矿
            RAW_NICKEL = taggedIngredient("raw_nickel", CommonMetal.NICKEL.rawOres, Tags.Items.RAW_MATERIALS);

    public static final ItemEntry<Item> // 锭
            NICKEL_INGOT = taggedIngredient("nickel_ingot", CommonMetal.NICKEL.ingots, Tags.Items.INGOTS);

    public static final ItemEntry<Item> // 粒
            NICKEL_NUGGET = taggedIngredient("nickel_nugget", CommonMetal.NICKEL.nuggets, Tags.Items.NUGGETS);

    public static final ItemEntry<Item> // 板
            NICKEL_SHEET = taggedIngredient("nickel_sheet", CommonMetal.NICKEL.plates, PLATES.tag),
            ANDESITE_ALLOY_SHEET = taggedIngredient("andesite_alloy_sheet", CommonMetal.ANDESITE_ALLOY.plates, PLATES.tag);

    public static void register(Logger logger) {
        logger.info("Registering items...");
    }

    // 抄一下机械动力的一些很方便的写法
    private static ItemEntry<Item> ingredient(String name) {
        return REGISTRATE.item(name, Item::new)
                .register();
    }

    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
        return REGISTRATE.item(name, SequencedAssemblyItem::new)
                .register();
    }

    @SafeVarargs
    private static ItemEntry<Item> taggedIngredient(String name, TagKey<Item>... tags) {
        if (REGISTRATE != null) { // 防止IDE报错
            return REGISTRATE.item(name, Item::new)
                    .tag(tags)
                    .register();
        }
        return null;
    }
}
