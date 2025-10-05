package group.togawa.stress_expansion.content.processing.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Joiner;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 应力膨胀版本的处理配方抽象
 * 基本上就是原来的那个额外加了点附属要用的东西，没啥改动
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ExpansedProcessingRecipe<I extends RecipeInput, P extends ExpansedProcessingRecipeParams>
        implements Recipe<I> {

    protected P params;
    protected NonNullList<Ingredient> ingredients;
    protected NonNullList<ProcessingOutput> results;
    protected NonNullList<FluidIngredient> fluidIngredients;
    protected NonNullList<FluidStack> fluidResults;
    protected int processingDuration;
    protected ExpansedHeatCondition requiredHeat; // 替换成应力膨胀中的热量等级
    protected ExpansedStressCondition requiredStress; // 添加应力膨胀中的应力等级
    protected double stressMultiplier; // 应力消耗倍率

    private RecipeType<?> type;
    private RecipeSerializer<?> serializer;
    private IRecipeTypeInfo typeInfo;
    private Supplier<ItemStack> forcedResult;

    public ExpansedProcessingRecipe(IRecipeTypeInfo typeInfo, P params) {
        this.params = params;
        this.ingredients = params.ingredients;
        this.fluidIngredients = params.fluidIngredients;
        this.results = params.results;
        this.fluidResults = params.fluidResults;
        this.processingDuration = params.processingDuration;
        this.requiredHeat = params.requiredHeat;
        this.requiredStress = params.requiredStress; // 添加应力膨胀中的应力等级
        this.stressMultiplier = params.stressMultiplier; // 应力消耗倍率
        this.type = typeInfo.getType();
        this.serializer = typeInfo.getSerializer();
        this.typeInfo = typeInfo;
        this.forcedResult = null;
    }

    protected abstract int getMaxInputCount(); // 获取最大输入物品数量

    protected abstract int getMaxOutputCount(); // 获取最大输出物品数量

    protected boolean canRequireHeat() { // 是否可以指定热量等级
        return false;
    }

    protected boolean canSpecifyDuration() { // 是否可以指定处理时间
        return false;
    }

    protected int getMaxFluidInputCount() { // 获取最大输入流体数量
        return 0;
    }

    protected int getMaxFluidOutputCount() { // 获取最大输出流体数量
        return 0;
    }

    protected boolean canRequireStress() { // 是否可以指定应力等级
        return false;
    }

    protected boolean canSpecifyStressMultiplier() { // 是否可以指定应力消耗倍率
        return false;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        int ingredientCount = ingredients.size();
        int outputCount = results.size();

        if (ingredientCount > getMaxInputCount())
            errors.add("Recipe has more item inputs (" + ingredientCount + ") than supported ("
                    + getMaxInputCount() + ").");

        if (outputCount > getMaxOutputCount())
            errors.add("Recipe has more item outputs (" + outputCount + ") than supported ("
                    + getMaxOutputCount() + ").");

        ingredientCount = fluidIngredients.size();
        outputCount = fluidResults.size();

        if (ingredientCount > getMaxFluidInputCount())
            errors.add("Recipe has more fluid inputs (" + ingredientCount + ") than supported ("
                    + getMaxFluidInputCount() + ").");

        if (outputCount > getMaxFluidOutputCount())
            errors.add("Recipe has more fluid outputs (" + outputCount + ") than supported ("
                    + getMaxFluidOutputCount() + ").");

        if (processingDuration > 0 && !canSpecifyDuration())
            errors.add("Recipe specified a duration. Durations have no impact on this type of recipe.");

        if (requiredHeat != ExpansedHeatCondition.NONE && !canRequireHeat())
            errors.add("Recipe specified a heat condition. Heat conditions have no impact on this type of recipe.");

        if (requiredStress != ExpansedStressCondition.NONE && !canRequireStress())
            errors.add("Recipe specified a stress condition. Stress conditions have no impact on this type of recipe.");

        if (stressMultiplier != 1.0 && !canSpecifyStressMultiplier())
            errors.add(
                    "Recipe specified a stress multiplier. Stress multipliers have no impact on this type of recipe.");

        return errors;

    }

    public P getParams() {
        return params;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public NonNullList<FluidIngredient> getFluidIngredients() {
        return fluidIngredients;
    }

    public List<ProcessingOutput> getRollableResults() {
        return results;
    }

    public NonNullList<FluidStack> getFluidResults() {
        return fluidResults;
    }

    public List<ItemStack> getRollableResultsAsItemStacks() {
        return getRollableResults().stream()
                .map(ProcessingOutput::getStack)
                .collect(Collectors.toList());
    }

    public void enforceNextResult(Supplier<ItemStack> stack) {
        forcedResult = stack;
    }

    public List<ItemStack> rollResults(RandomSource randomSource) {
        return rollResults(this.getRollableResults(), randomSource);
    }

    public List<ItemStack> rollResults(List<ProcessingOutput> rollableResults, RandomSource randomSource) {
        List<ItemStack> results = new ArrayList<>();
        for (int i = 0; i < rollableResults.size(); i++) {
            ProcessingOutput output = rollableResults.get(i);
            // ItemStack stack = i == 0 && forcedResult != null ? forcedResult.get() :
            // output.rollOutput(randomSource);
            ItemStack stack = i == 0 && forcedResult != null ? forcedResult.get() : output.rollOutput(); // 这个版本的机械动力还没复用这个randomSource，rollOutput()不需要参数，直接在ProcessingOutput的内部实现了
            if (!stack.isEmpty())
                results.add(stack);
        }
        return results;
    }

    public int getProcessingDuration() {
        return processingDuration;
    }

    public ExpansedHeatCondition getRequiredHeat() {
        return requiredHeat;
    }

    public ExpansedStressCondition getRequiredStress() {
        return requiredStress;
    }

    public double getStressMultiplier() {
        return stressMultiplier;
    }

    // IRecipe<> paperwork

    @Override
    public ItemStack assemble(I t, HolderLookup.Provider provider) {
        return getResultItem(provider);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return getRollableResults().isEmpty() ? ItemStack.EMPTY
                : getRollableResults().getFirst()
                        .getStack();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    // Processing recipes do not show up in the recipe book
    @Override
    public String getGroup() {
        return "processing";
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    @Override
    public RecipeType<?> getType() {
        return type;
    }

    public IRecipeTypeInfo getTypeInfo() {
        return typeInfo;
    }

    public static <P extends ExpansedProcessingRecipeParams, R extends ExpansedProcessingRecipe<?, P>> MapCodec<R> codec(
            Factory<P, R> factory, MapCodec<P> paramsCodec) {
        return paramsCodec.xmap(factory::create, recipe -> recipe.getParams())
                .validate(recipe -> {
                    var errors = recipe.validate();
                    if (errors.isEmpty())
                        return DataResult.success(recipe);
                    errors.add(recipe.getClass().getSimpleName() + " failed validation:");
                    return DataResult.error(() -> Joiner.on('\n').join(errors), recipe);
                });
    }

    public static <P extends ExpansedProcessingRecipeParams, R extends ExpansedProcessingRecipe<?, P>> StreamCodec<RegistryFriendlyByteBuf, R> streamCodec(
            Factory<P, R> factory, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec) {
        return streamCodec.map(factory::create, ExpansedProcessingRecipe::getParams);
    }

    @FunctionalInterface
    public interface Factory<P extends ExpansedProcessingRecipeParams, R extends ExpansedProcessingRecipe<?, P>> {
        R create(P params);
    }

    @FunctionalInterface
    public interface CompatFactory<P extends ProcessingRecipeParams, R extends ProcessingRecipe<?, P>> { // 留着兼容用，虽然我也不知道它有什么用
        R create(P params);
    }

}
