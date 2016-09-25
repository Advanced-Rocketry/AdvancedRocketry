package zmaster587.advancedRocketry.block;

import java.util.Random;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockCharcoalLog extends BlockLog {

	
	public BlockCharcoalLog() {
		super();
	}
	
	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 0;
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.COAL;
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return 1;
	}
	
    public int quantityDroppedWithBonus(int i, Random rand)
    {
        return this.quantityDropped(rand) + (i > 0 ? rand.nextInt(i) : 0);
    }
}
