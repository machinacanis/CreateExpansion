package group.togawa.stress_expansion.api.data.recipe;

import group.togawa.stress_expansion.content.processing.recipe.ExpansedProcessingRecipeParams;
import group.togawa.stress_expansion.content.processing.recipe.ExpansedStandardProcessingRecipe;
import group.togawa.stress_expansion.content.processing.recipe.ExpansedStandardProcessingRecipe.Builder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

/**
 * 应力膨胀版本的标准加工配方生成器
 * 用于简化各种标准加工类配方的注册
 *
 * @param <R> 配方类型
 */
public abstract class ExpansedStandardProcessingRecipeGen<
    R extends ExpansedStandardProcessingRecipe<?>
>
    extends ExpansedProcessingRecipeGen<
        ExpansedProcessingRecipeParams,
        R,
        ExpansedStandardProcessingRecipe.Builder<R>
    > {

    public ExpansedStandardProcessingRecipeGen(
        PackOutput output,
        CompletableFuture<Provider> registries,
        String defaultNamespace
    ) {
        super(output, registries, defaultNamespace);
    }

    protected ExpansedStandardProcessingRecipe.Serializer<R> getSerializer() {
        return getRecipeType().getSerializer();
    }

    @Override
    protected Builder<R> getBuilder(ResourceLocation id) {
        return new ExpansedStandardProcessingRecipe.Builder<>(
            getSerializer().factory(),
            id
        );
    }
}
