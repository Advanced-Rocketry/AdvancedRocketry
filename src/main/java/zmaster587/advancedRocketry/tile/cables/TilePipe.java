package zmaster587.advancedRocketry.tile.cables;

import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TilePipe extends TileEntity {

	int networkID;
	boolean initialized, destroyed;

	boolean connectedSides[];

	public TilePipe() {
		initialized = false;
		destroyed = false;
		connectedSides = new boolean[6];
	}

	
	public void initialize(int id) {
		networkID = id;
		initialized = true;
		getNetworkHandler().getNetwork(id).addPipeToNetwork(this);
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		removePipeFromSystem();
		
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		removePipeFromSystem();
	}
	
	public void removePipeFromSystem() {
		if(!isInitialized())
			return;
		
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if(tile != null)
				getNetworkHandler().removeFromAllTypes(this, tile);
		}
		
		//Fix NPE on chunk unload
		if(getNetworkHandler().getNetwork(networkID) != null) {
			getNetworkHandler().getNetwork(networkID).removePipeFromNetwork(this);
			//Recreate the network until a clean way to tranverse nets in unloaded chunk can be found
			getNetworkHandler().removeNetworkByID(networkID);
		}
	}
	
	public void markForUpdate() {
		if(!worldObj.isRemote) {
			worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 20);
		}
	}

	public void onPlaced() {

		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);


			if(tile != null) {
				if(tile instanceof TilePipe && tile.getClass() == this.getClass()) {
					TilePipe pipe = (TilePipe)tile;
					if(this.destroyed)
						continue;

					if(isInitialized() && pipe.isInitialized() && pipe.getNetworkID() != networkID)
						getNetworkHandler().mergeNetworks(networkID,  pipe.getNetworkID());
					else if(!isInitialized() && pipe.isInitialized()) {
						initialize(pipe.getNetworkID());
					}
				}
			}
		}


		if(!isInitialized()) {
			initialize(getNetworkHandler().getNewNetworkID());
		}

		linkSystems();
	}

	public void linkSystems() {
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

			if(tile != null ) {
				attemptLink(dir, tile);
			}
		}
	}

	protected void attemptLink(ForgeDirection dir, TileEntity tile) {
		//If the pipe can inject or extract, add to the cache
		//if(!(tile instanceof IFluidHandler))
			//return;
		if(canExtract(dir, tile) && (worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) > 0 || worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))) {
			if(worldObj.isRemote)
				connectedSides[dir.ordinal()]=true;
			else {
				getNetworkHandler().removeFromAllTypes(this, tile);
				getNetworkHandler().addSource(this,tile,dir);
			}
		}

		if(canInject(dir, tile) && (worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) == 0 && !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))) {
			if(worldObj.isRemote)
				connectedSides[dir.ordinal()]=true;
			else {
				getNetworkHandler().removeFromAllTypes(this, tile);
				getNetworkHandler().addSink(this, tile,dir);
			}
		}
	}
	
	public int getNetworkID() { return networkID; }

	public boolean isInitialized() { return initialized && getNetworkHandler().doesNetworkExist(networkID); }

	public void onNeighborTileChange(int x, int y, int z) {

		//if(worldObj.isRemote)
		//return;

		TileEntity tile = worldObj.getTileEntity(x, y, z);

		if(!worldObj.isRemote && !getNetworkHandler().doesNetworkExist(networkID)) {
			initialized = false;
		}

		if(tile != null) {

			//If two networks touch, merge them
			if(tile instanceof TilePipe && tile.getClass() == this.getClass()) {

				TilePipe pipe = ((TilePipe) tile);

				if(worldObj.isRemote) {
					ForgeDirection dir = ForgeDirection.UNKNOWN;
					for(ForgeDirection dir2 : ForgeDirection.VALID_DIRECTIONS) {
						if(dir2.offsetX == x - xCoord && dir2.offsetY == y - yCoord && dir2.offsetZ == z - zCoord)
							dir = dir2;
					}
					connectedSides[dir.ordinal()] = true;
				}
				else {

					if(this.destroyed)
						return;

					if(pipe.isInitialized()) {
						if(!isInitialized()) {
							initialize(pipe.getNetworkID());
							linkSystems();
							markDirty();

						} else if(pipe.getNetworkID() != networkID)
							mergeNetworks(pipe.getNetworkID(), networkID);
					}
					else if(isInitialized()) {
						pipe.initialize(networkID);
					}
					else {
						onPlaced();
						markDirty();
					}
				}
			}
			else {
				if(!worldObj.isRemote && !isInitialized()) {
					networkID = getNetworkHandler().getNewNetworkID();
					initialized = true;
				}

				ForgeDirection dir = ForgeDirection.UNKNOWN;
				for(ForgeDirection dir2 : ForgeDirection.VALID_DIRECTIONS) {
					if(dir2.offsetX == x - xCoord && dir2.offsetY == y - yCoord && dir2.offsetZ == z - zCoord)
						dir = dir2;
				}

				//If the pipe can inject or extract, add to the cache
				attemptLink(dir, tile);
			}
		}
		else if(worldObj.isRemote) {
			
			ForgeDirection dir = ForgeDirection.UNKNOWN;
			for(ForgeDirection dir2 : ForgeDirection.VALID_DIRECTIONS) {
				if(dir2.offsetX == x - xCoord && dir2.offsetY == y - yCoord && dir2.offsetZ == z - zCoord)
					dir = dir2;
			}
			connectedSides[dir.ordinal()] = false;
		}
	}

	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.liquidNetwork;
	}

	public boolean canConnect(int side) {
		return connectedSides[side];
	}

	public boolean canExtract(ForgeDirection dir, TileEntity e) {
		return false;
	}

	public boolean canInject(ForgeDirection dir, TileEntity e) {
		return false;
	}

	public void mergeNetworks(int a, int b) {
		networkID = getNetworkHandler().mergeNetworks(a, b);
		this.markDirty();
	}

	@Override
	public String toString() {
		return "ID: " + networkID + "   " + getNetworkHandler().toString(networkID);
	}

	public void setDestroyed() {
		destroyed = true;
	}

	public void setInvalid() {
		initialized = false;
		//markDirty();
	}
}
