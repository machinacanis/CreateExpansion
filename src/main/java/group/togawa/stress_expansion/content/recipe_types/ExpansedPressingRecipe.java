package group.togawa.stress_expansion.content.recipe_types;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import group.togawa.stress_expansion.content.processing.recipe.ExpansedProcessingRecipeParams;
import group.togawa.stress_expansion.content.processing.recipe.ExpansedStandardProcessingRecipe;

import com.simibubi.create.AllBlocks;
import group.togawa.stress_expansion.AllRecipeTypes;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 应力膨胀版本的冲压配方，暂时还没有完全实现
 */
public class ExpansedPressingRecipe extends ExpansedStandardProcessingRecipe<SingleRecipeInput> // 这里把配方处理器也改成应力膨胀的
		implements IAssemblyRecipe {
	public ExpansedPressingRecipe(ExpansedProcessingRecipeParams params) {
		super(AllRecipeTypes.EXPANSED_PRESSING, params);
	}

	@Override
	public boolean matches(SingleRecipeInput inv, Level worldIn) {
		if (inv.isEmpty())
			return false;
		return ingredients.get(0)
				.test(inv.getItem(0));
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 2;
	}

	@Override
	public void addAssemblyIngredients(List<Ingredient> list) {
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Component getDescriptionForAssembly() {
		return CreateLang.translateDirect("recipe.assembly.pressing");
	}

	@Override
	public void addRequiredMachines(Set<ItemLike> list) {
		list.add(AllBlocks.MECHANICAL_PRESS.get());
	}

	@Override
	public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
		return () -> SequencedAssemblySubCategory.AssemblyPressing::new;
	}
}
