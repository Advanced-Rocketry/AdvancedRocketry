package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IProgressBar;
import zmaster587.libVulpes.inventory.modules.ISliderBar;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleSlider;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.INetworkMachine;

public class TileStationOrientationControl extends TileEntity implements IModularInventory, INetworkMachine, ISliderBar {

	int numRotationsPerHour;
	int progress;

	private ModuleText moduleAngularVelocity, numThrusters, maxAngularAcceleration, targetRotations;

	public TileStationOrientationControl() {
		moduleAngularVelocity = new ModuleText(6, 15, "Angular Velocity: ", 0xaa2020);
		//numThrusters = new ModuleText(10, 25, "Number Of Thrusters: ", 0xaa2020);
		maxAngularAcceleration = new ModuleText(6, 25, "Maximum Angular Acceleration: ", 0xaa2020);
		targetRotations = new ModuleText(6, 35, "Target Ang Vel:", 0x202020);
		progress = getTotalProgress(0)/2;
	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(moduleAngularVelocity);
		//modules.add(numThrusters);
		modules.add(maxAngularAcceleration);

		modules.add(targetRotations);
		modules.add(new ModuleSlider(6, 60, 0, TextureResources.doubleWarningSideBarIndicator, (ISliderBar)this));

		updateText();
		return modules;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	private void updateText() {
		if(worldObj.isRemote) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.xCoord, this.zCoord);
			if(object != null) {
				moduleAngularVelocity.setText(String.format("Angular Velocity: %.1f", 7200D*object.getDeltaRotation()));
				maxAngularAcceleration.setText(String.format("Maximum Angular Acceleration: %.1f", 7200D*object.getMaxRotationalAcceleration()));
			}

			//numThrusters.setText("Number Of Thrusters: 0");

			targetRotations.setText(String.format("Target Ang Vel: %d", numRotationsPerHour));
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if(this.worldObj.provider instanceof WorldProviderSpace) {
			if(!worldObj.isRemote) {
				ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.xCoord, this.zCoord);

				if(object != null) {
					double targetAngularVelocity = numRotationsPerHour/7200D;
					double angVel = object.getDeltaRotation();
					double acc = object.getMaxRotationalAcceleration();

					double difference = targetAngularVelocity - angVel;

					if(difference != 0) {
						double finalVel = angVel;
						if(difference < 0) {
							finalVel = angVel + Math.max(difference, -acc);
						}
						else if(difference > 0) {
							finalVel = angVel + Math.min(difference, acc);
						}

						object.setDeltaRotation(finalVel);
						if(!worldObj.isRemote) {
							//PacketHandler.sendToNearby(new PacketStationUpdate(object, PacketStationUpdate.Type.ROTANGLE_UPDATE), this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 1024);
							PacketHandler.sendToAll(new PacketStationUpdate(object, PacketStationUpdate.Type.ROTANGLE_UPDATE));
						}
					}
				}
				else
					updateText();
			}
		}
	}
	@Override
	public String getModularInventoryName() {
		return "tile.orientationControl.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 0) {
			out.writeShort(progress);
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == 0) {
			setProgress(0, in.readShort());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setShort("numRotations", (short)numRotationsPerHour);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		numRotationsPerHour = nbt.getShort("numRotations");
		progress = numRotationsPerHour + getTotalProgress(0)/2;
	}


	@Override
	public float getNormallizedProgress(int id) {
		return getProgress(0)/(float)getTotalProgress(0);
	}

	@Override
	public void setProgress(int id, int progress) {

		this.progress = progress;
		numRotationsPerHour = 1*(progress - getTotalProgress(id)/2);
	}

	@Override
	public int getProgress(int id) {
		return this.progress;
	}

	@Override
	public int getTotalProgress(int id) {
		return 120;
	}

	@Override
	public void setTotalProgress(int id, int progress) {

	}

	@Override
	public void setProgressByUser(int id, int progress) {
		setProgress(id, progress);
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}
}
