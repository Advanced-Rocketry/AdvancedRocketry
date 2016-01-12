package zmaster587.advancedRocketry.tile.infrastructure;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.client.render.util.IndicatorBarImage;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.IButtonInventory;
import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.IProgressBar;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModuleButton;
import zmaster587.advancedRocketry.inventory.modules.ModuleProgress;
import zmaster587.advancedRocketry.network.PacketEntity;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.item.ItemLinker;
import zmaster587.libVulpes.util.INetworkMachine;

public class TileEntityMoniteringStation extends TileEntity  implements IModularInventory, IInfrastructure, ILinkableTile, INetworkMachine, IButtonInventory, IProgressBar  {

	EntityRocketBase linkedRocket;

	int rocketHeight;
	int velocity;
	int fuelLevel, maxFuelLevel;

	@Override
	public void invalidate() {
		super.invalidate();

		if(linkedRocket != null) {
			linkedRocket.unlinkInfrastructure(this);
			unlinkRocket();
		}
	}

	@Override
	public int getMaxLinkDistance() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {

		ItemLinker.setMasterCoords(item, this.xCoord, this.yCoord, this.zCoord);

		if(player.worldObj.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("You program the linker with the monitoring station at: " + this.xCoord + " " + this.yCoord + " " + this.zCoord)));
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		if(player.worldObj.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("This must be the first machine to link!")));
		return false;
	}

	@Override
	public void unlinkRocket() {
		linkedRocket = null;
	}

	@Override
	public boolean disconnectOnLiftOff() {
		return false;
	}

	@Override
	public boolean linkRocket(EntityRocketBase rocket) {
		this.linkedRocket = rocket;
		return true;
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {

	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {

	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {

		if(id == 100) {
			if(linkedRocket != null)
				linkedRocket.launch();
		}
	}

	@Override
	public List<ModuleBase> getModules() {

		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModuleButton(20, 60, 0, "Launch!", this, TextureResources.buttonBuild));
		modules.add(new ModuleProgress(98, 4, 0, new IndicatorBarImage(2, 7, 12, 81, 17, 0, 6, 6, 1, 0, ForgeDirection.UP, TextureResources.rocketHud), this));
		modules.add(new ModuleProgress(120, 14, 1, new IndicatorBarImage(2, 95, 12, 71, 17, 0, 6, 6, 1, 0, ForgeDirection.UP, TextureResources.rocketHud), this));
		modules.add(new ModuleProgress(142, 14, 2, new ProgressBarImage(2, 173, 12, 71, 17, 6, 3, 69, 1, 1, ForgeDirection.UP, TextureResources.rocketHud), this));

		return modules;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		PacketHandler.sendToServer(new PacketMachine(this, (byte) (buttonId + 100)) );
	}

	@Override
	public String getModularInventoryName() {
		return "container.monitoringstation";
	}

	@Override
	public float getNormallizedProgress(int id) {
		if(id == 1) {
			return Math.min(0.5f + (getProgress(id)/(float)getTotalProgress(id)), 1);
		}
		return getProgress(id)/(float)getTotalProgress(id);
	}

	@Override
	public void setProgress(int id, int progress) {
		if(id == 0)
			rocketHeight = progress;
		else if(id == 1)
			velocity = progress;
		else if(id == 2)
			fuelLevel = progress;
	}

	@Override
	public int getProgress(int id) {
		//Try to keep client synced with server, this also allows us to put the monitor on a different world altogether
		if(worldObj.isRemote)
			if(id == 0)
				return rocketHeight;
			else if(id == 1)
				return velocity;
			else if(id == 2)
				return fuelLevel;

		if(linkedRocket == null)
			return 0;
		if(id == 0)
			return (int)linkedRocket.posY;
		else if(id == 1)
			return (int)(linkedRocket.motionY*100);
		else if (id == 2)
			return (int)(linkedRocket.getFuelAmount());
		
		return 0;
	}

	@Override
	public int getTotalProgress(int id) {
		if(id == 0)
			return Configuration.orbit;
		else if(id == 1)
			return 200;
		else if(id == 2)
			if(worldObj.isRemote)
				return maxFuelLevel;
			else
				if(linkedRocket == null)
					return 0;
				else
					return linkedRocket.getFuelCapacity();
		
		return 1;
	}

	@Override
	public void setTotalProgress(int id, int progress) {
		//Should only become an issue if configs are desynced or fuel
		if(id == 2)
			maxFuelLevel = progress;
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}
}
