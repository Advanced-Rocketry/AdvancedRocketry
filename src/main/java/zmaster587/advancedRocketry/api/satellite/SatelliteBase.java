package zmaster587.advancedRocketry.api.satellite;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class SatelliteBase {
	
	protected SatelliteProperties satelliteProperties;
	private int dimId = -1;

	public SatelliteBase() {
		satelliteProperties = new SatelliteProperties();
		satelliteProperties.setSatelliteType(SatelliteRegistry.getKey(this.getClass()));
	}
	
	public boolean acceptsItemInConstruction(ItemStack item) {
		int flag = SatelliteRegistry.getSatelliteProperty(item).getPropertyFlag();
		return SatelliteProperties.Property.MAIN.isOfType(flag);
	}
	
	/**
	 * Gets status info eg "Moving into position" or "Ready" or "68% damaged"
	 * @return Human-readable Information about the satellite (supports \n for newline)
	 */
	public abstract String getInfo(World world);
	
	/**
	 * Gets the human-readable display name of the satellite
	 * @return display name of the satellite
	 */
	public abstract String getName();
	
	/**
	 * Actually does something with the satellite.  Normally called when the player rightclicks the master block
	 * @param Player interacting with the satellite
	 * @return whether the player has successfully interacted with the satellite
	 */
	public abstract boolean performAction(EntityPlayer player, World world,int x, int y, int z);
	
	/**
	 * Note: this is not currently used
	 * @return chance from 0 to 1 of failing this tick
	 */
	public abstract double failureChance();
	
	
	/**
	 * @return true if the satellite can tick
	 */
	public boolean canTick() {
		return false;
	}
	
	/**
	 * called every tick if satellite can tick
	 */
	public void tickEntity() {}
	
	/**
	 * @return the long id of the satellite, used to get a satellite from the main list
	 */
	public long getId() {
		return satelliteProperties.getId();
	}
	
	/**
	 * Does not currently support dimension change
	 * @param world World of which to assign to the satellite
	 */
	public void setDimensionId(World world) {
		int newId = world.provider.dimensionId;
		if(dimId != -1) {
			//TODO: handle dim change
		}
		dimId = newId;
	}
	
	/**
	 * @param satelliteProperties satelliteProperties to assign to this satellite
	 */
	public void setProperties(SatelliteProperties satelliteProperties) {
		this.satelliteProperties = satelliteProperties;
	}
	
	/**
	 * @return dimensionID of the satellite, -1 if none
	 */
	public int getDimensionId() {
		return dimId;
	}
	
	/**
	 * @param nbt NBT data to store
	 */
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setString("dataType", SatelliteRegistry.getKey(this.getClass()));
		
		NBTTagCompound properties = new NBTTagCompound();
		satelliteProperties.writeToNBT(properties);
		nbt.setTag("properties", properties);
		nbt.setInteger("dimId", dimId);
		
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		satelliteProperties.readFromNBT(nbt.getCompoundTag("properties"));
		dimId = nbt.getInteger("dimId");
	}
	
	public void writeDataToNetwork(ByteBuf out, byte packetId) {
		
	}
	
	public void readDataToNetwork(byte packetId, ByteBuf in) {
		
	}
	
	public void useNetworkData(EntityPlayer player, Side client, byte packetId,
			NBTTagCompound nbt) {
		
	}
	
	//Server Syncing stuff
	//Used if the satellite needs to sync in a modularGUI
	
	public int numberChangesToSend() {
		return 0;
	}
	
	public void onChangeRecieved(int slot, int value) {

	}
	
	public boolean isUpdateRequired(int localId) {
		return false;
	}
	
	public void sendChanges(Container container, ICrafting crafter, int variableId, int localId) {

	}


}
