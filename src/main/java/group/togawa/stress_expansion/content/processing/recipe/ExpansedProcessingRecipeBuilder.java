package group.togawa.stress_expansion.content.processing.recipe;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.data.SimpleDatagenIngredient;
import com.simibubi.create.foundation.data.recipe.Mods;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.tterrag.registrate.util.DataIngredient;

import group.togawa.stress_expansion.content.processing.recipe.ExpansedProcessingRecipe.Factory;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 应力膨胀版本的处理配方构建器
 * 基本上是复刻了机械动力本身的ProcessingRecipeBuilder，在这之上加入了应力膨胀的添加的参数
 *
 * @param <P> 处理配方参数类型
 * @param <R> 处理配方类型
 * @param <S> 处理配方构建器类型
 */
public abstract class ExpansedProcessingRecipeBuilder<P extends ExpansedProcessingRecipeParams, R extends ExpansedProcessingRecipe<?, P>, S extends ExpansedProcessingRecipeBuilder<P, R, S>> {
    protected ResourceLocation recipeId;
    protected Factory<P, R> factory;
    protected P params;
    protected List<ICondition> recipeConditions;

    public ExpansedProcessingRecipeBuilder(Factory<P, R> factory, ResourceLocation recipeId) {
        this.recipeId = recipeId;
        this.factory = factory;
        this.params = createParams();
        this.recipeConditions = new ArrayList<>();
    }

    protected abstract P createParams();

    public abstract S self();

    public S withItemIngredients(Ingredient... ingredients) {
        return withItemIngredients(NonNullList.of(Ingredient.EMPTY, ingredients));
    }

    public S withItemIngredients(NonNullList<Ingredient> ingredients) {
        params.ingredients = ingredients;
        return self();
    }

    public S withSingleItemOutput(ItemStack output) {
        return withItemOutputs(new ProcessingOutput(output, 1));
    }

    public S withItemOutputs(ProcessingOutput... outputs) {
        return withItemOutputs(NonNullList.of(ProcessingOutput.EMPTY, outputs));
    }

    public S withItemOutputs(NonNullList<ProcessingOutput> outputs) {
        params.results = outputs;
        return self();
    }

    public S withFluidIngredients(FluidIngredient... ingredients) {
        return withFluidIngredients(NonNullList.of(FluidIngredient.EMPTY, ingredients));
    }

    public S withFluidIngredients(NonNullList<FluidIngredient> ingredients) {
        params.fluidIngredients = ingredients;
        return self();
    }

    public S withFluidOutputs(FluidStack... outputs) {
        return withFluidOutputs(NonNullList.of(FluidStack.EMPTY, outputs));
    }

    public S withFluidOutputs(NonNullList<FluidStack> outputs) {
        params.fluidResults = outputs;
        return self();
    }

    public S duration(int ticks) {
        params.processingDuration = ticks;
        return self();
    }

    public S averageProcessingDuration() {
        return duration(100);
    }

    public S requiresHeat(ExpansedHeatCondition condition) { // 这里替换成应力膨胀的热量条件枚举
        params.requiredHeat = condition;
        return self();
    }

    public S requiresStress(ExpansedStressCondition condition) { // 添加应力膨胀的应力条件枚举
        params.requiredStress = condition;
        return self();
    }

    public S stressMultiplier(float multiplier) { // 添加应力膨胀的应力消耗系数
        params.stressMultiplier = multiplier;
        return self();
    }

    public R build() {
        return factory.create(params);
    }

    public void build(RecipeOutput consumer) {
        R recipe = build();
        IRecipeTypeInfo recipeType = recipe.getTypeInfo();
        ResourceLocation typeId = recipeType.getId();
        ResourceLocation id = recipeId.withPrefix(typeId.getPath() + "/");
        var errors = recipe.validate();
        if (!errors.isEmpty()) {
            errors.add(recipe.getClass().getSimpleName() + "with id " + id + " failed validation:");
            Create.LOGGER.warn(Joiner.on('\n').join(errors));
        }
        consumer.accept(id, recipe, null, recipeConditions.toArray(new ICondition[0]));
    }

    // Datagen shortcuts

    public S require(TagKey<Item> tag) {
        return require(Ingredient.of(tag));
    }

    public S require(ItemLike item) {
        return require(Ingredient.of(item));
    }

    public S require(Ingredient ingredient) {
        params.ingredients.add(ingredient);
        return self();
    }

    public S require(ICustomIngredient ingredient) {
        params.ingredients.add(ingredient.toVanilla());
        return self();
    }

    public S require(Mods mod, String id) {
        params.ingredients.add(new SimpleDatagenIngredient(mod, id).toVanilla());
        return self();
    }

    public S require(Fluid fluid, int amount) {
        return require(FluidIngredient.fromFluid(fluid, amount));
    }

    public S require(TagKey<Fluid> fluidTag, int amount) {
        return require(FluidIngredient.fromTag(fluidTag, amount));
    }

    public S require(FluidIngredient ingredient) {
        params.fluidIngredients.add(ingredient);
        return self();
    }

    // 后面都是原来的方法，一点没动

    public S output(ItemLike item) {
        return output(item, 1);
    }

    public S output(float chance, ItemLike item) {
        return output(chance, item, 1);
    }

    public S output(ItemLike item, int amount) {
        return output(1, item, amount);
    }

    public S output(float chance, ItemLike item, int amount) {
        return output(chance, new ItemStack(item, amount));
    }

    public S output(ItemStack output) {
        return output(1, output);
    }

    public S output(float chance, ItemStack output) {
        return output(new ProcessingOutput(output, chance));
    }

    public S output(float chance, Mods mod, String id, int amount) {
        return output(new ProcessingOutput(mod.asResource(id), amount, chance));
    }

    public S output(ResourceLocation id) {
        return output(1, id, 1);
    }

    public S output(Mods mod, String id) {
        return output(1, mod.asResource(id), 1);
    }

    public S output(float chance, ResourceLocation registryName, int amount) {
        return output(new ProcessingOutput(registryName, amount, chance));
    }

    public S output(ProcessingOutput output) {
        params.results.add(output);
        return self();
    }

    public S output(Fluid fluid, int amount) {
        fluid = FluidHelper.convertToStill(fluid);
        return output(new FluidStack(fluid, amount));
    }

    public S output(FluidStack fluidStack) {
        params.fluidResults.add(fluidStack);
        return self();
    }

    //

    public S whenModLoaded(String modid) {
        return withCondition(new ModLoadedCondition(modid));
    }

    public S whenModMissing(String modid) {
        return withCondition(new NotCondition(new ModLoadedCondition(modid)));
    }

    public S withCondition(ICondition condition) {
        recipeConditions.add(condition);
        return self();
    }
}
