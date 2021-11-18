package zmaster587.advancedRocketry.cable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import zmaster587.advancedRocketry.tile.cables.TilePipe;
import zmaster587.libVulpes.util.SingleEntry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class CableNetwork {

	int networkID;

	protected static HashSet<Integer> usedIds = new HashSet<>();

	CopyOnWriteArraySet<Entry<TileEntity, Direction>> sources;

	protected int numCables = 0;
	
	CopyOnWriteArraySet<Entry<TileEntity, Direction>> sinks;

	protected CableNetwork() {

		sources = new CopyOnWriteArraySet<>();
		sinks = new CopyOnWriteArraySet<>();
	}

	public Set<Entry<TileEntity, Direction>> getSources() {
		return sources;
	}

	public Set<Entry<TileEntity, Direction>> getSinks() {
		return sinks;
	}

	public void addSource(TileEntity tile, Direction dir) {

		for (Entry<TileEntity, Direction> entry : sources) {
			TileEntity tile2 = entry.getKey();
			if (tile2.equals(tile)) {
				return;
			}
			if (tile2.getPos().compareTo(tile.getPos()) == 0) {
				sources.remove(entry);
				//iter.remove();
				break;
			}
		}

		sources.add(new SingleEntry<>(tile, dir));
	}

	public void addSink(TileEntity tile, Direction dir) {

		for (Entry<TileEntity, Direction> entry : sinks) {
			TileEntity tile2 = entry.getKey();
			if (tile2.equals(tile)) {
				return;
			}
			if (tile2.getPos().compareTo(tile.getPos()) == 0) {
				sinks.remove(entry);
				//iter.remove();
				break;
			}
		}

		sinks.add(new SingleEntry<>(tile, dir));
	}

	public void writeToNBT(CompoundNBT nbt) {

	}


	public void readFromNBT(CompoundNBT nbt) {

	}

	public static CableNetwork initWithID(int id) {
		CableNetwork net = new CableNetwork();
		net.networkID = id;

		return net;
	}

	public static CableNetwork initNetwork() {
		Random random = new Random(System.currentTimeMillis());

		int id = random.nextInt();

		while(usedIds.contains(id)){ id = random.nextInt(); }

		CableNetwork net = new CableNetwork();

		usedIds.add(id);
		net.networkID = id;

		return net;
	}

	public int getNetworkID() {	return networkID; }

	public void removeFromAll(TileEntity tile) {
		Iterator<Entry<TileEntity, Direction>> iter = sources.iterator();

		while(iter.hasNext()) {
			Entry<TileEntity, Direction> entry = iter.next();
			TileEntity tile2 = entry.getKey();
			if(tile2.getPos().compareTo(tile.getPos()) == 0) {
				sources.remove(entry);
				break;
			}
		}

		iter = sinks.iterator();

		while(iter.hasNext()) {
			Entry<TileEntity, Direction> entry = iter.next();
			TileEntity tile2 = entry.getKey();
			if(tile2.getPos().compareTo(tile.getPos()) == 0) {
				sinks.remove(entry);
				break;
			}
		}

	}

	@Override 
	public String toString() {
		String output = "NumCables:   " + numCables + "     Sources: ";
		for(Entry<TileEntity, Direction> obj : sources) {
			TileEntity tile = obj.getKey();
			output += tile.getPos().getX() + "," + tile.getPos().getY() + "," + tile.getPos().getZ() + " ";
		}

		output += "    Sinks: ";
		for(Entry<TileEntity, Direction> obj : sinks) {
			TileEntity tile = obj.getKey();
			output += tile.getPos().getX() + "," + tile.getPos().getY() + "," + tile.getPos().getZ() + " ";
		}
		return output;
	}

	/**
	 * Merges this network with the one specified.  Normally the specified one is removed
	 * @param cableNetwork
	 */
	public boolean merge(CableNetwork cableNetwork) {
		sinks.addAll(cableNetwork.getSinks());

		for(Entry<TileEntity, Direction> obj : cableNetwork.getSinks()) {
			boolean canMerge = true;
			for(Entry<TileEntity, Direction> obj2 : sinks) {
				if(obj.getKey().getPos().compareTo(obj2.getKey().getPos()) == 0 && obj.getValue() == obj2.getValue()) {
					//canMerge = false;
					return false;
				}
			}

			//if(canMerge) {
				sinks.add(obj);
			//}
		}

		for(Entry<TileEntity, Direction> obj : cableNetwork.getSources()) {
			boolean canMerge = true;
			for(Entry<TileEntity, Direction> obj2 : sources) {
				if(obj.getKey().getPos().compareTo(obj2.getKey().getPos()) == 0 && obj.getValue() == obj2.getValue()) {
					//canMerge = false;
					return false;
				}
			}

			//if(canMerge) {
				sources.add(obj);
			//}
		}
		return true;
	}

	public void addPipeToNetwork(TilePipe tile) {
		numCables++;
	}
	
	public void tick() {
	}

	public void removePipeFromNetwork(TilePipe tilePipe) {
		numCables--;
		
	}
}
