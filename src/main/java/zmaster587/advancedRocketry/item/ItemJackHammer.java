package zmaster587.advancedRocketry.item;

import java.util.Set;

import zmaster587.advancedRocketry.api.MaterialGeode;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.oredict.OreDictionary;

public class ItemJackHammer extends ItemTool {
	
	private static final Set items = Sets.newHashSet(new Block[] {Blocks.cobblestone, Blocks.double_stone_slab, Blocks.stone_slab, Blocks.stone, Blocks.sandstone, Blocks.mossy_cobblestone, Blocks.iron_ore, Blocks.iron_block, Blocks.coal_ore, Blocks.gold_block, Blocks.gold_ore, Blocks.diamond_ore, Blocks.diamond_block, Blocks.ice, Blocks.netherrack, Blocks.lapis_ore, Blocks.lapis_block, Blocks.redstone_ore, Blocks.lit_redstone_ore, Blocks.rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.activator_rail});
	
	public ItemJackHammer(ToolMaterial toolMaterial) {
		super(5f,toolMaterial,items);
		
		efficiencyOnProperMaterial = 50f;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack stackMe, ItemStack stackItem) {
		return OreDictionary.itemMatches(OreDictionary.getOres("stickTitanium").get(0), stackItem, false);//super.getIsRepairable(p_82789_1_, p_82789_2_);
	}
	
    public float func_150893_a(ItemStack stack, Block block)
    {
        return  block.getMaterial() == Material.iron || block.getMaterial() == Material.rock || block.getMaterial() == MaterialGeode.geode  ? this.efficiencyOnProperMaterial : super.func_150893_a(stack, block);
    }
	
    @Override
    public boolean canHarvestBlock(Block par1Block, ItemStack itemStack) {
    	return true;
    }
}
