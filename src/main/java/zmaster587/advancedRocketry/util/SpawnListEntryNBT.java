package zmaster587.advancedRocketry.util;

import java.util.UUID;

import org.w3c.dom.DOMException;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;

public class SpawnListEntryNBT extends SpawnListEntry {

	NBTTagCompound nbt;
	String nbtString;
	
	public SpawnListEntryNBT(Class<? extends EntityLiving> entityclassIn, int weight, int groupCountMin,
			int groupCountMax) {
		super(entityclassIn, weight, groupCountMin, groupCountMax);
		nbt = null;
		nbtString = "";
	}
	
	public void setNbt(String nbtString) throws DOMException, NBTException {
		
		this.nbtString = nbtString;
		if(nbtString.isEmpty())
			this.nbt = null;
		else
			this.nbt = JsonToNBT.getTagFromJson(nbtString);
	}
	
	public String getNBTString() {
		return this.nbtString;
	}
	
	@Override
	public EntityLiving newInstance(World world) throws Exception {
		EntityLiving entity = super.newInstance(world);
		if(nbt != null) {
            UUID uuid = entity.getUniqueID();
            NBTTagCompound oldNbt = entity.writeToNBT(new NBTTagCompound());
            oldNbt.merge(nbt);
			entity.readFromNBT(nbt);
			entity.setUniqueId(uuid);
		}
		return entity;
	}
}
