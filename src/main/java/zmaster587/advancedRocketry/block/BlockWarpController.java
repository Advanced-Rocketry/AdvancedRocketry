package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.inventory.GuiHandler;

import javax.annotation.Nonnull;

public class BlockWarpController extends BlockTile {

	public BlockWarpController(Properties properties, GuiHandler.guiId guiId) {
		super(properties, guiId);
	}

	@Override
<<<<<<< HEAD
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		
		
		ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
=======
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
			EntityLivingBase placer, @Nonnull ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		
		ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
>>>>>>> origin/feature/nuclearthermalrockets
		
		if(spaceObject instanceof SpaceStationObject) {
			((SpaceStationObject)spaceObject).setForwardDirection(getFront(state).getOpposite());
		}
	}
}
