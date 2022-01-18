package zmaster587.advancedRocketry.block.plant;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BlockLightwoodLeaves extends LeavesBlock {

	public BlockLightwoodLeaves(AbstractBlock.Properties properties) {
		super(properties);
		// light value
		properties.setLightLevel(value -> 8);
		this.setDefaultState(this.stateContainer.getBaseState().with(PERSISTENT, false).with(DISTANCE, 0));
	}
	
	protected static final String[] names = {"blueLeaf"};
	protected static final String[][] textures = new String[][] {{"leaves_oak"}, {"leaves_oak_opaque"}};
    
    public int quantityDropped(Random p_149745_1_)
    {
        return p_149745_1_.nextInt(100) == 0 ? 1 : 0;
    }
    
    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
    	return 50;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
    	return 50;
    }

    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(AdvancedRocketryBlocks.blockLightwoodSapling);
    }
    
    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public List<ItemStack> onSheared(@Nullable PlayerEntity player, ItemStack item, World world, BlockPos pos, int fortune) {
    	List<ItemStack> stackList = new LinkedList<>();
    	stackList.add(new ItemStack(this, 1));
    	return stackList;
    }
}
