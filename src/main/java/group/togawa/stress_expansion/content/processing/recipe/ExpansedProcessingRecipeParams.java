package group.togawa.stress_expansion.content.processing.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 应力膨胀的配方参数设定，对机械动力原本的处理配方参数进行了扩展
 * 加入了应力网络等级、应力消耗倍率的设定
 */
public class ExpansedProcessingRecipeParams {
    public static MapCodec<ExpansedProcessingRecipeParams> CODEC = codec(ExpansedProcessingRecipeParams::new);
    public static StreamCodec<RegistryFriendlyByteBuf, ExpansedProcessingRecipeParams> STREAM_CODEC = streamCodec(
            ExpansedProcessingRecipeParams::new);

    protected NonNullList<Ingredient> ingredients; // 输入物品
    protected NonNullList<ProcessingOutput> results; // 输出物品
    protected NonNullList<FluidIngredient> fluidIngredients; // 输入流体
    protected NonNullList<FluidStack> fluidResults; // 输出流体
    protected int processingDuration; // 处理时间
    protected ExpansedHeatCondition requiredHeat; // 所需温度等级
    protected ExpansedStressCondition requiredStress; // 所需应力等级
    protected double stressMultiplier = 1.0; // 应力消耗倍率

    /**
     * 构造函数，初始化配方参数
     */
    protected ExpansedProcessingRecipeParams() {
        ingredients = NonNullList.create();
        results = NonNullList.create();
        fluidIngredients = NonNullList.create();
        fluidResults = NonNullList.create();
        processingDuration = 0;
        requiredHeat = ExpansedHeatCondition.NONE; // 热量消耗等级，默认值为NONE
        requiredStress = ExpansedStressCondition.NONE; // 应力网络等级，默认值为NONE
        stressMultiplier = 1.0; // 应力消耗倍率，默认值为1.0
    }

    protected static <P extends ExpansedProcessingRecipeParams> MapCodec<P> codec(Supplier<P> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.either(FluidIngredient.CODEC, Ingredient.CODEC).listOf().fieldOf("ingredients")
                        .forGetter(ExpansedProcessingRecipeParams::ingredients),
                Codec.either(FluidStack.CODEC, ProcessingOutput.CODEC).listOf().fieldOf("results")
                        .forGetter(ExpansedProcessingRecipeParams::results),
                Codec.INT.optionalFieldOf("processing_time", 0)
                        .forGetter(ExpansedProcessingRecipeParams::processingDuration),
                ExpansedHeatCondition.CODEC.optionalFieldOf("heat_requirement", ExpansedHeatCondition.NONE) // 替换成应力膨胀的热量消耗等级
                        .forGetter(ExpansedProcessingRecipeParams::requiredHeat),
                ExpansedStressCondition.CODEC.optionalFieldOf("stress_requirement", ExpansedStressCondition.NONE) // 添加应力膨胀的应力网络等级
                        .forGetter(ExpansedProcessingRecipeParams::requiredStress),
                Codec.DOUBLE.optionalFieldOf("stress_multiplier", 1.0) // 添加应力膨胀的应力消耗倍率
                        .forGetter(ExpansedProcessingRecipeParams::stressMultiplier))
                .apply(instance,
                        (ingredients, results, processingDuration, requiredHeat, requiredStress, stressMultiplier) -> {
                            P params = factory.get();
                            ingredients.forEach(either -> either
                                    .ifRight(params.ingredients::add)
                                    .ifLeft(params.fluidIngredients::add));
                            results.forEach(either -> either
                                    .ifRight(params.results::add)
                                    .ifLeft(params.fluidResults::add));
                            params.processingDuration = processingDuration;
                            params.requiredHeat = requiredHeat;
                            params.requiredStress = requiredStress;
                            params.stressMultiplier = stressMultiplier;
                            return params;
                        }));
    }

    /**
     * 流式编码解码器，用于网络传输
     */
    protected static <P extends ExpansedProcessingRecipeParams> StreamCodec<RegistryFriendlyByteBuf, P> streamCodec(
            Supplier<P> factory) {
        return StreamCodec.of(
                (buffer, params) -> params.encode(buffer),
                buffer -> {
                    P params = factory.get();
                    params.decode(buffer);
                    return params;
                });
    }

    /**
     * 获取所有输入物品和流体
     */
    protected final List<Either<FluidIngredient, Ingredient>> ingredients() {
        List<Either<FluidIngredient, Ingredient>> ingredients = new ArrayList<>(
                this.ingredients.size() + this.fluidIngredients.size());
        this.ingredients.forEach(ingredient -> ingredients.add(Either.right(ingredient)));
        this.fluidIngredients.forEach(ingredient -> ingredients.add(Either.left(ingredient)));
        return ingredients;
    }

    /**
     * 获取所有输出物品和流体
     */
    protected final List<Either<FluidStack, ProcessingOutput>> results() {
        List<Either<FluidStack, ProcessingOutput>> results = new ArrayList<>(
                this.results.size() + this.fluidResults.size());
        this.results.forEach(result -> results.add(Either.right(result)));
        this.fluidResults.forEach(result -> results.add(Either.left(result)));
        return results;
    }

    /**
     * 获取处理时间
     */
    protected final int processingDuration() {
        return processingDuration;
    }

    /**
     * 获取所需热量等级
     */
    protected final ExpansedHeatCondition requiredHeat() {
        return requiredHeat;
    }

    /**
     * 获取所需应力等级
     */
    protected final ExpansedStressCondition requiredStress() {
        return requiredStress;
    }

    /**
     * 获取应力消耗倍率
     */
    protected final double stressMultiplier() {
        return stressMultiplier;
    }

    /**
     * 编码参数到网络缓冲区
     */
    protected void encode(RegistryFriendlyByteBuf buffer) {
        CatnipStreamCodecBuilders.nonNullList(Ingredient.CONTENTS_STREAM_CODEC).encode(buffer, ingredients);
        CatnipStreamCodecBuilders.nonNullList(FluidIngredient.STREAM_CODEC).encode(buffer, fluidIngredients);
        CatnipStreamCodecBuilders.nonNullList(ProcessingOutput.STREAM_CODEC).encode(buffer, results);
        CatnipStreamCodecBuilders.nonNullList(FluidStack.STREAM_CODEC).encode(buffer, fluidResults);
        ByteBufCodecs.VAR_INT.encode(buffer, processingDuration);
        ExpansedHeatCondition.STREAM_CODEC.encode(buffer, requiredHeat);
        ExpansedStressCondition.STREAM_CODEC.encode(buffer, requiredStress);
        ByteBufCodecs.DOUBLE.encode(buffer, stressMultiplier);
    }

    /**
     * 从网络缓冲区解码参数
     */
    protected void decode(RegistryFriendlyByteBuf buffer) {
        ingredients = CatnipStreamCodecBuilders.nonNullList(Ingredient.CONTENTS_STREAM_CODEC).decode(buffer);
        fluidIngredients = CatnipStreamCodecBuilders.nonNullList(FluidIngredient.STREAM_CODEC).decode(buffer);
        results = CatnipStreamCodecBuilders.nonNullList(ProcessingOutput.STREAM_CODEC).decode(buffer);
        fluidResults = CatnipStreamCodecBuilders.nonNullList(FluidStack.STREAM_CODEC).decode(buffer);
        processingDuration = ByteBufCodecs.VAR_INT.decode(buffer);
        requiredHeat = ExpansedHeatCondition.STREAM_CODEC.decode(buffer);
        requiredStress = ExpansedStressCondition.STREAM_CODEC.decode(buffer);
        stressMultiplier = ByteBufCodecs.DOUBLE.decode(buffer);
    }

}
