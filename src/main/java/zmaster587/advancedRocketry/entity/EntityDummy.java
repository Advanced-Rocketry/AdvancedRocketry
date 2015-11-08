package zmaster587.advancedRocketry.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityDummy extends Entity {

	
	//Just a dummy so a player can sit on a chair
	public EntityDummy(World world) {
		super(world);
		this.noClip=true;
		this.height=0f;
		this.yOffset=0.2f;
		
	}

	public EntityDummy(World world, double x, double y, double z) {
		this(world);
		
		this.posX = this.lastTickPosX = x;
		this.posY = this.lastTickPosY = y;
		this.posZ = this.lastTickPosZ = z;
		this.boundingBox.minY = this.boundingBox.maxY = y + .3f;
	}
	
	@Override
	public boolean isInvisible() {
		return true;
	}
	
	@Override
	public void onChunkLoad() {
		super.onChunkLoad();
		this.setDead();
	}

	/**
	 * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
	 * length * 64 * renderDistanceWeight Args: distance
	 */
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double p_70112_1_)
	{
		return false;
	}

	
	@Override
	protected void entityInit() {
		
	}

	@Override
	public boolean shouldRiderSit() {
		return true;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
		
	}

	
}
