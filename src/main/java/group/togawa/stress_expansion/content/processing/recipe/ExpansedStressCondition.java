package group.togawa.stress_expansion.content.processing.recipe;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * 应力膨胀中新加入的网络应力条件，用于限制网络应力的总量和转速。
 * 每个条件都有一个最大网络应力总量和最大转速，既可以用来定义某种配方需要的处理等级，也可以作为应力传导组件的限制数值。
 * TODO: 给这几个等级想一套名字
 */
public enum ExpansedStressCondition implements StringRepresentable {
    NONE(0, 0),
    L1(32, 8),
    L2(128, 16),
    L3(2048, 32),
    L4(16384, 64),
    L5(65536, 128),
    L6(262144, 128),
    L7(1048576, 256),
    L8(4194304, 256),
    L9(16777216, 256),
    L10(67108864, 512);

    private int maxTotalStress; // 最大网络应力总量
    private int maxRPM; // 最大转速

    public static final Codec<ExpansedStressCondition> CODEC = StringRepresentable
            .fromEnum(ExpansedStressCondition::values);
    public static final StreamCodec<ByteBuf, ExpansedStressCondition> STREAM_CODEC = CatnipStreamCodecBuilders
            .ofEnum(ExpansedStressCondition.class);

    ExpansedStressCondition(int maxTotalStress, int maxRPM) {
        this.maxTotalStress = maxTotalStress;
        this.maxRPM = maxRPM;
    }

    @Override
    public @NotNull String getSerializedName() {
        return Lang.asId(name());
    }

    public String getTranslationKey() {
        return "recipe.stress_requirement." + getSerializedName();
    }

    public int getMaxTotalStress() {
        return maxTotalStress;
    }

    public int getMaxRPM() {
        return maxRPM;
    }

}
