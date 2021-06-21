package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import zmaster587.advancedRocketry.tile.station.TileDockingPort;
import zmaster587.advancedRocketry.tile.station.TileLandingPad;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.block.BlockFullyRotatable;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.IModularInventory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

public class BlockStationModuleDockingPort extends BlockFullyRotatable {

	public BlockStationModuleDockingPort(Properties par2Material) {
		super(par2Material);
	}
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
<<<<<<< HEAD
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
=======
	@ParametersAreNullableByDefault
	public TileEntity createTileEntity(World world, IBlockState state) {
>>>>>>> origin/feature/nuclearthermalrockets
		return new TileDockingPort();
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
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state,
			LivingEntity placer, ItemStack stack) {
=======
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
			EntityLivingBase placer, @Nonnull ItemStack stack) {
>>>>>>> origin/feature/nuclearthermalrockets
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileDockingPort) {
			((TileDockingPort) tile).registerTileWithStation(world, pos);
		}
	}
	
	@Override
<<<<<<< HEAD
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
=======
	@ParametersAreNonnullByDefault
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
>>>>>>> origin/feature/nuclearthermalrockets
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileLandingPad) {
			((TileLandingPad) tile).unregisterTileWithStation(world, pos);
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

}
