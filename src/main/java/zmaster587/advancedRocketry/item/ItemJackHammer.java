package zmaster587.advancedRocketry.item;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.tags.ItemTags;
import zmaster587.advancedRocketry.api.MaterialGeode;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

public class ItemJackHammer extends ToolItem {
	
<<<<<<< HEAD
	private static final Set items = Sets.newHashSet(new Block[] {Blocks.COBBLESTONE, Blocks.STONE_BRICK_SLAB, Blocks.STONE_SLAB, Blocks.STONE, Blocks.SANDSTONE, Blocks.MOSSY_COBBLESTONE, Blocks.IRON_ORE, Blocks.IRON_BLOCK, Blocks.COAL_ORE, Blocks.GOLD_BLOCK, Blocks.GOLD_BLOCK, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, Blocks.ICE, Blocks.NETHERRACK, Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK, Blocks.REDSTONE_ORE, Blocks.RAIL, Blocks.DETECTOR_RAIL, Blocks.ACTIVATOR_RAIL});
=======
	private static final Set<Block> items = Sets.newHashSet(Blocks.COBBLESTONE, Blocks.DOUBLE_STONE_SLAB, Blocks.STONE_SLAB, Blocks.STONE, Blocks.SANDSTONE, Blocks.MOSSY_COBBLESTONE, Blocks.IRON_ORE, Blocks.IRON_BLOCK, Blocks.COAL_ORE, Blocks.GOLD_BLOCK, Blocks.GOLD_BLOCK, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, Blocks.ICE, Blocks.NETHERRACK, Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK, Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.ACTIVATOR_RAIL);
>>>>>>> origin/feature/nuclearthermalrockets
	
	public ItemJackHammer(IItemTier toolMaterial, Item.Properties properties) {
		super(1, 1, toolMaterial, items, properties);
		
	}
	
	@Override
<<<<<<< HEAD
	public boolean getIsRepairable(ItemStack stackMe, ItemStack stackItem) {
		return ItemTags.getCollection().getOwningTags(stackItem.getItem()).stream().anyMatch(value -> value.getPath().equals("stickTitanium"));
	}
	
=======
	@ParametersAreNonnullByDefault
	public boolean getIsRepairable(@Nonnull ItemStack stackMe, ItemStack stackItem) {
		return OreDictionary.itemMatches(OreDictionary.getOres("stickTitanium").get(0), stackItem, false);//super.getIsRepairable(p_82789_1_, p_82789_2_);
	}

	@ParametersAreNonnullByDefault
    public float getStrVsBlock(@Nonnull ItemStack stack, IBlockState state)
    {
    	return  state.getMaterial() == Material.IRON || state.getMaterial() == Material.ROCK || state.getMaterial() == MaterialGeode.geode  ? this.efficiencyOnProperMaterial : super.getStrVsBlock(stack, state);
    	   
    }
>>>>>>> origin/feature/nuclearthermalrockets
    
	@Override
    public boolean canHarvestBlock(BlockState blockIn)
    {
            return true;
    }
}
