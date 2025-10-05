package group.togawa.stress_expansion.content;

import com.simibubi.create.Create;
import group.togawa.stress_expansion.StressExpansion;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class StressExpansionTags {
    public enum NameSpace {
        CREATE(Create.ID),
        COMMON("c"),
        STRESS_EXPANSION(StressExpansion.MOD_ID);

        public final String id;

        NameSpace(String id) {
            this.id = id;
        }

        public ResourceLocation id(String path) {
            return ResourceLocation.fromNamespaceAndPath(this.id, path);
        }

        public ResourceLocation id(Enum<?> entry, @Nullable String pathOverride) {
            return this.id(pathOverride != null ? pathOverride : Lang.asId(entry.name()));
        }
    }

    public enum BlockTags {
        BRASS_ANVIL,
        ;

        public final TagKey<Block> tag;

        BlockTags() {
            this(NameSpace.STRESS_EXPANSION);
        }

        BlockTags(NameSpace namespace) {
            this(namespace, null);
        }

        BlockTags(NameSpace namespace, @Nullable String pathOverride) {
            this.tag = TagKey.create(Registries.BLOCK, namespace.id(this, pathOverride));
        }

        @SuppressWarnings("deprecation")
        public boolean matches(Block block) {
            return block.builtInRegistryHolder()
                    .is(tag);
        }

        public boolean matches(ItemStack stack) {
            return stack != null && stack.getItem() instanceof BlockItem blockItem && matches(blockItem.getBlock());
        }

        public boolean matches(BlockState state) {
            return state.is(tag);
        }

    }

}
