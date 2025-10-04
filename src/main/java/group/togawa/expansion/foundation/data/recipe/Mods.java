package group.togawa.expansion.foundation.data.recipe;

import com.simibubi.create.Create;
import group.togawa.expansion.StressExpansion;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * 模组物品ID枚举类
 * 实现了 <a href="https://github.com/Creators-of-Create/Create/blob/mc1.21.1/dev/src/main/java/com/simibubi/create/foundation/data/recipe/Mods.java">Github</a>
 * 这里提到的一个helper class
 */
public enum Mods {
    VANILLA("minecraft"),
    CREATE(Create.ID),
    STRESS_EXPANSION(StressExpansion.MOD_ID);

    private final String id;

    public boolean reversedMetalPrefix;
    public boolean strippedIsSuffix;
    public boolean omitWoodSuffix;

    Mods(String id) {
        this(id, b -> {
        });
    }

    Mods(String id, Consumer<Builder> props) {
        props.accept(new Builder());
        this.id = id;
    }

    public ResourceLocation ingotOf(String type) {
        return ResourceLocation.fromNamespaceAndPath(id, reversedMetalPrefix ? "ingot_" + type : type + "_ingot");
    }

    public ResourceLocation nuggetOf(String type) {
        return ResourceLocation.fromNamespaceAndPath(id, reversedMetalPrefix ? "nugget_" + type : type + "_nugget");
    }

    public ResourceLocation oreOf(String type) {
        return ResourceLocation.fromNamespaceAndPath(id, reversedMetalPrefix ? "ore_" + type : type + "_ore");
    }

    public ResourceLocation deepslateOreOf(String type) {
        return ResourceLocation.fromNamespaceAndPath(id, reversedMetalPrefix ? "deepslate_ore_" + type : "deepslate_" + type + "_ore");
    }

    public ResourceLocation asResource(String id) {
        return ResourceLocation.fromNamespaceAndPath(this.id, id);
    }

    public String recipeId(String id) {
        return "compat/" + this.id + "/" + id;
    }

    public String getId() {
        return id;
    }

    class Builder {

        Builder reverseMetalPrefix() {
            reversedMetalPrefix = true;
            return this;
        }

        Builder strippedWoodIsSuffix() {
            strippedIsSuffix = true;
            return this;
        }

        Builder omitWoodSuffix() {
            omitWoodSuffix = true;
            return this;
        }

    }
}
