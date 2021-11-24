package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.IFuelTank;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

public class BlockGenericFuelTank extends Block implements IFuelTank{

	public final static EnumProperty<TankStates> TANKSTATES = EnumProperty.create("tankstates", TankStates.class);
	private int fuelCapacity;
	private FuelType type;

	public BlockGenericFuelTank(int fuel, FuelType type, Properties machineLineProperties) {
		super(machineLineProperties);
		this.setDefaultState(this.stateContainer.getBaseState().with(TANKSTATES, TankStates.MIDDLE));

		this.fuelCapacity = fuel;
		this.type = type;
	}

	@Override
	@ParametersAreNonnullByDefault
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(TANKSTATES);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {

		World world = context.getWorld();
		BlockPos pos = context.getPos();


		int i = world.getBlockState(pos.add(0,1,0)).getBlock() == this ? 1 : 0;
		i += world.getBlockState(pos.add(0,-1,0)).getBlock() == this ? 2 : 0;

		//If there is no tank below this one
		if( i == 1 ) {
			return this.getDefaultState().with(TANKSTATES, TankStates.BOTTOM);
		}
		//If there is no tank above this one
		else if( i == 2 ) {
			return this.getDefaultState().with(TANKSTATES, TankStates.TOP);
		}
		//If there is a tank above and below this one
		else {
			return this.getDefaultState().with(TANKSTATES, TankStates.MIDDLE);
		}
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {

		if(!(facing == Direction.UP || facing == Direction.DOWN))
			return super.updatePostPlacement(stateIn, facing, facingState, world, currentPos, facingPos);

		if(facingState.getBlock() == this) {
			if(stateIn.get(TANKSTATES) == TankStates.TOP && facing == Direction.UP)
				return this.getDefaultState().with(TANKSTATES, TankStates.MIDDLE);
			if(stateIn.get(TANKSTATES) == TankStates.BOTTOM && facing == Direction.DOWN)
				return this.getDefaultState().with(TANKSTATES, TankStates.MIDDLE);
			return super.updatePostPlacement(stateIn, facing, facingState, world, currentPos, facingPos);

		} else {
			if(stateIn.get(TANKSTATES) == TankStates.MIDDLE && facing == Direction.UP)
				return this.getDefaultState().with(TANKSTATES, TankStates.TOP);
			if(stateIn.get(TANKSTATES) == TankStates.MIDDLE && facing == Direction.DOWN)
				return this.getDefaultState().with(TANKSTATES, TankStates.BOTTOM);

			if(stateIn.get(TANKSTATES) == TankStates.BOTTOM && facing == Direction.UP || stateIn.get(TANKSTATES) == TankStates.TOP && facing == Direction.DOWN)
				return this.getDefaultState().with(TANKSTATES, TankStates.MIDDLE);
			return stateIn;
		}
	}

	public void updateMyState(World world, BlockPos pos) {
		int i = world.getBlockState(pos.add(0,1,0)).getBlock() == this ? 1 : 0;
		i += world.getBlockState(pos.add(0,-1,0)).getBlock() == this ? 2 : 0;

		//If there is no tank below this one
		if( i == 1 ) {
			world.setBlockState(pos, this.getDefaultState().with(TANKSTATES, TankStates.BOTTOM),2);
		}
		//If there is no tank above this one
		else if( i == 2 ) {
			world.setBlockState(pos, this.getDefaultState().with(TANKSTATES, TankStates.TOP),2);
		}
		//If there is a tank above and below this one
		else {
			world.setBlockState(pos, this.getDefaultState().with(TANKSTATES, TankStates.MIDDLE),2);
		}
	}

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
		return fuelCapacity;
	}

	@Override
	public FuelType getFuelType(World world, BlockPos pos) {
		return type;
	}

	public enum TankStates implements IStringSerializable {
		TOP,
		BOTTOM,
		MIDDLE;

		@Nonnull
		@Override
		public String getString() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}
}