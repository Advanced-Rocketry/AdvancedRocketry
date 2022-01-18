package zmaster587.advancedRocketry.stations;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.IStorageChunk;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

public abstract class SpaceObjectBase implements ISpaceObject {
	private int posX, posY;
	private int altitude;
	private HashedBlockPosition spawnLocation;
	private double[] rotation;
	private double[] angularVelocity;
	private long lastTimeModification = 0;
	private DimensionProperties properties;

	public SpaceObjectBase() {
		properties = (DimensionProperties) zmaster587.advancedRocketry.dimension.DimensionManager.defaultSpaceDimensionProperties.clone();
		angularVelocity = new double[3];
		rotation = new double[3];
	}

	public long getExpireTime() { 
		return Long.MAX_VALUE;
	}
	
	public void beginTransition(long time) {
	}

	public long getTransitionTime() {
		return 0;
	}

	/**
	 * @return id of the space object (NOT the DIMID)
	 */
	@Override
	public ResourceLocation getId() {
		return properties.getId();
	}

	/**
	 * @return dimension properties of the object
	 */
	@Override
	public DimensionProperties getProperties() {
		return properties;
	}

	@OnlyIn(value=Dist.CLIENT)
	public void setProperties(DimensionProperties properties) {
		this.properties = properties;
	}

	/**
	 * @return the DIMID of the planet the object is currently orbiting, Constants.INVALID_PLANET if none
	 */
	@Override
	public ResourceLocation getOrbitingPlanetId() {
		return properties.getParentPlanet();
	}

	/**
	 * Sets the forward Facing direction of the object.  Mostly used for warpships
	 * @param direction
	 */
	public void setForwardDirection(Direction direction) {
		
	}

	/**
	 * Gets the forward facing direction of the ship.  Direction is not garunteed to be set
	 * @return direction of the ship, or UNKNOWN if none exists
	 */
	public Direction getForwardDirection() {
			return Direction.DOWN;
	}

	/**
	 * @return if the object is anchored in place by anything
	 */
	@Override
	public boolean isAnchored() { return false;}

	/**
	 * Sets if the object is anchored or not
	 */
	@Override
	public void setIsAnchored(boolean anchored) {
	}

	/**
	 * @return the altitude above the parent DIM the object currently is
	 */
	public int getAltitude() {
		return altitude;
	}
	
	/**
	 * @return rotation of the station in degrees
	 */
	public double getRotation(Direction dir) {
		return (rotation[getIDFromDir(dir)] + getDeltaRotation(dir)*(getWorldTime() - lastTimeModification)) % (360D);
	}
	
	protected int getIDFromDir(Direction facing){
		if(facing == Direction.EAST)
			return 0;
		else if(facing == Direction.UP)
			return 1;
		else
			return 2;
	}
	
	/**
	 * @param rotation rotation of the station in degrees
	 */
	public void setRotation(double rotation, Direction facing) {
		this.rotation[getIDFromDir(facing)] = rotation;
	}
	
	/**
	 * @return anglarVelocity of the station in degrees per tick
	 */
	public double getDeltaRotation(Direction facing) {
		return this.angularVelocity[getIDFromDir(facing)];
	}
	
	/**
	 * @param rotation anglarVelocity of the station in degrees per tick
	 */
	public void setDeltaRotation(double rotation, Direction facing) {
		this.rotation[getIDFromDir(facing)] = getRotation(facing);
		this.lastTimeModification = getWorldTime();
		
		this.angularVelocity[getIDFromDir(facing)] = rotation;
	}
	
	public double getMaxRotationalAcceleration() {
		return 0d;
	}

	private long getWorldTime() {
		return AdvancedRocketry.proxy.getWorldTimeUniversal();
	}
	
	/**
	 * @return the X postion on the graph the object is stored in {@link SpaceObjectManager}
	 */
	public int getPosX() {
		return posX;
	}

	/**
	 * @return the Y postion on the graph the object is stored in {@link SpaceObjectManager}
	 */
	public int getPosY() {
		return posY;
	}

	/**
	 * @return the spawn location of the object
	 */
	public HashedBlockPosition getSpawnLocation() {
		return spawnLocation;
	}


	/**
	 * @param id the space object id of this object (NOT DIMID)
	 */
	@Override
	public void setId(ResourceLocation id) {
		properties.setId(id);
	}

	/**
	 * Sets the coords of the space object on the graph
	 * @param posX
	 * @param posY
	 */
	@Override
	public void setPos(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}

	/**
	 * Sets the spawn location for the space object
	 * @param x
	 * @param y
	 * @param z
	 */
	@Override
	public void setSpawnLocation(int x, int y, int z) {
		spawnLocation = new HashedBlockPosition(x,y,z);
	}

	/**
	 * Sets the orbiting planet for the space object but does NOT register it with the planet
	 * @param id
	 */
	@Override
	public void setOrbitingBody(ResourceLocation id) {
		if(id == this.getOrbitingPlanetId())
			return;

		properties.setParentPlanet(zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getDimensionProperties(id), false);
	}

	@Override
	public void setDestOrbitingBody(ResourceLocation id) {
		if(EffectiveSide.get().isServer()) {
			PacketHandler.sendToAll(new PacketStationUpdate(this, PacketStationUpdate.Type.DEST_ORBIT_UPDATE));
		}
	}

	@Override
	public ResourceLocation getDestOrbitingBody() {
		return DimensionManager.overworldProperties.getId();
	}

	/**
	 * When the space stations are first created they are 'unpacked' from the storage chunk they reside in
	 * @param chunk
	 */
	public void onModuleUnpack(IStorageChunk chunk) {
		World worldObj = ZUtils.getWorld(DimensionManager.spaceId);
		chunk.pasteInWorld(worldObj, spawnLocation.x - chunk.getSizeX()/2, spawnLocation.y - chunk.getSizeY()/2, spawnLocation.z - chunk.getSizeZ()/2);

	}

	@Override
	public void writeToNbt(CompoundNBT nbt) {
		properties.writeToNBT(nbt);
		nbt.putString("id", getId().toString());
		nbt.putInt("posX", posX);
		nbt.putInt("posY", posY);
		nbt.putInt("alitude", altitude);
		nbt.putInt("spawnX", spawnLocation.x);
		nbt.putInt("spawnY", spawnLocation.y);
		nbt.putInt("spawnZ", spawnLocation.z);
		nbt.putDouble("rotationX", rotation[0]);
		nbt.putDouble("rotationY", rotation[1]);
		nbt.putDouble("rotationZ", rotation[2]);
		nbt.putDouble("deltaRotationX", angularVelocity[0]);
		nbt.putDouble("deltaRotationY", angularVelocity[1]);
		nbt.putDouble("deltaRotationZ", angularVelocity[2]);
	}

	@Override
	public void readFromNbt(CompoundNBT nbt) {
		properties.readFromNBT(nbt);

		posX = nbt.getInt("posX");
		posY = nbt.getInt("posY");
		altitude = nbt.getInt("altitude");
		spawnLocation = new HashedBlockPosition(nbt.getInt("spawnX"), nbt.getInt("spawnY"), nbt.getInt("spawnZ"));
		properties.setId(new ResourceLocation(nbt.getString("id")));
		rotation[0] = nbt.getDouble("rotationX");
		rotation[1] = nbt.getDouble("rotationY");
		rotation[2] = nbt.getDouble("rotationZ");
		angularVelocity[0] = nbt.getDouble("deltaRotationX");
		angularVelocity[1] = nbt.getDouble("deltaRotationY");
		angularVelocity[2] = nbt.getDouble("deltaRotationZ");
	}

	/**
	 * True if the spawn location for this space object is not the default one assigned to it
	 * @return
	 */
	@Override
	public boolean hasCustomSpawnLocation() {
		return false;
	}

	@Override
	public boolean hasFreeLandingPad() {
		return false;
	}

	@Override
	public HashedBlockPosition getNextLandingPad(boolean commit) {
		return null;
	}

	@Override
	public void addLandingPad(int x, int z, String name) {
		
	}

	@Override
	public void removeLandingPad(int x, int z) {
		
	}

	@Override
	public void setPadStatus(int posX, int posZ, boolean full) {
		
	}
	

}
