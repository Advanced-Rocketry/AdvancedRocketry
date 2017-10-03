package zmaster587.advancedRocketry.block.multiblock;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.tile.hatch.TileDataBus;
import zmaster587.advancedRocketry.tile.hatch.TileSatelliteHatch;
import zmaster587.advancedRocketry.tile.infrastructure.TileGuidanceComputerHatch;
import zmaster587.advancedRocketry.tile.infrastructure.TileRocketFluidLoader;
import zmaster587.advancedRocketry.tile.infrastructure.TileRocketFluidUnloader;
import zmaster587.advancedRocketry.tile.infrastructure.TileRocketLoader;
import zmaster587.advancedRocketry.tile.infrastructure.TileRocketUnloader;
import zmaster587.libVulpes.block.multiblock.BlockHatch;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;

public class BlockARHatch extends BlockHatch {

	public BlockARHatch(Material material) {
		super(material);
	}
	
	@Override
	public void getSubBlocks(CreativeTabs tab,
			NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
		list.add(new ItemStack(this, 1, 2));
		list.add(new ItemStack(this, 1, 3));
		list.add(new ItemStack(this, 1, 4));
		list.add(new ItemStack(this, 1, 5));
		list.add(new ItemStack(this, 1, 6));
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState,
			IBlockAccess blockAccess, BlockPos pos, EnumFacing direction) {


		boolean isPointer = blockAccess.getTileEntity(pos.offset(direction.getOpposite())) instanceof TilePointer;
		
		if(isPointer || blockState.getValue(VARIANT) < 2)
			return super.shouldSideBeRendered(blockState, blockAccess, pos, direction);
		return true;

	}
	
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess,
			BlockPos pos, EnumFacing side) {
		if(blockAccess.getTileEntity(pos) instanceof TilePointer && !((TilePointer)blockAccess.getTileEntity(pos)).allowRedstoneOutputOnSide(side))
			return 0;
		
		return blockState.getValue(VARIANT) >= 2 ? 15 : 0;
	}
	
	public void setRedstoneState(World world, IBlockState bstate , BlockPos pos, boolean state) {
		if(bstate.getBlock() == this) {
			if(state && (bstate.getValue(VARIANT) & 8) == 0) {
				world.setBlockState(pos, bstate.withProperty(VARIANT, bstate.getValue(VARIANT) | 8));
				world.notifyBlockUpdate(pos, bstate,  bstate, 3);
			}
			else if(!state && (bstate.getValue(VARIANT) & 8) != 0) {
				world.setBlockState(pos, bstate.withProperty(VARIANT, bstate.getValue(VARIANT) & 7));
				world.notifyBlockUpdate(pos, bstate,  bstate, 3);
			}
		}
	}
	
	@Override
	public boolean canProvidePower(IBlockState state) {
		return state.getValue(VARIANT) >= 10;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		int metadata = state.getValue(VARIANT);
		
		//TODO: multiple sized Hatches
		if((metadata & 7) == 0)
			return new TileDataBus(4);
		else if((metadata & 7) == 1)
			return new TileSatelliteHatch(1);	
		else if((metadata & 7) == 2)
			return new TileRocketUnloader(4);
		else if((metadata & 7) == 3)
			return new TileRocketLoader(4);
		else if((metadata & 7) == 4)
			return new TileRocketFluidUnloader();
		else if((metadata & 7) == 5)
			return new TileRocketFluidLoader();
		else if((metadata & 7) == 6)
			return new TileGuidanceComputerHatch();
		
		return null;
	}
}
