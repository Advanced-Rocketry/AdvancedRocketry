package zmaster587.advancedRocketry.util;

import java.util.UUID;

import org.w3c.dom.DOMException;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class SpawnListEntryNBT extends SpawnListEntry {

	CompoundNBT nbt;
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
            CompoundNBT oldNbt = entity.writeToNBT(new CompoundNBT());
            oldNbt.merge(nbt);
			entity.readFromNBT(nbt);
			entity.setUniqueId(uuid);
		}
		return entity;
	}
}
