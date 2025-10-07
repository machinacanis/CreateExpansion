package group.togawa.stress_expansion.content.processing.recipe;

import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;

import net.createmod.catnip.lang.Lang;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * 基于机械动力原本的加热条件进行扩展
 * TODO：完善其对应的可视化条件显示以及对各个热源方块的测试方法，不过这些要等到对应的这些东西实现之后再做
 */
public enum ExpansedHeatCondition implements StringRepresentable {
    ABSOLUTEZERO(0x1C43E8), // 绝对零度程度需求，约-273℃
    HIGHFREEZED(0x1C43E8), // 高降温程度需求，约-196℃
    MIDFREEZED(0x1C43E8), // 中等降温程度需求，约-50℃
    LOWFREEZED(0x1C43E8), // 低降温程度需求，约-10℃
    ZERO(0xffffff), // 零度降温程度需求，约0℃
    NONE(0xffffff), // 室温程度需求，约20℃
    LOWHEATED(0xE88300), // 低加热程度需求，约300℃
    MIDHEATED(0x5C93E8), // 中等加热程度需求，约800℃
    HIGHHEATED(0x2C63E8), // 高加热程度需求，约1500℃
    SUPERHEATED(0x1C43E8), // 超高加热程度需求，约2000℃
    WILDHEATED(0x1C43E8), // 超超高加热程度需求，约3000℃
    WTFHEATED(0x1C43E8) // 超超超高加热程度需求，约5000℃
    ;

    private int color;

    public static final Codec<ExpansedHeatCondition> CODEC = StringRepresentable
            .fromEnum(ExpansedHeatCondition::values);
    public static final StreamCodec<ByteBuf, ExpansedHeatCondition> STREAM_CODEC = CatnipStreamCodecBuilders
            .ofEnum(ExpansedHeatCondition.class);

    ExpansedHeatCondition(int color) {
        this.color = color;
    }

    // public boolean testBlazeBurner(BlazeBurnerBlock.HeatLevel level) {
    // if (this == SUPERHEATED)
    // return level == HeatLevel.SEETHING;
    // if (this == HEATED)
    // return level != HeatLevel.NONE && level != HeatLevel.SMOULDERING;
    // return true;
    // }
    //
    // public BlazeBurnerBlock.HeatLevel visualizeAsBlazeBurner() {
    // if (this == SUPERHEATED)
    // return HeatLevel.SEETHING;
    // if (this == HEATED)
    // return HeatLevel.KINDLED;
    // return HeatLevel.NONE;
    // }

    @Override
    public @NotNull String getSerializedName() {
        return Lang.asId(name());
    }

    public String getTranslationKey() {
        return "recipe.heat_requirement." + getSerializedName();
    }

    public int getColor() {
        return color;
    }
}
