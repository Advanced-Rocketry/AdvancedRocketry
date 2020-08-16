package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.IFuelTank;

import java.util.Locale;

public class BlockFuelTank extends Block implements IFuelTank{

	public final static EnumProperty<TankStates> TANKSTATES = EnumProperty.create("tankstates", TankStates.class);

	public BlockFuelTank(Properties mat) {
		super(mat);
		this.setDefaultState(this.stateContainer.getBaseState().with(TANKSTATES, TankStates.MIDDLE));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(TANKSTATES);
	}
	
	/*@Override
	public void onBlockAdded(World world, BlockPos pos, BlockState state) {


		int i = world.getBlockState(pos.add(0,1,0)).getBlock() == this ? 1 : 0;
		i += world.getBlockState(pos.add(0,-1,0)).getBlock() == this ? 2 : 0;

		//If there is no tank below this one
		if( i == 1 ) {
			world.setBlockState(pos, this.getDefaultState().with(TANKSTATES, TankStates.BOTTOM),2);
			((TileModelRender)world.getTileEntity(pos)).setType(TileModelRender.models.TANKEND);
		}
		//If there is no tank above this one
		else if( i == 2 ) {
			world.setBlockState(pos, this.getDefaultState().with(TANKSTATES, TankStates.TOP),2);
			((TileModelRender)world.getTileEntity(pos)).setType(TileModelRender.models.TANKTOP);
		}
		//If there is a tank above and below this one
		else {
			world.setBlockState(pos, this.getDefaultState().with(TANKSTATES, TankStates.MIDDLE),2);
			((TileModelRender)world.getTileEntity(pos)).setType(TileModelRender.models.TANKMIDDLE);
		}
	}*/

	
	@Override
	public BlockState getStateAtViewpoint(BlockState state, IBlockReader world, BlockPos pos, Vector3d viewpoint) {
		int i = world.getBlockState(pos.add(0,1,0)).getBlock() == this ? 1 : 0;
		i += world.getBlockState(pos.add(0,-1,0)).getBlock() == this ? 2 : 0;

		//If there is no tank below this one
		if( i == 1 ) {
			return state.with(TANKSTATES, TankStates.BOTTOM);
		}
		//If there is no tank above this one
		else if( i == 2 ) {
			return state.with(TANKSTATES, TankStates.TOP);
		}
		//If there is a tank above and below this one
		else {
			return state.with(TANKSTATES, TankStates.MIDDLE);
		}
	}

	@Override
	public int getMaxFill(World world, BlockPos pos , BlockState state) {
		return 1000;
	}

	public enum TankStates implements IStringSerializable {
		TOP,
		BOTTOM,
		MIDDLE;

		@Override
		public String func_176610_l() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}
}
