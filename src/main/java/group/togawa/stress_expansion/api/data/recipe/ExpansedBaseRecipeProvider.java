package group.togawa.stress_expansion.api.data.recipe;

import group.togawa.stress_expansion.StressExpansion;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * 应力膨胀版本的基础配方提供器
 * 基本上和机械动力原版实现的BaseRecipeProvider没区别
 * 用于简化配方注册流程
 */
public abstract class ExpansedBaseRecipeProvider extends RecipeProvider {

    protected final String modid;
    protected final List<GeneratedRecipe> all = new ArrayList<>();

    public ExpansedBaseRecipeProvider(
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> registries,
        String defaultNamespace
    ) {
        super(output, registries);
        this.modid = defaultNamespace;
    }

    protected ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(modid, path);
    }

    protected GeneratedRecipe register(GeneratedRecipe recipe) {
        all.add(recipe);
        return recipe;
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        all.forEach(c -> c.register(recipeOutput));
        StressExpansion.LOGGER.info(
            "{} registered {} recipe{}",
            getName(),
            all.size(),
            all.size() == 1 ? "" : "s"
        );
    }

    @FunctionalInterface
    public interface GeneratedRecipe {
        void register(RecipeOutput recipeOutput);
    }
}
