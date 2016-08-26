package zmaster587.advancedRocketry.item;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.satellite.SatelliteBiomeChanger;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleContainerPan;
import zmaster587.libVulpes.inventory.modules.ModuleImage;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.network.INetworkItem;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketItemModifcation;

public class ItemBiomeChanger extends ItemSatelliteIdentificationChip implements IModularInventory, IButtonInventory, INetworkItem {

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> list = new LinkedList<ModuleBase>();
		
		if(player.worldObj.isRemote) {
			list.add(new ModuleImage(24, 14, zmaster587.advancedRocketry.inventory.TextureResources.earthCandyIcon));
		}
		
		List<ModuleBase> list2 = new LinkedList<ModuleBase>();
		int j = 0;
		for(int i = 0; i < BiomeGenBase.getBiomeGenArray().length; i++) {
			BiomeGenBase biome = BiomeGenBase.getBiomeGenArray()[i];
			if(biome != null) {
				list2.add(new ModuleButton(32, 16 + 24*(j), biome.biomeID, biome.biomeName, this, TextureResources.buttonBuild));
				j++;
			}

		}
		for(BiomeGenBase biome : AdvancedRocketryBiomes.instance.getHighPressureBiomes()) {
			list2.add(new ModuleButton(32, 16 + 24*(j++), biome.biomeID, biome.biomeName, this, TextureResources.buttonBuild));
		}
		
		//Relying on a bug, is this safe?
		ModuleContainerPan pan = new ModuleContainerPan(32, 16, list2, new LinkedList<ModuleBase>(), null, 128, 128, 0, -64, 0, 1000);

		list.add(pan);
		list.add(new ModulePower(16, 48, (IUniversalEnergy) getSatellite(player.getCurrentEquippedItem())));

		return list;
	}
	
	

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world,
			EntityPlayer player) {
		if(!world.isRemote) {
			if(player.isSneaking()) {
				if(getSatellite(stack ) != null)
					player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARNOINV.ordinal(), world, -1, -1, 0);
				return super.onItemRightClick(stack, world, player);
			}
			else {
				//Attempt to change biome
				SatelliteBase satellite = getSatellite(stack);
				if(satellite != null) {
					satellite.performAction(player, world, (int)player.posX, (int)player.posY, (int)player.posZ);
				}
			}
		}
		return super.onItemRightClick(stack, world, player);
	}

	private int getBiomeId(ItemStack stack) {
		SatelliteBase sat = getSatellite(stack);
		if(sat != null && sat instanceof SatelliteBiomeChanger)
			return ((SatelliteBiomeChanger)sat).getBiome();
		else
			return -1;
	}

	private void setBiomeId(ItemStack stack, int id) {
		if(BiomeGenBase.getBiome(id) != null) {
			SatelliteBase sat = getSatellite(stack);
			if(sat != null && sat instanceof SatelliteBiomeChanger) {
				((SatelliteBiomeChanger)sat).setBiome(id);
			}
		}
	}

	@Override
	public String getModularInventoryName() {
		return "item.biomeChanger.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();
		if(stack != null && stack.getItem() == this) {
			setBiomeId(stack, buttonId);
			PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getMinecraft().thePlayer, (byte)0));
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id, ItemStack stack) {
		if(id == 0) {
			out.writeInt(getBiomeId(stack));
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt, ItemStack stack) {
		if(packetId == 0) {
			nbt.setInteger("biome", in.readInt());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt, ItemStack stack) {
		if(id == 0) {
			//TODO: identify players attempting to send invalid/locked? biomes
			//If this message is for you then you know what i mean ;)
			setBiomeId(stack, nbt.getInteger("biome"));
		}
	}
}
