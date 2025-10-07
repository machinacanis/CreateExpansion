package group.togawa.stress_expansion.mixin;

import group.togawa.stress_expansion.content.StressExpansionTags;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    /**
     * 为黄铜砧添加 isValidBlock 逻辑使其可以正常打开铁砧的界面
     */
    @Inject(method = "isValidBlock", at = @At("RETURN"), cancellable = true)
    private void stress_expansion$checkBrassAnvil(
        BlockState state,
        CallbackInfoReturnable<Boolean> cir
    ) {
        // 若方块为黄铜砧，也返回true
        if (state.is(StressExpansionTags.BlockTags.BRASS_ANVIL.tag)) {
            cir.setReturnValue(true);
        }
    }
}
