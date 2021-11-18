package zmaster587.advancedRocketry.util;

import net.minecraft.nbt.JsonToNBT;

import java.util.UUID;

import org.w3c.dom.DOMException;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;

public class SpawnListEntryNBT extends Spawners {

	CompoundNBT nbt;
	String nbtString;
	
	public SpawnListEntryNBT(EntityType<?> entityclassIn, int weight, int groupCountMin,
			int groupCountMax) {
		super(entityclassIn, weight, groupCountMin, groupCountMax);
		nbt = null;
		nbtString = "";
	}
	
	public void setNbt(String nbtString) throws DOMException, CommandSyntaxException {
		
		this.nbtString = nbtString;
		if(nbtString.isEmpty())
			this.nbt = null;
		else
			this.nbt = JsonToNBT.getTagFromJson(nbtString);
	}
	
	public String getNBTString() {
		return this.nbtString;
	}
	
	
	public LivingEntity newInstance(World world) {
		LivingEntity entity = (LivingEntity) super.type.create(world);
		if(nbt != null) {
            UUID uuid = entity.getUniqueID();
            CompoundNBT oldNbt = entity.serializeNBT();
            oldNbt.merge(nbt);
			entity.deserializeNBT(nbt);
			entity.setUniqueId(uuid);
		}
		return entity;
	}
}
