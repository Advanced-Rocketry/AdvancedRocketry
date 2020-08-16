package zmaster587.advancedRocketry.cable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import zmaster587.advancedRocketry.tile.cables.TilePipe;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

public class HandlerCableNetwork {
	protected Hashtable<Integer,CableNetwork> networks = new Hashtable<Integer,CableNetwork>();

	//private static final String FILENAME = "/data/insanityCraft.dat";
	
	/*public static void loadNetworksFromFile() throws IOException {
		String saveDir = MinecraftServer.getServer().getActiveAnvilConverter().getSaveLoader(MinecraftServer.getServer().getFolderName(), false).getWorldDirectoryName();

		FileInputStream stream = new FileInputStream(saveDir + FILENAME);

		CompoundNBT nbt = CompressedStreamTools.readCompressed(stream);

		stream.close();

		Iterator<Object> iterator = nbt.func_150296_c().iterator();

		while(iterator.hasNext()) {

			String key = (String)iterator.next();

			CompoundNBT subNbt = nbt.getCompound(key);

			CableNetwork net = CableNetwork.initWithID(Integer.parseInt(key));
			net.readFromNBT(subNbt);
		}
	}

	public static void saveNetworksToFile() throws IOException {

		CompoundNBT nbt = new CompoundNBT();
		for(Entry<Integer,CableNetwork> set : networks.entrySet()) {

			CompoundNBT subNbt = new CompoundNBT();
			set.getValue().writeToNBT(subNbt);
			nbt.put(String.valueOf(set.getKey()), subNbt);
		}

		String saveDir = MinecraftServer.getServer().getActiveAnvilConverter().getSaveLoader(MinecraftServer.getServer().getFolderName(), false).getWorldDirectoryName();

		FileOutputStream stream = new FileOutputStream(saveDir + "/data/insanityCraft.dat");

		CompressedStreamTools.writeCompressed(nbt, stream);

		stream.close();
	}*/

	public int getNewNetworkID() {
		CableNetwork net = CableNetwork.initNetwork();

		networks.put(net.networkID, net);

		return net.networkID;
	}
	

	public int mergeNetworks(int a, int b) {
		
		assert(networks.get(Math.max(a, b)) == null || networks.get(Math.min(a, b)) == null);
		
		networks.get(Math.min(a, b)).merge(networks.get(Math.max(a, b)));
		networks.get(Math.min(a, b)).numCables += networks.get(Math.max(a, b)).numCables;
		
		networks.remove(Math.max(a, b));
		
		
		return Math.min(a, b);
	}

	public void tickAllNetworks() {
		Iterator<Entry<Integer, CableNetwork>> iter = networks.entrySet().iterator();
		while(iter.hasNext()) {
			iter.next().getValue().tick();
		}
	}

	public boolean doesNetworkExist(int id) {
		return networks.containsKey(id);
	}

	/**
	 * Adds a source to the network on the side specified
	 * @param tilePipe The pipe adding the source
	 * @param tile The source to be added
	 * @param dir Direction of the source from the pipe
	 */
	public void addSource(TilePipe tilePipe, TileEntity tile, Direction dir) {
		networks.get(tilePipe.getNetworkID()).addSource(tile, dir.getOpposite());
	}

	/**
	 * Adds a sink to the network on the side specified
	 * @param tilePipe The pipe adding the sink
	 * @param tile The sink to be added
	 * @param dir Direction of the sink from the pipe
	 */
	public void addSink(TilePipe tilePipe, TileEntity tile, Direction dir) {
		networks.get(tilePipe.getNetworkID()).addSink(tile, dir.getOpposite());
	}
	
	/**
	 * Removed the specified network ID from the handler
	 * @param id id of the network to remove
	 */
	public void removeNetworkByID(int id) {
		networks.remove(id);
	}

	/**
	 * Removes the specified tile from both the sources and sink list
	 * @param pipe pipe that belongs to a network
	 * @param tile tile to be removed from the sinks and sources list
	 */
	public void removeFromAllTypes(TilePipe pipe, TileEntity tile) {
		if(pipe.isInitialized())
			networks.get(pipe.getNetworkID()).removeFromAll(tile);
	}

	/**What did you think this does?*/
	public String toString(int networkID) {
		if(networks.get(networkID) != null)
			return networks.get(networkID).toString();
		return "";
	}

	public CableNetwork getNetwork(int id) {
		return networks.get(id);
	}
}
