package group.togawa.stress_expansion.foundation.data.recipe;

import net.createmod.catnip.lang.Lang;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.*;

import static group.togawa.stress_expansion.foundation.data.recipe.Mods.*;

/**
 * 常用金属枚举类
 * 实现了 <a href="https://github.com/Creators-of-Create/Create/blob/mc1.21.1/dev/src/main/java/com/simibubi/create/foundation/data/recipe/CommonMetal.java">Github</a>
 * 类似的CommonMetal类
 */
public enum CommonMetal {
    IRON(VANILLA),
    GOLD(VANILLA),
    COPPER(VANILLA),

    ZINC(CREATE),
    BRASS(false, CREATE),
    ANDESITE_ALLOY(false, CREATE),
    NICKEL(STRESS_EXPANSION),
    ALUMINUM(STRESS_EXPANSION);


    private static final Map<Mods, Set<CommonMetal>> metalsOfMods = Util.make(() -> {
        Map<Mods, Set<CommonMetal>> map = new EnumMap<>(Mods.class);
        for (Mods mod : Mods.values()) {
            Set<CommonMetal> set = EnumSet.noneOf(CommonMetal.class);
            for (CommonMetal metal : values()) {
                if (metal.mods.contains(mod)) {
                    set.add(metal);
                }
            }
            map.put(mod, set);
        }
        return map;
    });

    /**
     * The name of this metal, for use in IDs. Note that a metal's name may be different depending on mod context.
     *
     * @see #getName(Mods)
     */
    public final String name;
    /**
     * The immutable set of mods which provide an ingot and nugget form of this metal.
     */
    public final Set<Mods> mods;

    /**
     * True is this metal generates naturally. If false, the following tags are nonsense:
     * <ul>
     *     <li>{@link #ores}</li>
     *     <li>{@link #rawOres}</li>
     *     <li>{@link #rawStorageBlocks}</li>
     * </ul>
     */
    public final boolean isNatural;

    public final ItemLikeTag ores;
    public final TagKey<Item> rawOres;
    public final ItemLikeTag rawStorageBlocks;
    public final TagKey<Item> ingots;
    public final ItemLikeTag storageBlocks;
    public final TagKey<Item> nuggets;
    public final TagKey<Item> plates;

    CommonMetal(Mods... mods) {
        this(true, mods);
    }

    CommonMetal(boolean natural, Mods... mods) {
        this.name = Lang.asId(name());
        this.mods = mods.length == 0 ? Set.of() : Collections.unmodifiableSet(EnumSet.copyOf(Set.of(mods)));

        this.isNatural = natural;

        this.ores = new ItemLikeTag("ores/" + this.name);
        this.rawOres = itemTag("raw_materials/" + this.name);
        this.rawStorageBlocks = new ItemLikeTag("storage_blocks/raw_" + this.name);
        this.ingots = itemTag("ingots/" + this.name);
        this.storageBlocks = new ItemLikeTag("storage_blocks/" + this.name);
        this.nuggets = itemTag("nuggets/" + this.name);
        this.plates = itemTag("plates/" + this.name);
    }

    /**
     * @return the set of all metals known to be provided by the given mod.
     */
    public static Set<CommonMetal> of(Mods mod) {
        return metalsOfMods.get(mod);
    }

    private static TagKey<Item> itemTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
    }

    private static TagKey<Block> blockTag(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", path));
    }

    public String getName(Mods mod) {
        // if (this == ALUMINUM && mod == IC2) // include in mods.builder if this happens again
        //     return "aluminium";
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public record ItemLikeTag(TagKey<Item> items, TagKey<Block> blocks) {
        private ItemLikeTag(String path) {
            this(itemTag(path), blockTag(path));
        }
    }
}
