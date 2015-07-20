package zmaster587.advancedRocketry.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface ISatallite {
	
	/**
	 * Gets status info eg "Moving into position" or "Ready" or "68% damaged"
	 * @return
	 */
	public String getInfo();
	
	/**
	 * Gets the display name of the satellite
	 * @return display name of the satellite
	 */
	public String getName();
	
	/**
	 * Actually does something with the satellite.  Normally called when the player rightclicks the master block
	 * @param Player interacting with the satellite
	 * @return whether the player has successfully interacted with the satellite
	 */
	public boolean performAction(EntityPlayer player, World world,int x, int y, int z);
	
	/**
	 * Note: this is not currently used
	 * @return chance from 0 to 1 of failing this tick
	 */
	public double failureChance();
	
	/**
	 * 
	 * @param nbt NBT data to store
	 */
	public void writeToNBT(NBTTagCompound nbt);
	
	public void readFromNBT(NBTTagCompound nbt);
}
