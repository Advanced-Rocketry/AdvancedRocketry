package zmaster587.advancedRocketry.tile.station;

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
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils;

import java.util.LinkedList;
import java.util.List;

public class TileStationAltitudeController extends TileEntity implements IModularInventory, ITickableTileEntity, INetworkMachine, ISliderBar {

	int progress;

	private ModuleText moduleGrav, numGravPylons, maxGravBuildSpeed, targetGrav;

	public TileStationAltitudeController() {
		super(AdvancedRocketryTileEntityType.TILE_ALT_CONTROLLER);
		moduleGrav = new ModuleText(6, 15, "Altitude: ", 0xaa2020);
		//numGravPylons = new ModuleText(10, 25, "Number Of Thrusters: ", 0xaa2020);
		maxGravBuildSpeed = new ModuleText(6, 25, LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.maxaltrate"), 0xaa2020);
		targetGrav = new ModuleText(6, 35, LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.tgtalt"), 0x202020);
	}

	@Override
	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(moduleGrav);
		//modules.add(numThrusters);
		modules.add(maxGravBuildSpeed);

		modules.add(targetGrav);
		modules.add(new ModuleSlider(6, 60, 0, TextureResources.doubleWarningSideBarIndicator, (ISliderBar)this));

		updateText();
		return modules;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = write(new CompoundNBT());
		

		SUpdateTileEntityPacket packet = new SUpdateTileEntityPacket(pos, 0, nbt);
		return packet;
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
	}
	
	private void updateText() {
		if(world.isRemote) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(object != null) {
				moduleGrav.setText(String.format("%s %.0fKm",LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.alt"), object.getOrbitalDistance()*200 + 100 ));
				maxGravBuildSpeed.setText(String.format("%s%.1f", LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.maxaltrate"), 7200D*object.getMaxRotationalAcceleration()));
			}

			//numThrusters.setText("Number Of Thrusters: 0");

			targetGrav.setText(String.format("%s %d", LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.tgtalt"), ((SpaceStationObject) object).targetOrbitalDistance * 200 + 100));
		}
	}

	@Override
	public void tick() {
		if(ARConfiguration.GetSpaceDimId().equals(ZUtils.getDimensionIdentifier(this.world))) {

			if(!world.isRemote) {
				ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

				if(object != null) {
					progress = ((SpaceStationObject) object).targetOrbitalDistance;

					double targetGravity = ((SpaceStationObject) object).targetOrbitalDistance;
					double angVel = object.getOrbitalDistance();
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

						object.setOrbitalDistance((float)finalVel);
						if(!world.isRemote) {
							//PacketHandler.sendToNearby(new PacketStationUpdate(object, PacketStationUpdate.Type.ROTANGLE_UPDATE), this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 1024);
							PacketHandler.sendToAll(new PacketStationUpdate(object, PacketStationUpdate.Type.ALTITUDE_UPDATE));
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
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		if(id == 0) {
			out.writeShort(progress);
		}
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		if(packetId == 0) {
			setProgress(0, in.readShort());
		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {

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
