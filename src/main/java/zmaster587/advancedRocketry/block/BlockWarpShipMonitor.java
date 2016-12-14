package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.block.BlockTile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockWarpShipMonitor extends BlockTile {

	public BlockWarpShipMonitor(Class<? extends TileEntity> tileClass, int guiId) {
		super(tileClass, guiId);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLivingBase entity, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, entity, stack);
		ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(x, z);
		
		if(object instanceof SpaceObject) {
			((SpaceObject)object).setForwardDirection(getFront(world.getBlockMetadata(x, y, z)).getOpposite());
		}
	}
}
