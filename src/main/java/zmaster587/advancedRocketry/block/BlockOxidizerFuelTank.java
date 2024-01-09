package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.IFuelTank;

import org.jetbrains.annotations.NotNull;
import java.util.Locale;

public class BlockOxidizerFuelTank extends Block implements IFuelTank{

	public final static PropertyEnum<TankStates> TANKSTATES = PropertyEnum.create("tankstates", TankStates.class);

	public BlockOxidizerFuelTank(Material mat) {
		super(mat);
		this.setDefaultState(this.getDefaultState().withProperty(TANKSTATES, TankStates.MIDDLE));
	}

	@Override
	@NotNull
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(TANKSTATES, TankStates.values()[meta]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TANKSTATES).ordinal();
	}
	
	@Override
	@NotNull
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TANKSTATES);
	}

	@Override
	@NotNull
	public IBlockState getActualState(@NotNull IBlockState state, IBlockAccess world,
			BlockPos pos) {
		int i = world.getBlockState(pos.add(0,1,0)).getBlock() == this ? 1 : 0;
		i += world.getBlockState(pos.add(0,-1,0)).getBlock() == this ? 2 : 0;

		//If there is no tank below this one
		if( i == 1 ) {
			return state.withProperty(TANKSTATES, TankStates.BOTTOM);
		}
		//If there is no tank above this one
		else if( i == 2 ) {
			return state.withProperty(TANKSTATES, TankStates.TOP);
		}
		//If there is a tank above and below this one
		else {
			return state.withProperty(TANKSTATES, TankStates.MIDDLE);
		}
	}

	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public int getMaxFill(World world, BlockPos pos , IBlockState state) {
		return 1000;
	}

	public enum TankStates implements IStringSerializable {
		TOP,
		BOTTOM,
		MIDDLE;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ENGLISH);
		}

	}
}
