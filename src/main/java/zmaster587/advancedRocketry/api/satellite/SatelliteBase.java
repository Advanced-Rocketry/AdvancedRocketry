package zmaster587.advancedRocketry.api.satellite;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.ISatelliteIdItem;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.item.ItemSatellite;
import zmaster587.libVulpes.util.UniversalBattery;

import javax.annotation.Nonnull;

public abstract class SatelliteBase {
	
	protected SatelliteProperties satelliteProperties;
	private int dimId = Constants.INVALID_PLANET;
	//Will always be of type ItemSatellite
	protected ItemStack satellite;

	private boolean isDead;

	//Satellite energy storage
	protected UniversalBattery battery;
	
	public SatelliteBase() {
		satelliteProperties = new SatelliteProperties();
		satelliteProperties.setSatelliteType(SatelliteRegistry.getKey(this.getClass()));
		isDead = false;
		satellite = ItemStack.EMPTY;

		//Satellite energy storage
		battery = new UniversalBattery(this.satelliteProperties.getPowerStorage());

	}
	
	public boolean acceptsItemInConstruction(@Nonnull ItemStack item) {
		int flag = SatelliteRegistry.getSatelliteProperty(item).getPropertyFlag();
		return SatelliteProperties.Property.MAIN.isOfType(flag) || SatelliteProperties.Property.POWER_GEN.isOfType(flag) || SatelliteProperties.Property.BATTERY.isOfType(flag);
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
	 * @param player interacting with the satellite
	 * @param world
	 * @param pos
	 * @return whether the player has successfully interacted with the satellite
	 */
	public abstract boolean performAction(EntityPlayer player, World world, BlockPos pos);
	
	/**
	 * Note: this is not currently used
	 * @return chance from 0 to 1 of failing this tick
	 */
	public abstract double failureChance();

	/**
	 * @return the power per tick the satellite produces
	 */
	public int getPowerPerTick() {
		return satelliteProperties.getPowerGeneration();
	}

	/**
	 * @return an item that can be used to control the satellite, normally a satellite ID chip but can be something else
	 */
	@Nonnull
	public ItemStack getControllerItemStack(@Nonnull ItemStack satIdChip, SatelliteProperties properties) {
		ISatelliteIdItem idChipItem = (ISatelliteIdItem)satIdChip.getItem();
		idChipItem.setSatellite(satIdChip, properties);
		return satIdChip;
	}
	
	/**
	 * @param stack stack to check (can be null)
	 * @return true if the item stack is a valid controller for the satellite
	 */
	public boolean isAcceptableControllerItemStack(@Nonnull ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() == AdvancedRocketryItems.itemSatelliteIdChip;
	}
	
	/**
	 * @return true if the satellite can tick
	 */
	public boolean canTick() {
		return true;
	}
	
	/**
	 * called every tick if satellite can tick
	 */
	public void tickEntity() {
		//Base power consumption is 1 energy per tick. Think of it like a communications & positioning upkeep amount. Some satellites may end up overriding this
		battery.acceptEnergy(getPowerPerTick() - 1, false);
	}
	
	/**
	 * @return the long id of the satellite, used to get a satellite from the main list
	 */
	public long getId() {
		return satelliteProperties.getId();
	}
	
	public void setDead(){
		isDead = true;
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	/**
	 * Does not currently support dimension change
	 * @param world World of which to assign to the satellite
	 */
	public void setDimensionId(World world) {
		int newId = world.provider.getDimension();
		if(dimId != Constants.INVALID_PLANET) {
			//TODO: handle dim change
		}
		dimId = newId;
	}
	
	public void setDimensionId(int world) {
		if(dimId != Constants.INVALID_PLANET) {
			//TODO: handle dim change
		}
		dimId = world;
	}
	
	/**
	 * @param stack satelliteProperties to assign to this satellite
	 */
	public void setProperties(@Nonnull ItemStack stack) {
		this.satelliteProperties = SatelliteRegistry.getSatelliteProperty(stack);
		this.battery.setMaxEnergyStored(satelliteProperties.getPowerStorage());
		this.satellite = stack;
	}

	@Nonnull
	public ItemStack getItemStackFromSatellite() {
		return satellite;
	}
	
	/**
	 * @return dimensionID of the satellite, Constants.INVALID_PLANET if none
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
		battery.writeToNBT(nbt);
		
		NBTTagCompound itemNBT = new NBTTagCompound();
		//Transition
		if(!satellite.isEmpty())
			satellite.writeToNBT(itemNBT);
		nbt.setTag("item", itemNBT);
		
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		satelliteProperties.readFromNBT(nbt.getCompoundTag("properties"));
		dimId = nbt.getInteger("dimId");
		satellite = new ItemStack(nbt.getCompoundTag("item"));
		battery.readFromNBT(nbt);
		if (satelliteProperties.getPowerStorage() == 0) {
			satelliteProperties.setPowerStorage(720);
			battery = new UniversalBattery(720);
		}
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
	
	public void onChangeReceived(int slot, int value) {

	}
	
	public boolean isUpdateRequired(int localId) {
		return false;
	}
	
	public void sendChanges(Container container, IContainerListener crafter, int variableId, int localId) {

	}


}
