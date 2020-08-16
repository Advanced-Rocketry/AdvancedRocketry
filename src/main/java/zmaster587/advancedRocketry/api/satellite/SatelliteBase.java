package zmaster587.advancedRocketry.api.satellite;

import java.util.Optional;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.ISatelliteIdItem;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.item.ItemSatellite;
import zmaster587.libVulpes.util.ZUtils;

public abstract class SatelliteBase {
	
	protected SatelliteProperties satelliteProperties;
	private Optional<ResourceLocation> dimId = Optional.empty();
	//Will always be of type ItemSatellite
	protected ItemStack satellite;

	private boolean isDead;
	
	public SatelliteBase() {
		satelliteProperties = new SatelliteProperties();
		satelliteProperties.setSatelliteType(SatelliteRegistry.getKey(this.getClass()));
		isDead = false;
		satellite = ItemStack.EMPTY;
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
	public abstract boolean performAction(PlayerEntity player, World world, BlockPos pos);
	
	/**
	 * Note: this is not currently used
	 * @return chance from 0 to 1 of failing this tick
	 */
	public abstract double failureChance();
	
	/**
	 * @return an item that can be used to control the satellite, normally a satellite ID chip but can be something else
	 */
	public ItemStack getContollerItemStack(ItemStack satIdChip, SatelliteProperties properties) {
		ISatelliteIdItem idChipItem = (ISatelliteIdItem)satIdChip.getItem();
		idChipItem.setSatellite(satIdChip, properties);
		return satIdChip;
	}
	
	/**
	 * @param stack stack to check (can be null)
	 * @return true if the item stack is a valid controller for the satellite
	 */
	public boolean isAcceptableControllerItemStack(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() == AdvancedRocketryItems.itemSatelliteIdChip;
	}
	
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
	
	public void remove(){
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
		ResourceLocation newId = ZUtils.getDimensionIdentifier(world);
		if(dimId.isPresent()) {
			//TODO: handle dim change
		}
		dimId = Optional.of(newId);
	}
	
	public void setDimensionId(ResourceLocation world) {
		ResourceLocation newId = world;
		if(dimId.isPresent()) {
			//TODO: handle dim change
		}
		dimId =  Optional.of(newId);
	}
	
	/**
	 * @param satelliteProperties satelliteProperties to assign to this satellite
	 */
	public void setProperties(ItemStack stack) {
		this.satelliteProperties = ((ItemSatellite)stack.getItem()).getSatellite(stack);
		this.satellite = stack;
	}
	
	public ItemStack getItemStackFromSatellite() {
		return satellite;
	}
	
	/**
	 * @return dimensionID of the satellite, Constants.INVALID_PLANET if none
	 */
	public Optional<ResourceLocation> getDimensionId() {
		return dimId;
	}
	
	/**
	 * @param nbt NBT data to store
	 */
	public void writeToNBT(CompoundNBT nbt) {
		nbt.putString("dataType", SatelliteRegistry.getKey(this.getClass()));
		
		CompoundNBT properties = new CompoundNBT();
		satelliteProperties.writeToNBT(properties);
		nbt.put("properties", properties);
		dimId.ifPresent(value -> nbt.putString("dimId", value.toString() ));
		
		CompoundNBT itemNBT = new CompoundNBT();
		//Transition
		if(!satellite.isEmpty())
			satellite.write(itemNBT);
		nbt.put("item", itemNBT);
		
	}
	
	public void readFromNBT(CompoundNBT nbt) {
		satelliteProperties.readFromNBT(nbt.getCompound("properties"));
		
		if(nbt.contains("dimId"))
			dimId = Optional.of( new ResourceLocation(nbt.getString("dimId")));
		else
			dimId = Optional.empty();
		satellite = ItemStack.read(nbt.getCompound("item"));
	}
	
	public void writeDataToNetwork(ByteBuf out, byte packetId) {
		
	}
	
	public void readDataToNetwork(byte packetId, ByteBuf in) {
		
	}
	
	public void useNetworkData(PlayerEntity player, Dist client, byte packetId,
			CompoundNBT nbt) {
		
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
	
	public void sendChanges(Container container, IContainerListener crafter, int variableId, int localId) {

	}


}
