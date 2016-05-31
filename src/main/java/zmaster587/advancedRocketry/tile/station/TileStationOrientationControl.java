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
import zmaster587.advancedRocketry.inventory.modules.IButtonInventory;
import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.IProgressBar;
import zmaster587.advancedRocketry.inventory.modules.ISliderBar;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModuleSlider;
import zmaster587.advancedRocketry.inventory.modules.ModuleText;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.util.INetworkMachine;

public class TileStationOrientationControl extends TileEntity implements IModularInventory, INetworkMachine, ISliderBar {

	int numRotationsPerHour;

	@Override
	public List<ModuleBase> getModules(int id) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModuleText(10, 15, "Angular Velocity: ", 0xaa2020));
		modules.add(new ModuleText(10, 25, "Number Of Thrusters: ", 0xaa2020));
		modules.add(new ModuleText(10, 35, "Maximum Angular Acceleration: ", 0xaa2020));

		modules.add(new ModuleText(10, 45, "Target Number of rotations per hour:", 0x202020));
		modules.add(new ModuleSlider(10, 60, 0, TextureResources.distanceIndicator, (ISliderBar)this));
		return modules;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if(this.worldObj.provider instanceof WorldProviderSpace) {
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
				}
			}
		}
	}
	@Override
	public String getModularInventoryName() {
		return "tile.orientationControl";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 0) {
			out.writeShort(numRotationsPerHour);
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == 0) {
			numRotationsPerHour = in.readShort();
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
	}


	@Override
	public float getNormallizedProgress(int id) {
		return getProgress(0)/(float)getTotalProgress(0);
	}

	@Override
	public void setProgress(int id, int progress) {
		numRotationsPerHour = progress;
	}

	@Override
	public int getProgress(int id) {
		return numRotationsPerHour;
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
		setProgress(id, progress-1);
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}
}
