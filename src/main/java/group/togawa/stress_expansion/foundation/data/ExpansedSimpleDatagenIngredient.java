package group.togawa.stress_expansion.foundation.data;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.mixin.accessor.MappedRegistryAccessor;
import group.togawa.stress_expansion.foundation.data.recipe.Mods;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

/**
 * 用于生成数据包中的配方成分，这个是直接从机械动力那边抄下来改的
 */
public class ExpansedSimpleDatagenIngredient implements ICustomIngredient {

    /*
     * "ingredients": [
     * {
     * "item": "mod:compat_item"
     * }
     * ]
     */

    private static final MapCodec<
        ExpansedSimpleDatagenIngredient
    > INTERNAL_CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance
            .group(
                ResourceLocation.CODEC.fieldOf("item").forGetter(i ->
                    i.mod.asResource(i.id)
                )
            )
            .apply(instance, location -> {
                for (Mods mod : Mods.values()) {
                    if (mod.getId().equals(location.getNamespace())) {
                        return new ExpansedSimpleDatagenIngredient(
                            mod,
                            location.getPath()
                        );
                    }
                }
                throw new AssertionError(
                    "ID " +
                        location.getNamespace() +
                        " doesn't correspond to any compat mod." +
                        " SimpleDatagenIngredient is not meant for deserialization anyway"
                );
            })
    );

    private static final MapCodec<ExpansedSimpleDatagenIngredient> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            instance
                .group(
                    INTERNAL_CODEC.codec()
                        .listOf()
                        .fieldOf("ingredients")
                        .forGetter(List::of)
                )
                .apply(instance, list -> {
                    assert list.size() ==
                    1 : "SimpleDatagenIngredient should only be serialized as a single-element list, and shouldn't be deserialized anyway";
                    return list.getFirst();
                })
        );
    private static final IngredientType<?> INGREDIENT_TYPE =
        new IngredientType<>(CODEC);

    private final Mods mod;
    private final String id;

    public ExpansedSimpleDatagenIngredient(Mods mod, String id) {
        this.mod = mod;
        this.id = id;
    }

    @Override
    public boolean test(@NotNull ItemStack stack) {
        return stack
            .getItemHolder()
            .getKey()
            .location()
            .equals(mod.asResource(id));
    }

    @Override
    public @NotNull Stream<ItemStack> getItems() {
        return Stream.empty();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    private static boolean didRegistryInjection = false;

    @Override
    public @NotNull IngredientType<?> getType() {
        if (!didRegistryInjection) {
            // Need to do some registry injection to get the Registry#byNameCodec to encode
            // the right type for this
            // getResourceKey and getId
            // byValue and toId
            // Holder.Reference: key
            if (
                NeoForgeRegistries.INGREDIENT_TYPES instanceof
                    MappedRegistryAccessor<?> mra
            ) {
                @SuppressWarnings("unchecked")
                MappedRegistryAccessor<IngredientType<?>> mra$ =
                    (MappedRegistryAccessor<IngredientType<?>>) mra;

                IngredientType<?> baseType =
                    NeoForgeMod.COMPOUND_INGREDIENT_TYPE.get();

                int wrappedId = mra$.getToId().getOrDefault(baseType, -1);
                ResourceKey<IngredientType<?>> wrappedKey =
                    NeoForgeMod.COMPOUND_INGREDIENT_TYPE.getKey();

                mra$.getToId().put(INGREDIENT_TYPE, wrappedId);
                // noinspection DataFlowIssue - it is ok to pass null as the owner, because this
                // is only being used for serialization
                mra$
                    .getByValue()
                    .put(
                        INGREDIENT_TYPE,
                        Holder.Reference.createStandAlone(null, wrappedKey)
                    );

                /*
                 * {
                 * "type": "neoforge:compound",
                 * "ingredients": [
                 * {
                 * "item": "mod:compat_item"
                 * }
                 * ]
                 * }
                 */

                didRegistryInjection = true;
            } else {
                throw new AssertionError(
                    "SimpleDatagenIngredient will not be able to" +
                        " serialize without injecting into a registry. Expected" +
                        " NeoForgeRegistries.INGREDIENT_TYPES to be of class MappedRegistry, is of class " +
                        NeoForgeRegistries.INGREDIENT_TYPES.getClass()
                );
            }
        }
        return INGREDIENT_TYPE;
    }
}
