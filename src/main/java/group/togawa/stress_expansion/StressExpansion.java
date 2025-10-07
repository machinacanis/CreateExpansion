package group.togawa.stress_expansion;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(StressExpansion.MOD_ID)
public class StressExpansion {

    public static final String MOD_ID = "stress_expansion";
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * CreateRegistrate 实例，用于注册内容
     */
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(
        MOD_ID
    ).defaultCreativeTab(AllCreativeModeTabs.SE_TAB.getKey()); // 注册内容的 CreateRegistrate 实例

    /**
     * 构造函数
     */
    public StressExpansion(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Expansing the STRENGTH of Create!!!");
        // ModLoadingContext modLoadingContext = ModLoadingContext.get(); // 这个暂时用不到

        REGISTRATE.registerEventListeners(modEventBus); // 设置 CreateRegistrate 实例的事件监听器

        AllBlocks.register(LOGGER); // 所有方块
        AllItems.register(LOGGER); // 所有物品
        AllCreativeModeTabs.register(modEventBus, LOGGER); // 创造模式物品栏

        AllRecipeTypes.register(modEventBus); // 配方类型

        modEventBus.addListener(StressExpansion::init); // 监听一个初始化事件
    }

    /**
     * 返回 CreateRegistrate 实例用于注册内容
     *
     * @return CreateRegistrate 实例
     */
    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing...");
    }
}
