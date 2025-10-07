package group.togawa.stress_expansion.mixin;

import java.util.function.Predicate;

import group.togawa.stress_expansion.content.StressExpansionTags;
import group.togawa.stress_expansion.content.anvil.BrassAnvilBlock;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
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

    /**
     * 为黄铜砧添加掉落时损坏的逻辑
     */
    @Inject(method = "causeFallDamage", at = @At("TAIL"))
    private void stress_expansion$applyBrassAnvilDamage(float fallDistance, float multiplier, DamageSource source,
            CallbackInfoReturnable<Boolean> cir) {
        // A.处理铁砧逻辑
        if (this.blockState.is(BlockTags.ANVIL)) {
            System.out.println("检测到注入的铁砧掉落逻辑开始运行，当前下落距离为：" + fallDistance);
            // 获取FallingBlockEntity实例
            FallingBlockEntity fallingBlock = (FallingBlockEntity)(Object)this;
            // 这里创建一个实体筛选器，筛选出其中的物品实体
            Predicate<Entity> itemEntitySelector = EntitySelector.NO_SPECTATORS
                    .and(entity -> entity instanceof ItemEntity);
            // 筛选出所有物品实体
            fallingBlock.level().getEntitiesOfClass(ItemEntity.class, fallingBlock.getBoundingBox(), itemEntitySelector).forEach(itemEntity -> {
                System.out.println(itemEntity.getItem().getItem().toString());
            });
            
            // 铁砧的掉落损坏逻辑原版已做处理，处理流程在这部分注入的代码之前，所以直接返回就行
            return;
        }
        // B.处理黄铜砧逻辑
        if (this.blockState.is(StressExpansionTags.BlockTags.BRASS_ANVIL.tag)) {
            // 这部分处理黄铜砧掉落时造成的伤害和损毁的逻辑
            int i = Mth.ceil(fallDistance - 1.0F); // 下落距离
            if (i < 0)
                return;
            float f = Math.min(Mth.floor(i * this.fallDamagePerDistance), this.fallDamageMax);
            if (f <= 0.0F)
                return;
            if (((Entity) (Object) this).getRandom().nextFloat() < 0.03F + i * 0.03F) {
                // 黄铜砧掉落时损坏的概率更低，基础概率3%，每增加1个单位下落距离，概率增加3%
                BlockState damagedState = BrassAnvilBlock.damage(this.blockState);
                if (damagedState == null) {
                    this.cancelDrop = true; // 无法再损坏，取消掉落
                } else {
                    this.blockState = damagedState; // 更新方块状态以便后续回写或继续存在
                }
            }
        }
    }
}