package zmaster587.advancedRocketry.entity;

import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.tile.multiblock.TilePlanetaryHologram;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityUIPlanet extends Entity {

	DimensionProperties properties;
	protected TilePlanetaryHologram tile;
	protected static final DataParameter<Integer> planetID =  EntityDataManager.<Integer>createKey(EntityUIPlanet.class, DataSerializers.VARINT);
	protected static final DataParameter<Float> scale =  EntityDataManager.<Float>createKey(EntityUIPlanet.class, DataSerializers.FLOAT);
	protected static final DataParameter<Boolean> selected =  EntityDataManager.<Boolean>createKey(EntityUIPlanet.class, DataSerializers.BOOLEAN);
	
	
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
		float scale = this.dataManager.get(this.scale);
		setSize(0.1f*scale, 0.1f*scale);
		return scale;
	}
	
	public void setScale(float myScale) {
		setSize(0.08f*myScale, 0.08f*myScale);
		this.dataManager.set(scale, myScale);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound p_189511_1_) {
		//DO not save
		return null;
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(planetID, properties == null ? -1 : properties.getId());
		this.dataManager.register(scale, 1f);
		this.dataManager.register(selected, false);
		
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
	public boolean processInitialInteract(EntityPlayer player, ItemStack stack,
			EnumHand hand) {
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

		int planetId = this.dataManager.get(planetID);

		if(properties != null && properties.getId() != planetId) {
			if(planetId == -1 )
				properties = null;
			else
				properties = DimensionManager.getInstance().getDimensionProperties(planetId);
		}

		return this.dataManager.get(planetID);
	}

	public void setProperties(DimensionProperties properties) {
		this.properties = properties;
		if(properties != null)
			this.dataManager.set(planetID, properties.getId());
		else
			this.dataManager.set(planetID, -1);
	}
	
	public void setSelected(boolean isSelected) {
		this.dataManager.set(selected, isSelected);
	}
	
	public boolean isSelected() {
		return this.dataManager.get(selected);
	}
	
	public void setPositionPolar(double originX, double originY, double originZ, double radius, double theta) {
		originX += radius*MathHelper.cos((float) theta);
		originZ += radius*MathHelper.sin((float) theta);
		
		setPosition(originX, originY, originZ);
	}

}
