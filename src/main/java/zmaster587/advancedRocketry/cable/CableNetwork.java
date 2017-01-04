package zmaster587.advancedRocketry.cable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.tile.cables.TilePipe;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.SingleEntry;

public class CableNetwork {

	int networkID;
	protected int numCables = 0;

	protected static HashSet<Integer> usedIds = new HashSet<Integer>();

	HashSet<Entry<TileEntity, ForgeDirection>> sources;

	HashSet<Entry<TileEntity, ForgeDirection>> sinks;

	protected CableNetwork() {

		sources = new HashSet<Entry<TileEntity, ForgeDirection>>();
		sinks = new HashSet<Entry<TileEntity, ForgeDirection>>();
	}

	public HashSet<Entry<TileEntity, ForgeDirection>> getSources() {
		return sources;
	}

	public HashSet<Entry<TileEntity, ForgeDirection>> getSinks() {
		return sinks;
	}

	public void addSource(TileEntity tile, ForgeDirection dir) {

		Iterator<Entry<TileEntity, ForgeDirection>> iter = sources.iterator();

		while(iter.hasNext()) {
			Entry<TileEntity, ForgeDirection> entry;
			TileEntity tile2 =  iter.next().getKey();
			if(tile2.equals(tile)) {
				return;
			}
			if(tile2.xCoord == tile.xCoord && tile2.yCoord == tile.yCoord && tile2.zCoord == tile.zCoord) {
				iter.remove();
				break;
			}
		}

		sources.add(new SingleEntry(tile, dir));
	}

	public void addSink(TileEntity tile, ForgeDirection dir) {

		Iterator<Entry<TileEntity, ForgeDirection>> iter = sinks.iterator();

		while(iter.hasNext()) {
			Entry<TileEntity, ForgeDirection> entry;
			TileEntity tile2 =  iter.next().getKey();
			if(tile2.equals(tile)) {
				return;
			}
			if(tile2.xCoord == tile.xCoord && tile2.yCoord == tile.yCoord && tile2.zCoord == tile.zCoord) {
				iter.remove();
				break;
			}
		}

		sinks.add(new SingleEntry(tile, dir));
	}

	public void writeToNBT(NBTTagCompound nbt) {

	}


	public void readFromNBT(NBTTagCompound nbt) {

	}

	public static CableNetwork initWithID(int id) {
		CableNetwork net = new CableNetwork();
		net.networkID = id;

		return net;
	}

	public static CableNetwork initNetwork() {
		Random random = new Random(System.currentTimeMillis());

		int id = random.nextInt();

		while(usedIds.contains(id)){ id = random.nextInt(); };

		CableNetwork net = new CableNetwork();

		usedIds.add(id);
		net.networkID = id;

		return net;
	}

	public int getNetworkID() {	return networkID; }

	public void removeFromAll(TileEntity tile) {
		Iterator<Entry<TileEntity, ForgeDirection>> iter = sources.iterator();

		while(iter.hasNext()) {
			Entry<TileEntity, ForgeDirection> entry = iter.next();
			TileEntity tile2 = entry.getKey();
			if(tile2.xCoord == tile.xCoord && tile2.yCoord == tile.yCoord && tile2.zCoord == tile.zCoord) {
				sources.remove(entry);
				break;
			}
		}

		iter = sinks.iterator();

		while(iter.hasNext()) {
			Entry<TileEntity, ForgeDirection> entry = iter.next();
			TileEntity tile2 = entry.getKey();
			if(tile2.xCoord == tile.xCoord && tile2.yCoord == tile.yCoord && tile2.zCoord == tile.zCoord) {
				sinks.remove(entry);
				break;
			}
		}

	}

	@Override 
	public String toString() {
		String output = "Sources: ";
		for(Entry<TileEntity, ForgeDirection> obj : sources) {
			TileEntity tile = (TileEntity)obj.getKey();
			output += tile.xCoord + "," + tile.yCoord + "," + tile.zCoord + " ";
		}

		output += "    Sinks: ";
		for(Entry<TileEntity, ForgeDirection> obj : sinks) {
			TileEntity tile = (TileEntity)obj.getKey();
			output += tile.xCoord + "," + tile.yCoord + "," + tile.zCoord + " ";
		}
		return output;
	}

	/**
	 * Merges this network with the one specified.  Normally the specified one is removed
	 * @param cableNetwork
	 */
	public boolean merge(CableNetwork cableNetwork) {
		sinks.addAll(cableNetwork.getSinks());

		for(Entry<TileEntity, ForgeDirection> obj : cableNetwork.getSinks()) {
			boolean canMerge = true;
			for(Entry<TileEntity, ForgeDirection> obj2 : sinks) {
				if(obj.getKey().xCoord == obj2.getKey().xCoord && obj.getKey().yCoord == obj2.getKey().yCoord && obj.getKey().zCoord == obj2.getKey().zCoord && obj.getValue() == obj2.getValue()) {
					return false;
				}
			}

			if(canMerge) {
				sinks.add(obj);
			}
		}

		for(Entry<TileEntity, ForgeDirection> obj : cableNetwork.getSources()) {
			boolean canMerge = true;
			for(Entry<TileEntity, ForgeDirection> obj2 : sources) {
				if(obj.getKey().xCoord == obj2.getKey().xCoord && obj.getKey().yCoord == obj2.getKey().yCoord && obj.getKey().zCoord == obj2.getKey().zCoord && obj.getValue() == obj2.getValue()) {
					canMerge = false;
					return false;
				}
			}

			if(canMerge) {
				sources.add(obj);
			}
		}
		return true;
	}

	public void tick() {
	}

	public void removePipeFromNetwork(TilePipe tilePipe) {
		numCables--;
	}

	public void addPipeToNetwork(TilePipe tilePipe) {
		numCables++;
	}
}
