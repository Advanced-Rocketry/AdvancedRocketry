package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.GravityHandler;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.inventory.modules.IGuiCallback;
import zmaster587.libVulpes.inventory.modules.ISliderBar;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleBlockSideSelector;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleRedstoneOutputButton;
import zmaster587.libVulpes.inventory.modules.ModuleSlider;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.inventory.modules.ModuleToggleSwitch;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

public class TileGravityController extends TileMultiPowerConsumer implements ISliderBar, IGuiCallback {

	int gravity;
	int progress;
	int radius;
	float currentProgress;
	double rotation;

	private ModuleRedstoneOutputButton redstoneControl;
	private RedstoneState state;
	private ModuleText targetGrav, textRadius;
	private ModuleBlockSideSelector sideSelectorModule;

	private static final Object[][][] structure = {
		{{null, null, null},
			{null, 'c', null},
			{null, null, null}},
			{{null, LibVulpesBlocks.blockAdvStructureBlock, null},
				{LibVulpesBlocks.blockAdvStructureBlock, 'P', LibVulpesBlocks.blockAdvStructureBlock},
				{null, LibVulpesBlocks.blockAdvStructureBlock, null}}
	};

	public TileGravityController() {
		//numGravPylons = new ModuleText(10, 25, "Number Of Thrusters: ", 0xaa2020);
		textRadius = new ModuleText(6, 82, "Radius: 5", 0x202020);
		targetGrav = new ModuleText(6, 110, "Target Gravity:", 0x202020);
		sideSelectorModule = new ModuleBlockSideSelector(90, 15, this, new String[] {"None", "Active: set", "Active: Additive"});

		redstoneControl = new ModuleRedstoneOutputButton(174, 4, 1, "", this);
		state = RedstoneState.OFF;
		redstoneControl.setRedstoneState(state);
		radius = 5;
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();//super.getModules(id, player);
		modules.add(toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, 11, 26, getMachineEnabled()));
		modules.add(new ModulePower(18, 20, getBatteries()));
		modules.add(sideSelectorModule);

		modules.add(redstoneControl);


		modules.add(new ModuleSlider(6, 120, 0, TextureResources.doubleWarningSideBarIndicator, (ISliderBar)this));
		modules.add(new ModuleSlider(6, 90, 1, TextureResources.doubleWarningSideBarIndicator, (ISliderBar)this));

		modules.add(new ModuleText(42, 20, "Target->\nDirection", 0x202020));
		modules.add(targetGrav);
		modules.add(textRadius);
		updateText();
		return modules;
	}

	public int getRadius() {
		return radius + 10;
	}
	
	protected boolean isStateActive(RedstoneState state, boolean condition) {
		if(state == RedstoneState.INVERTED)
			return !condition;
		else if(state == RedstoneState.OFF)
			return true;
		return condition;
	}

	public double getArmRotation() {
		rotation = (rotation + 10f*currentProgress) % 360f;
		return rotation;
	}

	public double getGravityMultiplier() {
		return currentProgress/2f;
	}

	private void updateText() {
		if(worldObj.isRemote) {
			textRadius.setText(String.format("Radius: %d", getRadius()));

			targetGrav.setText(String.format("Target Gravity: %.2f/%d",currentProgress, gravity));
		}
	}

	@Override
	public String getMachineName() {
		return getModularInventoryName();
	}

	@Override
	public void updateEntity() {

		//if(this.worldObj.provider instanceof WorldProviderSpace) {

		if(getMachineEnabled() && isStateActive(state, worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) > 0)) {
			if(!worldObj.isRemote) {
				//ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

				if(gravity == 0)
					gravity = 15;
				double targetGravity = gravity/100D;
				double angVel = currentProgress;
				double acc = 0.001;

				double difference = targetGravity - angVel;

				if(difference != 0) {
					double finalVel = angVel;
					if(difference < 0) {
						finalVel = angVel + Math.max(difference, -acc);
					}
					else if(difference > 0) {
						finalVel = angVel + Math.min(difference, acc);
					}

					currentProgress = (float)finalVel;
					markDirty();
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}

			}
			else
				updateText();

			List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, net.minecraft.util.AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(getRadius(), getRadius() , getRadius()));



			for(Entity e : entities) {
				boolean additive = true;
				boolean allowApply = false;
				e.fallDistance = 0;

				for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
					if(!(e instanceof EntityPlayer) || !((EntityPlayer)e).capabilities.isFlying) {

						if(sideSelectorModule.getStateForSide(dir) != 0) {
							allowApply = true;
							if(sideSelectorModule.getStateForSide(dir)  == 1)
								additive = false;

							if(e instanceof EntityLivingBase) {
								{
									e.motionX += dir.offsetX*GravityHandler.ENTITY_OFFSET*currentProgress;
									e.motionY += dir.offsetY*GravityHandler.ENTITY_OFFSET*currentProgress;
									e.motionZ += dir.offsetZ*GravityHandler.ENTITY_OFFSET*currentProgress;
								}
							}
							else if (e instanceof EntityItem || e instanceof EntityArrow) {
								e.motionX += dir.offsetX*GravityHandler.ITEM_GRAV_OFFSET*currentProgress;
								e.motionY += dir.offsetY*GravityHandler.ITEM_GRAV_OFFSET*currentProgress;
								e.motionZ += dir.offsetZ*GravityHandler.ITEM_GRAV_OFFSET*currentProgress;
							}
							
							if(worldObj.isRemote) {
								if(Minecraft.getMinecraft().gameSettings.particleSetting == 0 && !(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && Minecraft.getMinecraft().thePlayer == e))
								AdvancedRocketry.proxy.spawnParticle("gravityEffect", worldObj, e.posX, e.posY, e.posZ, .2f*dir.offsetX*currentProgress, .2f*dir.offsetY*currentProgress, .2f*dir.offsetZ*currentProgress);
							}
						}
					}
				}

				//Only apply gravity if none of the directions are set and it's not a player in flight
				if(allowApply && !additive)
					e.motionY += (e instanceof EntityItem || e instanceof EntityArrow) ? GravityHandler.ITEM_GRAV_OFFSET :  GravityHandler.ENTITY_OFFSET + 0.005;
			}
		}
		else if (currentProgress > 0) {
			currentProgress -= 0.01f;
			if(!worldObj.isRemote) {
				markDirty();
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			else
				updateText();
		}
		else
			currentProgress = 0;
		//}
	}

	@Override
	public boolean getMachineEnabled() {
		return super.getMachineEnabled();
	}

	@Override
	public String getModularInventoryName() {
		return "tile.gravityMachine.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		super.writeDataToNetwork(out, id);
		if(id == 3) {
			out.writeShort(progress);
			out.writeShort(radius);
		}
		else if(id == 4) {
			for(int i = 0; i < 6; i++)
				out.writeByte(sideSelectorModule.getStateForSide(i));
		}
		else if(id == 5) {
			out.writeByte(state.ordinal());
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		super.readDataFromNetwork(in, packetId, nbt);
		if(packetId == 3) {
			nbt.setShort("progress",  in.readShort());
			nbt.setShort("radius", in.readShort());
		}
		else if(packetId == 4) {
			byte bytes[] = new byte[6];
			for(int i = 0; i < 6; i++)
				bytes[i] = in.readByte();
			nbt.setByteArray("bytes", bytes);
		}
		else if(packetId == 5) {
			nbt.setByte("redstoneState", in.readByte());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		super.useNetworkData(player, side, id, nbt);

		if(id == 3) {
			setProgress(0, nbt.getShort("progress"));
			setProgress(1, nbt.getShort("radius"));
		}
		else if(id == 4) {
			byte bytes[] = nbt.getByteArray("bytes");
			for(int i = 0; i < 6; i++)
				sideSelectorModule.setStateForSide(i, bytes[i]);
		}
		else if(id == 5) {
			state = RedstoneState.values()[nbt.getByte("redstoneState")];
			redstoneControl.setRedstoneState(state);
		}
	}


	@Override
	protected void writeNetworkData(NBTTagCompound nbt) {
		super.writeNetworkData(nbt);
		nbt.setShort("gravity", (short)gravity);
		nbt.setFloat("currGravity", currentProgress);
		nbt.setByte("redstoneState", (byte) state.ordinal());
		nbt.setShort("radius", (short)radius);
		sideSelectorModule.writeToNBT(nbt);
	}

	@Override
	protected void readNetworkData(NBTTagCompound nbt) {
		super.readNetworkData(nbt);
		gravity = nbt.getShort("gravity");
		currentProgress = nbt.getFloat("currGravity");
		progress = gravity -5;
		radius = nbt.getShort("radius");
		sideSelectorModule.readFromNBT(nbt);
		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);

	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		super.onInventoryButtonPressed(buttonId);
		if(buttonId == 1) {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)5));
		}
	}

	@Override
	public float getNormallizedProgress(int id) {
		return getProgress(id)/(float)getTotalProgress(id);
	}

	@Override
	public void setProgress(int id, int progress) {

		if(id == 0) {
			this.progress = progress;
			gravity = progress + 5;
		}
		else
			radius = progress;
	}

	@Override
	public int getProgress(int id) {
		if(id == 0)
			return this.progress;
		else
			return radius;
	}

	@Override
	public int getTotalProgress(int id) {
		if(id == 0)
			return 190;
		else
			return 22;
	}

	@Override
	public void setTotalProgress(int id, int progress) {

	}

	@Override
	public void setProgressByUser(int id, int progress) {
		setProgress(id, progress);
		PacketHandler.sendToServer(new PacketMachine(this, (byte)3));
	}

	@Override
	public void onModuleUpdated(ModuleBase module) {
		PacketHandler.sendToServer(new PacketMachine(this, (byte)4));
	}

}
