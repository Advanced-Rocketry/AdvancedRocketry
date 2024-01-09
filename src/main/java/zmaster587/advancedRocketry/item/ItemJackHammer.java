package zmaster587.advancedRocketry.item;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.api.MaterialGeode;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ItemJackHammer extends ItemTool {
	
	private static final Set<Block> items = Sets.newHashSet(Blocks.COBBLESTONE, Blocks.DOUBLE_STONE_SLAB, Blocks.STONE_SLAB, Blocks.STONE, Blocks.SANDSTONE, Blocks.MOSSY_COBBLESTONE, Blocks.IRON_ORE, Blocks.IRON_BLOCK, Blocks.COAL_ORE, Blocks.GOLD_BLOCK, Blocks.GOLD_BLOCK, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, Blocks.ICE, Blocks.NETHERRACK, Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK, Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.ACTIVATOR_RAIL);
	
	public ItemJackHammer(ToolMaterial toolMaterial) {
		super(toolMaterial, items);
		this.efficiency = 50f;
	}
	
	@Override

	public boolean getIsRepairable(@NotNull ItemStack stackMe, ItemStack stackItem) {
		return OreDictionary.itemMatches(OreDictionary.getOres("stickTitanium").get(0), stackItem, false);//super.getIsRepairable(p_82789_1_, p_82789_2_);
	}


    public float getDestroySpeed(@NotNull ItemStack stack, IBlockState state) {
		return state.getMaterial() == Material.IRON ||
				state.getMaterial() == Material.ROCK ||
				state.getMaterial() == MaterialGeode.geode  ?
				this.efficiency : super.getDestroySpeed(stack, state);
    }
    
    public boolean canHarvestBlock(IBlockState blockIn)
    {
            return true;
    }
}
