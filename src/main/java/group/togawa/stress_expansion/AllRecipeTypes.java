package group.togawa.stress_expansion;

import group.togawa.stress_expansion.content.processing.recipe.ExpansedStandardProcessingRecipe;
import group.togawa.stress_expansion.content.recipe_types.ExpansedPressingRecipe;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.createmod.catnip.lang.Lang;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public enum AllRecipeTypes implements IRecipeTypeInfo, StringRepresentable {
    EXPANSED_PRESSING(ExpansedPressingRecipe::new);

    public static final Predicate<RecipeHolder<?>> CAN_BE_AUTOMATED = r -> !r.id()
            .getPath()
            .endsWith("_manual_only");

    public final ResourceLocation id;
    public final Supplier<RecipeSerializer<?>> serializerSupplier;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;
    @Nullable
    private final DeferredHolder<RecipeType<?>, RecipeType<?>> typeObject;
    private final Supplier<RecipeType<?>> type;

    private boolean isProcessingRecipe;

    public static final Codec<AllRecipeTypes> CODEC = StringRepresentable.fromEnum(AllRecipeTypes::values);

    AllRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier,
            boolean registerType) {
        String name = Lang.asId(name());
        id = StressExpansion.asResource(name);
        this.serializerSupplier = serializerSupplier;
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        if (registerType) {
            typeObject = Registers.TYPE_REGISTER.register(name, typeSupplier);
            type = typeObject;
        } else {
            typeObject = null;
            type = typeSupplier;
        }
        isProcessingRecipe = false;
    }

    AllRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        id = StressExpansion.asResource(name);
        this.serializerSupplier = serializerSupplier;
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        typeObject = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
        type = typeObject;
        isProcessingRecipe = false;
    }

    AllRecipeTypes(ExpansedStandardProcessingRecipe.Factory<?> processingFactory) {
        this(() -> new ExpansedStandardProcessingRecipe.Serializer<>(processingFactory));
        isProcessingRecipe = true;
    }

    // 这部分代码是用来处理手动应用/机械手配方的，这个等我们写到跟这个有关的内容之后再处理
    // 总之就是先放着

    // AllRecipeTypes(
    // ProcessingRecipe.Factory<ItemApplicationRecipeParams, ? extends
    // ItemApplicationRecipe> itemApplicationFactory) {
    // this(() -> new ItemApplicationRecipe.Serializer<>(itemApplicationFactory));
    // isProcessingRecipe = true;
    // }

    @Internal
    public static void register(IEventBus modEventBus) {
        ShapedRecipePattern.setCraftingSize(9, 9);
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type.get();
    }

    public <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> find(I inv, Level world) {
        return world.getRecipeManager()
                .getRecipeFor(getType(), inv, world);
    }

    public static boolean shouldIgnoreInAutomation(RecipeHolder<?> recipe) {
        RecipeSerializer<?> serializer = recipe.value().getSerializer();
        if (serializer != null && AllTags.AllRecipeSerializerTags.AUTOMATION_IGNORE.matches(serializer))
            return true;
        return !CAN_BE_AUTOMATED.test(recipe);
    }

    @Override
    public @NotNull String getSerializedName() {
        return id.toString();
    }

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister
                .create(BuiltInRegistries.RECIPE_SERIALIZER, StressExpansion.MOD_ID);
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister
                .create(Registries.RECIPE_TYPE, StressExpansion.MOD_ID);
    }

}
