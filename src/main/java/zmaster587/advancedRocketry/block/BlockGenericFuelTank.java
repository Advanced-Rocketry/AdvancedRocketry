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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.api.IFuelTank;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

public class BlockGenericFuelTank extends Block implements IFuelTank{

	public final static EnumProperty<TankStates> TANKSTATES = EnumProperty.create("tankstates", TankStates.class);
	static final VoxelShape HOLLOW_CUBE = VoxelShapes.create(0.0,0.0,0.0,1.0,1.0,1.0);
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
		return HOLLOW_CUBE;
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		int i = world.getBlockState(currentPos.add(0,1,0)).getBlock() == this.getBlock() ? 1 : 0;
		i += world.getBlockState(currentPos.add(0,-1,0)).getBlock() == this ? 2 : 0;

		//If there is no tank below this one
		if( i == 1 ) {
			return stateIn.with(TANKSTATES, TankStates.BOTTOM);
		}
		//If there is no tank above this one
		else if( i == 2 ) {
			return stateIn.with(TANKSTATES, TankStates.TOP);
		}
		//If there is a tank above and below this one
		else {
			return stateIn.with(TANKSTATES, TankStates.MIDDLE);
		}
	}

	@Override
	public BlockState getStateAtViewpoint(BlockState state, IBlockReader world, BlockPos pos, Vector3d viewpoint) {
		int i = world.getBlockState(pos.add(0,1,0)).getBlock() == this.getBlock() ? 1 : 0;
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

	@OnlyIn(Dist.CLIENT)
	@ParametersAreNonnullByDefault
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	@ParametersAreNonnullByDefault
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		return false;
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