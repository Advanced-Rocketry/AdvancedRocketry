package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
<<<<<<< HEAD
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
=======
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
>>>>>>> origin/feature/nuclearthermalrockets
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import zmaster587.advancedRocketry.tile.atmosphere.TileAtmosphereDetector;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.IModularInventory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

public class BlockRedstoneEmitter extends Block {
	
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");
	
	public BlockRedstoneEmitter(Properties material,String activeIconName) {
		super(material);
		this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, false));
	}
	
	@Override
<<<<<<< HEAD
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		
		builder.add(POWERED);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
=======
	@Nonnull
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, POWERED);
	}
	
	@Override
	@Nonnull
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(POWERED, (meta & 8) == 8);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(POWERED) ? 8 : 0;
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
>>>>>>> origin/feature/nuclearthermalrockets
		return true;
	}
	
	public void setState(World world, BlockState bstate, BlockPos pos, boolean state) {
		world.setBlockState(pos, bstate.with(POWERED, state));
	}
	
	public boolean getState(World world, BlockState bstate, BlockPos pos) {
		return bstate.get(POWERED);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if(!world.isRemote)
		{
			TileEntity te = world.getTileEntity(pos);
			if(te != null)
				NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, buf -> {buf.writeInt(((IModularInventory)te).getModularInvType().ordinal()); buf.writeBlockPos(pos); });
		}
		return ActionResultType.SUCCESS;
	}
	
	@Override
<<<<<<< HEAD
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
=======
	@ParametersAreNullableByDefault
	public TileEntity createTileEntity(World world, IBlockState state) {
>>>>>>> origin/feature/nuclearthermalrockets
		return new TileAtmosphereDetector();
	}
	
	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return blockState.get(POWERED) ? 15 : 0;
	}
	
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return blockState.get(POWERED) ? 15 : 0;
	}
	
	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

}
