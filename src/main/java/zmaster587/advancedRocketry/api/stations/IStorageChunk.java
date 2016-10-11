package zmaster587.advancedRocketry.api.stations;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IStorageChunk {
	/**
	 * Pastes the contents of the storage chunk into the world at the given coordinates
	 * @param world
	 * @param xCoord
	 * @param yCoord
	 * @param zCoord
	 */
	public void pasteInWorld(World world, int xCoord, int yCoord ,int zCoord);
	
	public int getSizeX();
	public int getSizeY();
	public int getSizeZ();

	public List<TileEntity> getTileEntityList();

	public void rotateBy(EnumFacing dir);
}
