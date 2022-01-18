package zmaster587.advancedRocketry.item.tools;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.tags.ItemTags;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

public class ItemJackhammer extends ToolItem {
	
	private static final Set<Block> items = Sets.newHashSet(Blocks.COBBLESTONE, Blocks.STONE_BRICK_SLAB, Blocks.STONE_SLAB, Blocks.STONE, Blocks.SANDSTONE, Blocks.MOSSY_COBBLESTONE, Blocks.IRON_ORE, Blocks.IRON_BLOCK, Blocks.COAL_ORE, Blocks.GOLD_BLOCK, Blocks.GOLD_BLOCK, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, Blocks.ICE, Blocks.NETHERRACK, Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK, Blocks.REDSTONE_ORE, Blocks.RAIL, Blocks.DETECTOR_RAIL, Blocks.ACTIVATOR_RAIL);
	
	public ItemJackhammer(IItemTier toolMaterial, Item.Properties properties) {
		super(1, 1, toolMaterial, items, properties);
	}
	
	@Override
	public boolean getIsRepairable(@Nonnull ItemStack stackMe, @Nonnull ItemStack stackItem) {
		return ItemTags.getCollection().getOwningTags(stackItem.getItem()).stream().anyMatch(value -> value.getPath().equals("rodTitanium"));
	}
    
	@Override
	@ParametersAreNonnullByDefault
    public boolean canHarvestBlock(BlockState blockIn)
    {
            return true;
    }
}
