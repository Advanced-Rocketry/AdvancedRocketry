package zmaster587.advancedRocketry.tile.cables;

import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

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

		for(EnumFacing dir : EnumFacing.VALUES) {
			TileEntity tile = worldObj.getTileEntity(this.getPos().offset(dir));
			if(tile != null)
				getNetworkHandler().removeFromAllTypes(this, tile);
		}
		getNetworkHandler().getNetwork(networkID).removePipeFromNetwork(this);
		//Recreate the network until a clean way to tranverse nets in unloaded chunk can be found
		getNetworkHandler().removeNetworkByID(networkID);
	}

	@Override
	public void markDirty() {
		super.markDirty();

		if(!worldObj.isRemote) {
			worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos),  worldObj.getBlockState(pos), 3);
		}
	}

	public void onPlaced() {

		for(EnumFacing dir : EnumFacing.values()) {
			TileEntity tile = worldObj.getTileEntity(getPos().offset(dir));


			if(tile != null) {
				if(tile instanceof TilePipe) {
					TilePipe pipe = (TilePipe)tile;
					if(this.destroyed)
						continue;

					if(initialized && pipe.isInitialized() && pipe.getNetworkID() != networkID)
						getNetworkHandler().mergeNetworks(networkID,  pipe.getNetworkID());
					else if(!initialized && pipe.isInitialized()) {
						initialize(pipe.getNetworkID());
					}
				}
			}
		}


		if(!initialized) {
			initialize(getNetworkHandler().getNewNetworkID());
		}

		linkSystems();
	}

	public void linkSystems() {
		for(EnumFacing dir : EnumFacing.values()) {
			TileEntity tile = worldObj.getTileEntity(getPos().offset(dir));

			if(tile != null) {
				attemptLink(dir, tile);
			}
		}
	}

	protected void attemptLink(EnumFacing dir, TileEntity tile) {
		//If the pipe can inject or extract, add to the cache
		//if(!(tile instanceof IFluidHandler))
		//return;

		if(canExtract(dir, tile) && (worldObj.isBlockIndirectlyGettingPowered(pos) > 0 || worldObj.getStrongPower(pos) > 0)) {
			if(worldObj.isRemote)
				connectedSides[dir.ordinal()]=true;
			else {
				getNetworkHandler().removeFromAllTypes(this, tile);
				getNetworkHandler().addSource(this,tile,dir);
			}
		}

		if(canInject(dir, tile) && worldObj.isBlockIndirectlyGettingPowered(pos) == 0 && worldObj.getStrongPower(pos) == 0) {
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

	public void onNeighborTileChange(BlockPos pos) {

		//if(worldObj.isRemote)
		//return;

		TileEntity tile = worldObj.getTileEntity(pos);

		if(!worldObj.isRemote && !getNetworkHandler().doesNetworkExist(networkID)) {
			initialized = false;
		}

		if(tile != null) {

			//If two networks touch, merge them
			if(tile instanceof TilePipe) {

				TilePipe pipe = ((TilePipe) tile);

				if(worldObj.isRemote) {
					EnumFacing dir = null;
					for(EnumFacing dir2 : EnumFacing.values()) {

						if(getPos().offset(dir2).compareTo(pos) == 0)
							dir = dir2;
					}
					if(dir != null)
						connectedSides[dir.ordinal()] = true;
				}
				else {

					if(this.destroyed)
						return;

					if(pipe.isInitialized()) {
						if(!initialized) {
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
				if(!worldObj.isRemote && !initialized) {
					networkID = getNetworkHandler().getNewNetworkID();
					initialized = true;
				}

				EnumFacing dir = null;
				for(EnumFacing dir2 : EnumFacing.values()) {
					if(getPos().offset(dir2).compareTo(pos) == 0)
						dir = dir2;
				}

				//If the pipe can inject or extract, add to the cache
				if(dir != null)
					attemptLink(dir, tile);
			}
		}
		else if(worldObj.isRemote) {

			EnumFacing dir = null;
			for(EnumFacing dir2 : EnumFacing.values()) {
				if(getPos().offset(dir2).compareTo(pos) == 0)
					dir = dir2;
			}
			if(dir != null)
				connectedSides[dir.ordinal()] = false;
		}
	}

	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.liquidNetwork;
	}

	public boolean canConnect(int side) {
		return connectedSides[side];
	}

	public boolean canExtract(EnumFacing dir, TileEntity e) {
		return false;
	}

	public boolean canInject(EnumFacing dir, TileEntity e) {
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
