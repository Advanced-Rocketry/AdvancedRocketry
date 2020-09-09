package zmaster587.advancedRocketry.entity;

import java.util.LinkedList;
import java.util.ListIterator;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryEntities;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.RocketEvent;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.client.SoundRocketEngine;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityRocket.PacketType;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketSpawnEntity;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.Vector3F;

public class EntityHoverCraft extends Entity implements IInventory, INetworkEntity {


	public enum VehicleType {
		submarine,
		blimp
	}

	protected double speedMultiplier;
	public boolean cruiseControl;
	protected float fanLoc;
	protected int freshBurnTime;
	protected int currentBurnTime;
	//Used to calculate rendering stuffs
	protected EmbeddedInventory inv;
	private boolean turningLeft, turningRight, turningUp, turningDownforWhat;

	public EntityHoverCraft(EntityType<?> type,  World par1World)
	{
		super( AdvancedRocketryEntities.ENTITY_HOVER_CRAFT, par1World);
		inv = new EmbeddedInventory(1);
	}

	public EntityHoverCraft(World par1World, double par2, double par4, double par6)
	{
		this(AdvancedRocketryEntities.ENTITY_HOVER_CRAFT, par1World);

		//System.out.println(localBoundingBox);

		this.setPosition(par2, par4 + (double)this.getYOffset(), par6);
		this.prevPosX = par2;
		this.prevPosY = par4;
		this.prevPosZ = par6;
		inv = new EmbeddedInventory(1);
	}


	@Override
	public double getYOffset() {
		return 0;
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	public boolean canTriggerWalking()
	{
		return false;
	}


	@Override
	public void markDirty() {

	}

	public void updateRiderPosition()
	{
		if (!this.getPassengers().isEmpty())
		{
			
		}
	}

	/**
	 * Returns the Y offset from the entity's position for any entity riding this one.
	 */
	public double getMountedYOffset()
	{
		return (double)this.getHeight() * 0.0D + 0.5D;
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities when colliding.
	 */
	public boolean canBePushed()
	{
		return true;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	public boolean canBeCollidedWith()
	{
		return this.isAlive();
	}
	
	/**
	 * First layer of player interaction
	 */
	@Override
	public ActionResultType processInitialInteract(PlayerEntity player, Hand hand)
	{
		if(this.getPassengers().isEmpty()/* || (this.riddenByEntity != null && this.riddenByEntity instanceof PlayerEntity && this.riddenByEntity != player)*/) {
			if (!this.world.isRemote)
				player.startRiding(this);
		}
		return ActionResultType.SUCCESS;
	}
	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
	{
		if(!this.world.isRemote && this.isAlive() && par1DamageSource.getImmediateSource() instanceof PlayerEntity && !this.getPassengers().contains(par1DamageSource.getImmediateSource()))
		{
			for(ItemStack i : getItemsDropOnDeath())
			{
				if(i != null)
					this.entityDropItem(i, 0.0F);
			}

			this.remove();
			return true;
		}
		return false;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return super.getRenderBoundingBox();
	}
	
	public ItemStack[] getItemsDropOnDeath()
	{
		ItemStack[] stack = { inv.getStackInSlot(0), new ItemStack(AdvancedRocketryItems.itemHovercraft) };
		return stack;
	}

	public float getMaxHeight()
	{
		return 250;
	}

	public double getMaxVelocity()
	{
		return 0.75;
	}

	public double getMaxVerticalSpeed()
	{
		return 0.1D;
	}


	public double getMaxAcceleration() {
		// TODO Auto-generated method stub
		return 0.05D;
	}

	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		return inv.decrStackSize(slot, amt);
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inv.getStackInSlot(i);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		inv.setInventorySlotContents(slot, itemstack);
	}
	
	public void onTurnRight(boolean state) {
		turningRight = state;
		PacketHandler.sendToServer(new PacketEntity((INetworkEntity) this, (byte)EntityRocket.PacketType.TURNUPDATE.ordinal()));
	}
	
	public void onTurnLeft(boolean state) {
		turningLeft = state;
		PacketHandler.sendToServer(new PacketEntity((INetworkEntity) this, (byte)EntityRocket.PacketType.TURNUPDATE.ordinal()));
	}
	
	public void onUp(boolean state) {
		turningUp = state;
		PacketHandler.sendToServer(new PacketEntity((INetworkEntity) this, (byte)EntityRocket.PacketType.TURNUPDATE.ordinal()));
	}
	
	public void onDown(boolean state) {
		turningDownforWhat = state;
		PacketHandler.sendToServer(new PacketEntity((INetworkEntity) this, (byte)EntityRocket.PacketType.TURNUPDATE.ordinal()));
	}

	@Override
	public void tick() {
		super.tick();

		if(this.getPassengers().isEmpty())
			this.turningDownforWhat = true;
		
		this.rotationYaw += (turningRight ? 5 : 0) - (turningLeft ? 5 : 0);
		double acc = this.getPassengerMovingForward()*getMaxAcceleration();
		//RCS mode, steer like boat
		float yawAngle = (float)(this.rotationYaw*Math.PI/180f);
		Vector3d motion = getMotion();
		
		float friction = 0.9f;
		float motionYMult = 1f;
		
		if (this.getPosY() > getMaxHeight()*1.1)
			motionYMult = 0;
		else if (this.getPosY() > getMaxHeight())
			motionYMult *= 0.1;
		
		
		Vector3d newMotion = new Vector3d(friction*(motion.x + acc*MathHelper.sin(-yawAngle)), 
				motionYMult*friction*( motion.y + (turningUp ? getMaxAcceleration() : 0) - (turningDownforWhat ? getMaxAcceleration() : 0)),
				friction*(motion.z + acc*MathHelper.cos(-yawAngle)));

		this.setMotion(newMotion);
		

		
		this.move(MoverType.SELF, this.getMotion());

	}
	
	public float getPassengerMovingForward() {

		for(Entity entity : this.getPassengers()) {
			if(entity instanceof PlayerEntity) {
				return ((PlayerEntity) entity).moveForward;
			}
		}
		return 0f;
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		if(packetId == PacketType.TURNUPDATE.ordinal()) {
			nbt.putBoolean("left", in.readBoolean());
			nbt.putBoolean("right", in.readBoolean());
			nbt.putBoolean("up", in.readBoolean());
			nbt.putBoolean("down", in.readBoolean());
		}
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		if(id == PacketType.TURNUPDATE.ordinal()) {
			out.writeBoolean(turningLeft);
			out.writeBoolean(turningRight);
			out.writeBoolean(turningUp);
			out.writeBoolean(turningDownforWhat);
		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {

		if(id == PacketType.TURNUPDATE.ordinal()) {
			this.turningLeft = nbt.getBoolean("left");
			this.turningRight = nbt.getBoolean("right");
			this.turningUp = nbt.getBoolean("up");
			this.turningDownforWhat = nbt.getBoolean("down");
		}
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
	}

	@Override
	public int getInventoryStackLimit() {
		return inv.getInventoryStackLimit();
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return false;
	}

	@Override
	public void openInventory(PlayerEntity player) {
		inv.openInventory(player);
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		inv.closeInventory(player);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return inv.isItemValidForSlot(index, stack);
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	protected void registerData() {
	}
	
	@Override
	protected void readAdditional(CompoundNBT compound) {
		inv.readFromNBT(compound);
	}
	

	@Override
	protected void writeAdditional(CompoundNBT compound) {
		inv.write(compound);
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return new PacketSpawnEntity(this);
	}
}
