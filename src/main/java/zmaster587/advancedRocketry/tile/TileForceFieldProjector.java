package zmaster587.advancedRocketry.tile;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.libVulpes.block.BlockFullyRotatable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileForceFieldProjector extends TileEntity{

	private short extensionRange;
	private static short MAX_RANGE = 32;

	public TileForceFieldProjector() {
		extensionRange = 0;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	public void destroyField(ForgeDirection facing) {
		while(extensionRange > 0) {
			int newX = xCoord + facing.offsetX*extensionRange;
			int newY = yCoord + facing.offsetY*extensionRange;
			int newZ = zCoord + facing.offsetZ*extensionRange;

			if(worldObj.getBlock(newX, newY, newZ) == AdvancedRocketryBlocks.blockForceField)
				worldObj.setBlockToAir(newX, newY, newZ);
			extensionRange--;
		}
	}

	@Override
	public void updateEntity() {

		if(worldObj.getTotalWorldTime() % 5 == 0) {
			if(worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
				if(extensionRange < MAX_RANGE) {
					if(extensionRange == 0)
						extensionRange = 1;

					if(worldObj.getBlock(xCoord, yCoord, zCoord) == AdvancedRocketryBlocks.blockForceFieldProjector) {
						ForgeDirection facing = BlockFullyRotatable.getFront(getBlockMetadata());
						
						int newX = xCoord + facing.offsetX*extensionRange;
						int newY = yCoord + facing.offsetY*extensionRange;
						int newZ = zCoord + facing.offsetZ*extensionRange;
						
						if(worldObj.getBlock(newX, newY, newZ).isReplaceable(worldObj, newX, newY, newZ) ) {
							worldObj.setBlock(newX, newY, newZ, AdvancedRocketryBlocks.blockForceField);
							extensionRange++;
						} else if(worldObj.getBlock(newX, newY, newZ) == AdvancedRocketryBlocks.blockForceField) {
							extensionRange++;
						}
					}
				}
			}
			else if(extensionRange > 0) {

				if(worldObj.getBlock(xCoord, yCoord, zCoord) == AdvancedRocketryBlocks.blockForceFieldProjector) {
					ForgeDirection facing = BlockFullyRotatable.getFront(getBlockMetadata());
					
					int newX = xCoord + facing.offsetX*extensionRange;
					int newY = yCoord + facing.offsetY*extensionRange;
					int newZ = zCoord + facing.offsetZ*extensionRange;

					if(worldObj.getBlock(newX, newY, newZ) == AdvancedRocketryBlocks.blockForceField)
						worldObj.setBlockToAir(newX, newY, newZ);
					extensionRange--;
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setShort("ext", extensionRange);
		super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		extensionRange = nbt.getShort("ext");
		super.readFromNBT(nbt);
	}

}
