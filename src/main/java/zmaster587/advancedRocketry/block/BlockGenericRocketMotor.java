package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.api.IRocketEngine;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.libVulpes.block.BlockFullyRotatable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockGenericRocketMotor extends BlockFullyRotatable implements IRocketEngine {

	static final VoxelShape HOLLOW_CUBE = VoxelShapes.create(0.0,0.0,0.0,1.0,1.0,1.0);
	private int thrust;
	private int fuelConsumption;
	private FuelType type;

	public BlockGenericRocketMotor(int thrust, int fuel, FuelType type, Properties mat) {
		super(mat);	
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.DOWN));

		this.thrust = thrust;
		this.fuelConsumption = fuel;
		this.type = type;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlockState(pos, state.with(FACING,Direction.DOWN), 2);
	}
	
	@Override
	public int getThrust(World world, BlockPos pos) {
		return thrust;
	}

	@Override
	public int getFuelConsumptionRate(World world, int x, int y, int z) {
		return fuelConsumption;
	}

	@Override
	public FuelType getFuelType(World world, BlockPos pos) {
		return type;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, Direction.DOWN);
	}
	
	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return HOLLOW_CUBE;
	}

	@OnlyIn(Dist.CLIENT)
	@ParametersAreNonnullByDefault
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 1.0F;
	}
}
