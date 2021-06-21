package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.IComparatorOverride;
import zmaster587.libVulpes.util.INetworkMachine;
<<<<<<< HEAD
import zmaster587.libVulpes.util.ZUtils;
=======
import zmaster587.libVulpes.util.ZUtils.RedstoneState;
>>>>>>> origin/feature/nuclearthermalrockets

import java.util.LinkedList;
import java.util.List;

<<<<<<< HEAD
public class TileStationGravityController extends TileEntity implements IModularInventory, ITickableTileEntity, INetworkMachine, ISliderBar {
=======
public class TileStationGravityController extends TileEntity implements IModularInventory, ITickable, INetworkMachine, ISliderBar, IButtonInventory, IComparatorOverride {
>>>>>>> origin/feature/nuclearthermalrockets

	private int progress;
	private RedstoneState state;

	private static int minGravity = 10;

	private ModuleText moduleGrav, maxGravBuildSpeed, targetGrav;
	private ModuleRedstoneOutputButton redstoneControl;

	public TileStationGravityController() {
		super(AdvancedRocketryTileEntityType.TILE_STATION_GRAVITY_CONTROLLER);
		moduleGrav = new ModuleText(6, 15, LibVulpes.proxy.getLocalizedString("msg.stationgravctrl.alt"), 0xaa2020);
		//numGravPylons = new ModuleText(10, 25, "Number Of Thrusters: ", 0xaa2020);
		maxGravBuildSpeed = new ModuleText(6, 25, LibVulpes.proxy.getLocalizedString("msg.stationgravctrl.maxaltrate"), 0xaa2020);
		targetGrav = new ModuleText(6, 35, LibVulpes.proxy.getLocalizedString("msg.stationgravctrl.tgtalt"), 0x202020);
<<<<<<< HEAD
		
		minGravity = ARConfiguration.getCurrentConfig().allowZeroGSpacestations.get() ? 0 : 10;
=======

		redstoneControl = new ModuleRedstoneOutputButton(174, 4, -1, "", this);

		minGravity = ARConfiguration.getCurrentConfig().allowZeroGSpacestations ? 0 : 10;
>>>>>>> origin/feature/nuclearthermalrockets
	}

	public static int getMinGravity() {
		return minGravity;
	}

	@Override
<<<<<<< HEAD
	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
=======
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<>();
>>>>>>> origin/feature/nuclearthermalrockets
		modules.add(moduleGrav);
		//modules.add(numThrusters);
		modules.add(maxGravBuildSpeed);
		modules.add(redstoneControl);

		modules.add(targetGrav);
		modules.add(new ModuleSlider(6, 60, 0, TextureResources.doubleWarningSideBarIndicator, this));

		updateText();
		return modules;
	}

	@Override
<<<<<<< HEAD
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = write(new CompoundNBT());

		SUpdateTileEntityPacket packet = new SUpdateTileEntityPacket(pos, 0, nbt);
		return packet;
=======
	public void onInventoryButtonPressed(int buttonId) {
		if(buttonId != -1)
			PacketHandler.sendToServer(new PacketMachine(this, (byte) (buttonId + 100)) );
		else {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)2));
			markDirty();
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = writeToNBT(new NBTTagCompound());

		return new SPacketUpdateTileEntity(pos, 0, nbt);
>>>>>>> origin/feature/nuclearthermalrockets
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
<<<<<<< HEAD
=======
        readFromNBT(pkt.getNbtCompound());
>>>>>>> origin/feature/nuclearthermalrockets
	}
	
	private void updateText() {
		if(world.isRemote) {
			ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(spaceObject != null) {
				moduleGrav.setText(String.format("%s%.2f", LibVulpes.proxy.getLocalizedString("msg.stationgravctrl.alt"), spaceObject.getProperties().getGravitationalMultiplier()));
				maxGravBuildSpeed.setText(String.format("%s%.1f",LibVulpes.proxy.getLocalizedString("msg.stationgravctrl.maxaltrate"), 7200D*spaceObject.getMaxRotationalAcceleration()));
			}

			//numThrusters.setText("Number Of Thrusters: 0");

			targetGrav.setText(String.format("%s%d", LibVulpes.proxy.getLocalizedString("msg.stationgravctrl.tgtalt"), ((SpaceStationObject) spaceObject).targetGravity));
		}
	}

	@Override
	public void tick() {

		if(ARConfiguration.GetSpaceDimId().equals(ZUtils.getDimensionIdentifier(this.world))) {

			if(!world.isRemote) {
				ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

				if(spaceObject != null) {
					if (redstoneControl.getState() == RedstoneState.ON)
						((SpaceStationObject) spaceObject).targetGravity = (world.getStrongPower(pos) * 6) + 10;
					else if (redstoneControl.getState() == RedstoneState.INVERTED)
						((SpaceStationObject) spaceObject).targetGravity = Math.abs(15 - world.getStrongPower(pos)) * 6 + 10;

					progress = ((SpaceStationObject) spaceObject).targetGravity - minGravity;

<<<<<<< HEAD
					int targetMultiplier = (ARConfiguration.getCurrentConfig().allowZeroGSpacestations.get()) ? ((SpaceStationObject) object).targetGravity : Math.max(11, ((SpaceStationObject) object).targetGravity);
=======
					int targetMultiplier = (ARConfiguration.getCurrentConfig().allowZeroGSpacestations) ? ((SpaceStationObject) spaceObject).targetGravity : Math.max(10, ((SpaceStationObject) spaceObject).targetGravity);
>>>>>>> origin/feature/nuclearthermalrockets
					double targetGravity = targetMultiplier/100D;
					double angVel = spaceObject.getProperties().getGravitationalMultiplier();
					double acc = 0.001;

					double difference = targetGravity - angVel;

					if(Math.abs(difference) >= 0.001) {
						double finalVel = angVel;
						if(difference < 0) {
							finalVel = angVel + Math.max(difference, -acc);
						}
						else if(difference > 0) {
							finalVel = angVel + Math.min(difference, acc);
						}

						spaceObject.getProperties().setGravitationalMultiplier((float)finalVel);
						if(!world.isRemote) {
							//PacketHandler.sendToNearby(new PacketStationUpdate(spaceObject, PacketStationUpdate.Type.ROTANGLE_UPDATE), this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 1024);
							PacketHandler.sendToAll(new PacketStationUpdate(spaceObject, PacketStationUpdate.Type.DIM_PROPERTY_UPDATE));
							markDirty();
						}
						else
							updateText();
					}
				}
			}
			else
				updateText();
		}
	}
	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.gravitycontroller";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
<<<<<<< HEAD
	public void writeDataToNetwork(PacketBuffer out, byte id) {
=======
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
>>>>>>> origin/feature/nuclearthermalrockets
		if(id == 0) {
			out.writeShort(progress);
		} else if(id == 2)
			out.writeByte(state.ordinal());
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		if(packetId == 0) {
			setProgress(0, in.readShort());
		} else if(packetId == 2) {
			nbt.setByte("state", in.readByte());
		}
	}

	@Override
<<<<<<< HEAD
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {

=======
	public void useNetworkData(EntityPlayer player, Side side, byte id, NBTTagCompound nbt) {
		if(id == 2) {
			state = RedstoneState.values()[nbt.getByte("state")];
			redstoneControl.setRedstoneState(state);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("redstoneState", (byte) state.ordinal());
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);
>>>>>>> origin/feature/nuclearthermalrockets
	}


	@Override
	public float getNormallizedProgress(int id) {
		return getProgress(0)/(float)getTotalProgress(0);
	}

	@Override
	public void setProgress(int id, int progress) {

		this.progress = progress;
		if (SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos) != null) {
			((SpaceStationObject) (SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos))).targetGravity = progress + minGravity;
		}
	}

	@Override
	public int getProgress(int id) {
		return this.progress;
	}

	@Override
	public int getTotalProgress(int id) {
		return 100 - minGravity;
	}

	@Override
	public void setTotalProgress(int id, int progress) {

	}

	@Override
	public int getComparatorOverride() {
		if(this.world.provider instanceof WorldProviderSpace) {
			if (!world.isRemote) {
				ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
				if (spaceObject != null) {
					return (int)((((SpaceStationObject)spaceObject).getProperties().getGravitationalMultiplier() - 0.1)/0.059);
				}
			}
		}
		return 0;
	}

	@Override
	public void setProgressByUser(int id, int progress) {
		setProgress(id, progress);
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, getModules(getModularInvType().ordinal(), player), this, getModularInvType());
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return guiId.MODULAR;
	}
}
