package zmaster587.advancedRocketry.tile.station;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
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
import zmaster587.advancedRocketry.dimension.DimensionManager;
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
import zmaster587.libVulpes.util.ZUtils;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;

public class TileStationAltitudeController extends TileEntity implements IModularInventory, ITickableTileEntity, INetworkMachine, ISliderBar, IButtonInventory, IComparatorOverride {

	int progress;
	private RedstoneState state = ZUtils.RedstoneState.OFF;

	private ModuleText moduleGrav, maxGravBuildSpeed, targetGrav;
	private ModuleRedstoneOutputButton redstoneControl;

	public TileStationAltitudeController() {
		super(AdvancedRocketryTileEntityType.TILE_ALT_CONTROLLER);
		moduleGrav = new ModuleText(6, 15, "Altitude: ", 0xaa2020);
		//numGravPylons = new ModuleText(10, 25, "Number Of Thrusters: ", 0xaa2020);
		maxGravBuildSpeed = new ModuleText(6, 25, LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.maxaltrate"), 0xaa2020);
		targetGrav = new ModuleText(6, 35, LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.tgtalt"), 0x202020);
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, "", this);
	}

	@Override
	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<>();
		modules.add(moduleGrav);
		//modules.add(numThrusters);
		modules.add(maxGravBuildSpeed);

		modules.add(targetGrav);
		modules.add(new ModuleSlider(6, 60, 0, TextureResources.doubleWarningSideBarIndicator, this));
		modules.add(redstoneControl);

		updateText();
		return modules;
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		if(buttonId == redstoneControl) {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)2));
		}
		else
			PacketHandler.sendToServer(new PacketMachine(this, (byte)100) );
	}
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = write(new CompoundNBT());


		return new SUpdateTileEntityPacket(pos, 0, nbt);
	}

	@Nonnull
	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	@Override
	@ParametersAreNonnullByDefault
	public void read(BlockState blkstate, CompoundNBT nbt) {
		super.read(blkstate, nbt);

		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		nbt.putByte("redstoneState", (byte) state.ordinal());
		return nbt;
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		 if(id == 2)
			out.writeByte(state.ordinal());
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
									CompoundNBT nbt) {
		if(packetId == 1) {
			nbt.putLong("id", in.readLong());
		}
		else if(packetId == 2) {
			nbt.putByte("state", in.readByte());
		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
							   CompoundNBT nbt) {
		if(id == 2) {
			state = RedstoneState.values()[nbt.getByte("state")];
			redstoneControl.setRedstoneState(state);
		}
	}
	
	private void updateText() {
		if(world.isRemote) {
			ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(spaceObject != null) {
				moduleGrav.setText(String.format("%s %.0fKm",LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.alt"), spaceObject.getOrbitalDistance()*200 + 100 ));
				maxGravBuildSpeed.setText(String.format("%s%.1f", LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.maxaltrate"), 7200D*spaceObject.getMaxRotationalAcceleration()));
				targetGrav.setText(String.format("%s %d", LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.tgtalt"), ((SpaceStationObject) spaceObject).targetOrbitalDistance * 200 + 100));
			}

			//numThrusters.setText("Number Of Thrusters: 0");
		}
	}

	@Override
	public void tick() {
		if(DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(this.world))) {

			if(!world.isRemote) {
				ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

				if(spaceObject != null) {
					if (redstoneControl.getState() == RedstoneState.ON)
					    ((SpaceStationObject) spaceObject).targetOrbitalDistance = Math.max((world.getStrongPower(pos) * 13) + 4, 190);
					else if (redstoneControl.getState() == RedstoneState.INVERTED)
						((SpaceStationObject) spaceObject).targetOrbitalDistance = Math.max(Math.abs(15 - world.getStrongPower(pos)) * 13 + 4, 190);

					progress = ((SpaceStationObject) spaceObject).targetOrbitalDistance;

					double targetGravity = ((SpaceStationObject) spaceObject).targetOrbitalDistance;
					double angVel = spaceObject.getOrbitalDistance();
					double acc = 0.1*(getTotalProgress(0) - angVel + 1)/(float)getTotalProgress(0);

					double difference = targetGravity - angVel;

					if(difference != 0) {
						double finalVel = angVel;
						if(difference < 0) {
							finalVel = angVel + Math.max(difference, -acc);
						}
						else if(difference > 0) {
							finalVel = angVel + Math.min(difference, acc);
						}

						spaceObject.setOrbitalDistance((float)finalVel);
						if(!world.isRemote) {
							//PacketHandler.sendToNearby(new PacketStationUpdate(spaceObject, PacketStationUpdate.Type.ROTANGLE_UPDATE), this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 1024);
							PacketHandler.sendToAll(new PacketStationUpdate(spaceObject, PacketStationUpdate.Type.ALTITUDE_UPDATE));
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
		return "block.advancedrocketry.altitudecontroller";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}


	@Override
	public float getNormallizedProgress(int id) {
		return getProgress(0)/(float)getTotalProgress(0);
	}

	@Override
	public void setProgress(int id, int progress) {

		this.progress = progress;
		if (SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos) != null) {
			((SpaceStationObject) (SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos))).targetOrbitalDistance = progress;
		}
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
	public int getComparatorOverride() {
		if(DimensionManager.getInstance().isSpaceDimension(world)) {
			if (!world.isRemote) {
				ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
				if (spaceObject != null) {
                    return (int)(spaceObject.getOrbitalDistance() + 5)/13;
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

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	@ParametersAreNonnullByDefault
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, getModules(getModularInvType().ordinal(), player), this, getModularInvType());
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return guiId.MODULAR;
	}
}
