package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.IComparatorOverride;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import java.util.LinkedList;
import java.util.List;

public class TileStationAltitudeController extends TileEntity implements IModularInventory, ITickable, INetworkMachine, ISliderBar, IButtonInventory, IComparatorOverride {

	int progress;
	private RedstoneState state;

	private ModuleText moduleGrav, numGravPylons, maxGravBuildSpeed, targetGrav;
	private ModuleRedstoneOutputButton redstoneControl;

	public TileStationAltitudeController() {
		moduleGrav = new ModuleText(6, 15, "Altitude: ", 0xaa2020);
		//numGravPylons = new ModuleText(10, 25, "Number Of Thrusters: ", 0xaa2020);
		maxGravBuildSpeed = new ModuleText(6, 25, LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.maxaltrate"), 0xaa2020);
		targetGrav = new ModuleText(6, 35, LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.tgtalt"), 0x202020);
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, -1, "", this);
	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
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
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.getNbtCompound());
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
	public void update() {
		if(this.world.provider instanceof WorldProviderSpace) {

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
		return "tile.altitudeController.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 0) {
			out.writeShort(progress);
		} else if(id == 2)
			out.writeByte(state.ordinal());
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == 0) {
			setProgress(0, in.readShort());
		} else if(packetId == 2) {
			nbt.setByte("state", in.readByte());
		}
	}

	@Override
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
		if(this.world.provider instanceof WorldProviderSpace) {
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
}
