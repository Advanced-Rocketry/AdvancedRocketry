package zmaster587.advancedRocketry.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryEntities;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.tile.station.TilePlanetaryHologram;
import zmaster587.libVulpes.network.PacketSpawnEntity;

public class EntityUIPlanet extends Entity {

	DimensionProperties properties;
	protected TilePlanetaryHologram tile;
	protected static final DataParameter<String> planetID =  EntityDataManager.<String>createKey(EntityUIPlanet.class, DataSerializers.STRING);
	protected static final DataParameter<Float> scale =  EntityDataManager.<Float>createKey(EntityUIPlanet.class, DataSerializers.FLOAT);
	protected static final DataParameter<Boolean> selected =  EntityDataManager.<Boolean>createKey(EntityUIPlanet.class, DataSerializers.BOOLEAN);
	protected EntitySize size;
	
	public EntityUIPlanet(World worldIn, DimensionProperties properties, TilePlanetaryHologram tile, double x, double y, double z) {
		this(AdvancedRocketryEntities.ENTITY_UIPLANET, worldIn);
		setPosition(x, y, z);
		setProperties(properties);
		this.tile = tile;
	}
	
	public EntityUIPlanet(EntityType<?> type, World worldIn, DimensionProperties properties, TilePlanetaryHologram tile, double x, double y, double z) {
		this(type, worldIn);
		setPosition(x, y, z);
		setProperties(properties);
		this.tile = tile;
	}
	
	
	public EntityUIPlanet(EntityType<?> type, World worldIn) {
		super(type, worldIn);
		setSize(0.2f, 0.2f);
	}
	
	@Override
	public EntitySize getSize(Pose poseIn) {
		return super.getSize(poseIn);
	}
	
	public void setSize(float x, float z)
	{
		size = new EntitySize(x, z, false);
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
	public boolean writeUnlessRemoved(CompoundNBT compound) {
		return false;
	}
	

	@Override
	protected void registerData() {
		this.dataManager.register(planetID, properties == null ? Constants.INVALID_PLANET.toString() : properties.getId().toString());
		this.dataManager.register(scale, 1f);
		this.dataManager.register(selected, false);
		
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
	public ActionResultType processInitialInteract(PlayerEntity player, 
			Hand hand) {
		if(!world.isRemote && tile != null) {
			tile.selectSystem(properties.getId());
		}
		return ActionResultType.PASS;
	}

	@Override
	protected void writeAdditional(CompoundNBT compound) {

	}
	
	@Override
	protected void readAdditional(CompoundNBT compound) {
	}
	
	public DimensionProperties getProperties() {
		if((properties == null && getPlanetID() != Constants.INVALID_PLANET) || (properties != null && getPlanetID() != properties.getId())) {
			properties = DimensionManager.getInstance().getDimensionProperties(getPlanetID());
		}

		return properties;
	}

	public ResourceLocation getPlanetID() {
		//this.dataManager.set(planetID, 256);

		if(!world.isRemote)
			return properties == null ? Constants.INVALID_PLANET : properties.getId();

		ResourceLocation planetId = new ResourceLocation(this.dataManager.get(planetID));

		if(properties != null && properties.getId() != planetId) {
			if(planetId == Constants.INVALID_PLANET )
				properties = null;
			else
				properties = DimensionManager.getInstance().getDimensionProperties(planetId);
		}

		return planetId;
	}

	public void setProperties(DimensionProperties properties) {
		this.properties = properties;
		if(properties != null)
			this.dataManager.set(planetID, properties.getId().toString());
		else
			this.dataManager.set(planetID, Constants.INVALID_PLANET.toString());
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
	
	@Override
	public IPacket<?> createSpawnPacket() {
		return new PacketSpawnEntity(this);
	}

}
