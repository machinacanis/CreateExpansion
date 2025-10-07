package group.togawa.stress_expansion.foundation.data.recipe;

import group.togawa.stress_expansion.AllItems;
import group.togawa.stress_expansion.StressExpansion;
import group.togawa.stress_expansion.api.data.recipe.ExpansedPressingRecipeGen;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

/**
 * 用于注册应力膨胀的压制配方
 */
public final class CreatePressingRecipeGen extends ExpansedPressingRecipeGen {

    GeneratedRecipe TEST_RECIPE = create(
        () -> AllItems.NICKEL_INGOT,
        b -> b.output(AllItems.NICKEL_SHEET)
    );

    public CreatePressingRecipeGen(
        PackOutput output,
        CompletableFuture<Provider> registries
    ) {
        super(output, registries, StressExpansion.MOD_ID);
    }

    private GeneratedRecipe moddedPaths(Mods mod, String... blocks) {
        for (String block : blocks) {
            moddedCompacting(mod, block, block + "_path");
        }
        return null;
    }

    GeneratedRecipe moddedCompacting(Mods mod, String input, String output) {
        return create("compat/" + mod.getId() + "/" + output, b ->
            b.require(mod, input).output(mod, output).whenModLoaded(mod.getId())
        );
    }
}
