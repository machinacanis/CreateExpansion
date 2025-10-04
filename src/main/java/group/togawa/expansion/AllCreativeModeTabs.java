package group.togawa.expansion;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;


public class AllCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StressExpansion.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SE_TAB = TAB_REGISTER.register("se_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(AllBlocks.NICKEL_ORE.get()))
                    .title(Component.translatable("creativetab.stress_expansion.se_tab"))
                    .build()
    );

    public static void register(IEventBus modEventBus, Logger logger) {
        logger.info("Registering creative mode tabs...");
        TAB_REGISTER.register(modEventBus);
    }
}
