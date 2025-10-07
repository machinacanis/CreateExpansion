package group.togawa.stress_expansion.content.anvil;

import group.togawa.stress_expansion.AllBlocks;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BrassAnvilBlock extends AnvilBlock {
    private static final float FALL_DAMAGE_PER_DISTANCE = 3.0F; // 每格高度造成的掉落伤害
    private static final int FALL_DAMAGE_MAX = 60; // 掉落伤害上限

    public BrassAnvilBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    public static BlockState damage(BlockState state) {
        if (state.is(AllBlocks.BRASS_ANVIL.get())) {
            return AllBlocks.CHIPPED_BRASS_ANVIL.get().defaultBlockState().setValue(FACING, state.getValue(FACING));
        } else if (state.is(AllBlocks.CHIPPED_BRASS_ANVIL.get())) {
            return AllBlocks.DAMAGED_BRASS_ANVIL.get().defaultBlockState().setValue(FACING, state.getValue(FACING));
        } else {
            return null; // 已经是损坏的黄铜砧，再次损坏则损毁
        }
    }


    @Override
    protected void falling(FallingBlockEntity fallingEntity) {
        fallingEntity.setHurtsEntities(FALL_DAMAGE_PER_DISTANCE, FALL_DAMAGE_MAX);
    }
}
