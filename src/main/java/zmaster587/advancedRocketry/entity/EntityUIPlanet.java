package zmaster587.advancedRocketry.entity;

import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.tile.station.TilePlanetaryHologram;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityUIPlanet extends Entity {

	DimensionProperties properties;
	protected TilePlanetaryHologram tile;
	protected static final int PLANET_ID =  2;
	protected static final int SCALE_ID = 3;
	protected static final int selected =  4;
	
	
	public EntityUIPlanet(World worldIn, DimensionProperties properties, TilePlanetaryHologram tile, double x, double y, double z) {
		this(worldIn);
		setPosition(x, y, z);
		setProperties(properties);
		this.tile = tile;
	}
	
	public EntityUIPlanet(World worldIn) {
		super(worldIn);
		setSize(0.2f, 0.2f);
	}
	
	public float getScale() {
		float scale = this.dataWatcher.getWatchableObjectFloat(SCALE_ID);
		setSize(0.1f*scale, 0.1f*scale);
		return scale;
	}
	
	public void setScale(float myScale) {
		setSize(0.08f*myScale, 0.08f*myScale);
		this.dataWatcher.updateObject(SCALE_ID, myScale);
	}

	@Override
	public void writeToNBT(NBTTagCompound p_189511_1_) {
		//DO not save
	}
	
	@Override
	public boolean writeToNBTOptional(NBTTagCompound p_70039_1_) {
		return false;
	}
	
	@Override
	public boolean writeMountToNBT(NBTTagCompound p_98035_1_) {
		return false;
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(PLANET_ID, properties == null ? -1 : properties.getId());
		this.dataWatcher.addObject(SCALE_ID, 1f);
		this.dataWatcher.addObject(selected, (byte)0);
		
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {

	}
	
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
	
	@Override
	public boolean canBePushed() {
		return false;
	}
	
	
	@Override
	public boolean interactFirst(EntityPlayer p_130002_1_) {
		if(!worldObj.isRemote && tile != null) {
			tile.selectSystem(properties.getId());
		}
		return true;
	}
	

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {

	}

	public DimensionProperties getProperties() {
		if((properties == null && getPlanetID() != -1) || (properties != null && getPlanetID() != properties.getId())) {
			properties = DimensionManager.getInstance().getDimensionProperties(getPlanetID());
		}

		return properties;
	}

	public int getPlanetID() {
		//this.dataManager.set(planetID, 256);

		if(!worldObj.isRemote)
			return properties == null ? -1 : properties.getId();

		int planetId = this.dataWatcher.getWatchableObjectInt(PLANET_ID);

		if(properties != null && properties.getId() != planetId) {
			if(planetId == -1 )
				properties = null;
			else
				properties = DimensionManager.getInstance().getDimensionProperties(planetId);
		}

		return this.dataWatcher.getWatchableObjectInt(PLANET_ID);
	}

	public void setProperties(DimensionProperties properties) {
		this.properties = properties;
		if(properties != null)
			this.dataWatcher.updateObject(PLANET_ID, properties.getId());
		else
			this.dataWatcher.updateObject(PLANET_ID, -1);
	}
	
	public void setSelected(boolean isSelected) {
		this.dataWatcher.updateObject(selected, new Byte((byte) (isSelected ? 1 : 0)));
	}
	
	public boolean isSelected() {
		return this.dataWatcher.getWatchableObjectByte(selected) == 1 ? true : false;
	}
	
	public void setPositionPolar(double originX, double originY, double originZ, double radius, double theta) {
		originX += radius*MathHelper.cos((float) theta);
		originZ += radius*MathHelper.sin((float) theta);
		
		setPosition(originX, originY, originZ);
	}

}
