package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.GravityHandler;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.inventory.modules.IGuiCallback;
import zmaster587.libVulpes.inventory.modules.ISliderBar;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleBlockSideSelector;
import zmaster587.libVulpes.inventory.modules.ModuleRedstoneOutputButton;
import zmaster587.libVulpes.inventory.modules.ModuleSlider;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

public class TileGravityController extends TileMultiPowerConsumer implements ISliderBar, IGuiCallback {

	int gravity;
	int progress;
	float currentProgress;
	double rotation;

	private ModuleRedstoneOutputButton redstoneControl;
	private RedstoneState state;
	private ModuleText moduleGrav, maxGravBuildSpeed, targetGrav;
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
		moduleGrav = new ModuleText(6, 95, "Artifical Gravity: ", 0xaa2020);
		//numGravPylons = new ModuleText(10, 25, "Number Of Thrusters: ", 0xaa2020);
		maxGravBuildSpeed = new ModuleText(6, 85, "Max Gravity Change Rate: ", 0xaa2020);
		targetGrav = new ModuleText(6, 105, "Target Gravity:", 0x202020);
		sideSelectorModule = new ModuleBlockSideSelector(90, 15, this, new String[] {"None", "Active: set", "Active: Additive"});

		redstoneControl = new ModuleRedstoneOutputButton(174, 4, 1, "", this);
		state = RedstoneState.OFF;
		redstoneControl.setRedstoneState(state);
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(id, player);
		modules.add(sideSelectorModule);
		modules.add(moduleGrav);
		modules.add(redstoneControl);

		modules.add(targetGrav);
		modules.add(new ModuleSlider(6, 120, 0, TextureResources.doubleWarningSideBarIndicator, (ISliderBar)this));

		updateText();
		return modules;
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
			moduleGrav.setText(String.format("Artifical Gravity: %.2f", currentProgress));
			maxGravBuildSpeed.setText(String.format("Max Gravity Change Rate: %.1f", 1D));

			targetGrav.setText(String.format("Target Gravity: %d", gravity));
		}
	}

	@Override
	public String getMachineName() {
		return getModularInventoryName();
	}
	
	@Override
	public void update() {

		//if(this.worldObj.provider instanceof WorldProviderSpace) {

		if(getMachineEnabled() && isStateActive(state, worldObj.isBlockIndirectlyGettingPowered(getPos()) > 0)) {
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
					worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos),  worldObj.getBlockState(pos), 2);
				}

			}
			else
				updateText();

			List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(getPos()).expand(32, 32, 32));



			for(Entity e : entities) {
				boolean additive = true;
				boolean allowApply = false;

				for(EnumFacing dir : EnumFacing.VALUES) {
					if(!(e instanceof EntityPlayer) || !((EntityPlayer)e).capabilities.isFlying) {
						
						if(sideSelectorModule.getStateForSide(dir) != 0) {
							allowApply = true;
							if(sideSelectorModule.getStateForSide(dir)  == 1)
								additive = false;

							if(e instanceof EntityLivingBase) {
								{
									e.motionX += dir.getFrontOffsetX()*GravityHandler.ENTITY_OFFSET*currentProgress;
									e.motionY += dir.getFrontOffsetY()*GravityHandler.ENTITY_OFFSET*currentProgress;
									e.motionZ += dir.getFrontOffsetZ()*GravityHandler.ENTITY_OFFSET*currentProgress;
								}
							}
							else if (e instanceof EntityItem || e instanceof EntityArrow) {
								e.motionX += dir.getFrontOffsetX()*GravityHandler.ITEM_GRAV_OFFSET*currentProgress;
								e.motionY += dir.getFrontOffsetY()*GravityHandler.ITEM_GRAV_OFFSET*currentProgress;
								e.motionZ += dir.getFrontOffsetZ()*GravityHandler.ITEM_GRAV_OFFSET*currentProgress;
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
		sideSelectorModule.writeToNBT(nbt);
	}

	@Override
	protected void readNetworkData(NBTTagCompound nbt) {
		super.readNetworkData(nbt);
		gravity = nbt.getShort("gravity");
		currentProgress = nbt.getFloat("currGravity");
		progress = gravity -5;
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
		return getProgress(0)/(float)getTotalProgress(0);
	}

	@Override
	public void setProgress(int id, int progress) {

		this.progress = progress;
		gravity = progress + 5;
	}

	@Override
	public int getProgress(int id) {
		return this.progress;
	}

	@Override
	public int getTotalProgress(int id) {
		return 190;
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
