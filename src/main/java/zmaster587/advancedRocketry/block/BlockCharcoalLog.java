package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Random;

public class BlockCharcoalLog extends BlockLog {


	public BlockCharcoalLog() {
		super();
		this.setDefaultState(this.blockState.getBaseState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
	}

	@Override
	@NotNull
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, LOG_AXIS);
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 0;
	}
	
	@Override
	@NotNull
	public ItemStack getItem(World worldIn, BlockPos pos, @Nullable IBlockState state) {
        Item item = Item.getItemFromBlock(this);
        return item.equals(Items.AIR) ? ItemStack.EMPTY : new ItemStack(item, 1, 0);
	}
	
	
	
    @NotNull
	public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState();

        switch (meta & 12)
        {
            case 0:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
                break;
            case 4:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
                break;
            case 8:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
                break;
            default:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
        }

        return iblockstate;
    }


	public int getMetaFromState(IBlockState state)
	{
		int i = 0;
		switch (state.getValue(LOG_AXIS))
		{
		case X:
			i |= 4;
			break;
		case Z:
			i |= 8;
			break;
		case NONE:
			i |= 12;
		}

		return i;
	}

	@Override
	@NotNull
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.COAL;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 1;
	}

	public int quantityDroppedWithBonus(int i, @NotNull Random rand)
	{
		return this.quantityDropped(rand) + (i > 0 ? rand.nextInt(i) : 0);
	}
}
