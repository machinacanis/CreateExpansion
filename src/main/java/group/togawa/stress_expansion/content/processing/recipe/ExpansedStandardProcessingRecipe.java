package group.togawa.stress_expansion.content.processing.recipe;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 应力膨胀版本的标准处理配方，基本就是照着机械动力的改的
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ExpansedStandardProcessingRecipe<T extends RecipeInput>
        extends ExpansedProcessingRecipe<T, ExpansedProcessingRecipeParams> {
    public ExpansedStandardProcessingRecipe(IRecipeTypeInfo typeInfo, ExpansedProcessingRecipeParams params) {
        super(typeInfo, params);
    }

    @FunctionalInterface
    public interface Factory<R extends ExpansedStandardProcessingRecipe<?>> // 定义一个工厂函数接口，用于创建处理配方
            extends ExpansedProcessingRecipe.Factory<ExpansedProcessingRecipeParams, R> {
        R create(ExpansedProcessingRecipeParams params);
    }

    public static class Builder<R extends ExpansedStandardProcessingRecipe<?>>
            extends ExpansedProcessingRecipeBuilder<ExpansedProcessingRecipeParams, R, Builder<R>> {

        public Builder(Factory<R> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
        }

        @Override
        protected ExpansedProcessingRecipeParams createParams() {
            return new ExpansedProcessingRecipeParams();
        }

        @Override
        public Builder<R> self() {
            return this;
        }
    }

    public static class Serializer<R extends ExpansedStandardProcessingRecipe<?>> implements RecipeSerializer<R> {
        private final Factory<R> factory;
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

        public Serializer(Factory<R> factory) {
            this.factory = factory;
            this.codec = ExpansedProcessingRecipe.codec(factory, ExpansedProcessingRecipeParams.CODEC);
            this.streamCodec = ExpansedProcessingRecipe.streamCodec(factory,
                    ExpansedProcessingRecipeParams.STREAM_CODEC);
        }

        @Override
        public MapCodec<R> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
            return streamCodec;
        }

        public Factory<R> factory() {
            return factory;
        }
    }
}