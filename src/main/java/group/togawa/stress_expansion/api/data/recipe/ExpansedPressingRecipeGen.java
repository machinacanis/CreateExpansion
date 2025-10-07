package group.togawa.stress_expansion.api.data.recipe;

import com.simibubi.create.content.kinetics.press.PressingRecipe;
import group.togawa.stress_expansion.AllRecipeTypes;
import group.togawa.stress_expansion.content.recipe_types.ExpansedPressingRecipe;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

/**
 * 应力膨胀版本的压制配方生成器
 * 暂时还没做什么修改，除了用的是应力膨胀的配方实现之外和原版没什么区别
 */
public abstract class ExpansedPressingRecipeGen
    extends ExpansedStandardProcessingRecipeGen<ExpansedPressingRecipe> {

    public ExpansedPressingRecipeGen(
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> registries,
        String defaultNamespace
    ) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.EXPANSED_PRESSING;
    }
}
