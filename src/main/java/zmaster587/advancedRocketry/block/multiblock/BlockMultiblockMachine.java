package zmaster587.advancedRocketry.block.multiblock;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.block.BlockTile;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * hosts a multiblock machine master tile
 *
 */
public class BlockMultiblockMachine extends BlockTile {

	public BlockMultiblockMachine(Class<? extends TileMultiBlock> tileClass,
			int guiId) {
		super(tileClass, guiId);
	}

	@Override
	public void onBlockPreDestroy(World world, int x,
			int y, int z, int meta) {
		super.onBlockPreDestroy(world, x, y, z,	meta);

		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileMultiBlock) {
			TileMultiBlock tileMulti = (TileMultiBlock)tile;
			if(tileMulti.isComplete())
				tileMulti.deconstructMultiBlock(world, x, y, z, false);
		}
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side) {
		
		ForgeDirection direction = ForgeDirection.getOrientation(side);
		
		TileEntity tile = access.getTileEntity(x - direction.offsetX, y- direction.offsetY, z - direction.offsetZ);
		if(tile instanceof TileMultiBlock) {
			return !((TileMultiBlock)tile).shouldHideBlock(tile.getWorldObj(), x - direction.offsetX, y- direction.offsetY, z - direction.offsetZ, access.getBlock(x - direction.offsetX, y- direction.offsetY, z - direction.offsetZ)) || !((TileMultiBlock)tile).canRender();
		}
		return super.shouldSideBeRendered(access, x, y, z, side);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileMultiBlock) {
			TileMultiBlock tileMulti = (TileMultiBlock)tile;
			if(tileMulti.isComplete() && !world.isRemote) {
				player.openGui(AdvancedRocketry.instance, guiId, world, x, y, z);
			}
			else
				return tileMulti.attemptCompleteStructure();
		}
		return true;
	}
}
