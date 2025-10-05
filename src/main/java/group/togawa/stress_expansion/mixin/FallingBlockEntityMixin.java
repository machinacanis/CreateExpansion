package group.togawa.stress_expansion.mixin;

import group.togawa.stress_expansion.content.StressExpansionTags;
import group.togawa.stress_expansion.content.anvil.BrassAnvilBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {
    @Shadow
    private BlockState blockState;
    @Shadow
    private boolean cancelDrop;
    @Shadow
    private int fallDamageMax;
    @Shadow
    private float fallDamagePerDistance;

    @Inject(method = "causeFallDamage", at = @At("TAIL"))
    private void stress_expansion$applyBrassAnvilDamage(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        // 若本次路径已在原版铁砧逻辑中处理（铁砧标签），直接跳过
        if (this.blockState.is(BlockTags.ANVIL)) return;
        // 只处理自定义黄铜砧标签
        if (!this.blockState.is(StressExpansionTags.BlockTags.BRASS_ANVIL.tag)) return;

        // System.out.println("FallingBlockEntityMixin: Applying brass anvil damage logic");

        int i = Mth.ceil(fallDistance - 1.0F);
        if (i < 0) return;
        float f = Math.min(Mth.floor(i * this.fallDamagePerDistance), this.fallDamageMax);
        if (f <= 0.0F) return;

        // 与原版铁砧相同的随机概率：0.05F + i * 0.05F
        if (((Entity) (Object) this).getRandom().nextFloat() < 0.05F + i * 0.05F) {
            BlockState damagedState = BrassAnvilBlock.damage(this.blockState);
            if (damagedState == null) {
                this.cancelDrop = true; // 最终状态 -> 取消放置掉落为方块
            } else {
                this.blockState = damagedState; // 更新方块状态以便后续回写或继续存在
            }
        }
    }
}