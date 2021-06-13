package zmaster587.advancedRocketry.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.network.PacketSatellite;
import zmaster587.advancedRocketry.satellite.SatelliteBiomeChanger;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.INetworkItem;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketItemModifcation;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class ItemBiomeChanger extends ItemSatelliteIdentificationChip implements IModularInventory, IButtonInventory, INetworkItem {

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> list = new LinkedList<>();

		SatelliteBiomeChanger sat = (SatelliteBiomeChanger) getSatellite(player.getHeldItem(EnumHand.MAIN_HAND));
		if(player.world.isRemote) {
			list.add(new ModuleImage(24, 14, zmaster587.advancedRocketry.inventory.TextureResources.earthCandyIcon));
		}

		List<ModuleBase> list2 = new LinkedList<>();
		int j = 0;
		for(byte biomeByte : sat.discoveredBiomes()) {
			Biome biome = Biome.getBiome(biomeByte);
			String biomeName = AdvancedRocketry.proxy.getNameFromBiome(biome);
			
			
			list2.add(new ModuleButton(32, 16 + 24*(j++), Biome.getIdForBiome(biome), biomeName, this, TextureResources.buttonBuild));
		}

		//Relying on a bug, is this safe?
		ModuleContainerPan pan = new ModuleContainerPan(32, 16, list2, new LinkedList<>(), null, 128, 128, 0, -64, 0, 1000);

		list.add(pan);
		list.add(new ModuleButton(120, 124, -1, LibVulpes.proxy.getLocalizedString("msg.biomechanger.scan"), this, TextureResources.buttonScan));
		list.add(new ModulePower(16, 48, sat.getBattery()));

		return list;
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, World player, List<String> list, ITooltipFlag arg5) {

		SatelliteBase sat = DimensionManager.getInstance().getSatellite(this.getSatelliteId(stack));

		SatelliteBiomeChanger mapping = null;
		if(sat instanceof SatelliteBiomeChanger)
			mapping = (SatelliteBiomeChanger)sat;

		if(!stack.hasTagCompound())
			list.add(LibVulpes.proxy.getLocalizedString("msg.unprogrammed"));
		else if(mapping == null)
			list.add(LibVulpes.proxy.getLocalizedString("msg.biomechanger.nosat"));
		else if(mapping.getDimensionId() == player.provider.getDimension()) {
			list.add(LibVulpes.proxy.getLocalizedString("msg.connected"));
			list.add(LibVulpes.proxy.getLocalizedString("msg.biomechanger.selBiome") + Biome.getBiome(mapping.getBiome()).getBiomeName());
			list.add(LibVulpes.proxy.getLocalizedString("msg.biomechanger.numBiome") + mapping.discoveredBiomes().size());
		}
		else
			list.add(LibVulpes.proxy.getLocalizedString("msg.notconnected"));

		super.addInformation(stack, player, list, arg5);
	}


	@Override
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!world.isRemote) {
			SatelliteBase sat = DimensionManager.getInstance().getSatellite(this.getSatelliteId(stack));
			if(sat != null) {
				if(player.isSneaking()) {
					if(getSatellite(stack ) != null) {
						//Make sure to update player so discoveredBiome Ids match
						PacketHandler.sendToPlayer(new PacketSatellite(getSatellite(stack )), player);
						player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARNOINV.ordinal(), world, -1, -1, 0);
					}
				}
				else {
					//Attempt to change biome
					sat.performAction(player, world, player.getPosition());
				}
			}
		}
		return super.onItemRightClick(world, player, hand);
	}

	private int getBiomeId(@Nonnull ItemStack stack) {
		SatelliteBase sat = getSatellite(stack);
		if(sat instanceof SatelliteBiomeChanger)
			return ((SatelliteBiomeChanger)sat).getBiome();
		else
			return -1;
	}

	private void setBiomeId(@Nonnull ItemStack stack, int id) {
		if(Biome.getBiome(id) != null) {
			SatelliteBase sat = getSatellite(stack);
			if(sat instanceof SatelliteBiomeChanger) {
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
	@SideOnly(Side.CLIENT)
	public void onInventoryButtonPressed(int buttonId) {
		ItemStack stack = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
		if(!stack.isEmpty() && stack.getItem() == this) {
			setBiomeId(stack, buttonId);
			PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getMinecraft().player, (byte)(buttonId  == -1 ? -1 : 0)));
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id, @Nonnull ItemStack stack) {
		if(id == 0) {
			out.writeInt(getBiomeId(stack));
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt, @Nonnull ItemStack stack) {
		if(packetId == 0) {
			nbt.setInteger("biome", in.readInt());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt, @Nonnull ItemStack stack) {
		if(id == -1) {
			//If -1 then discover current biome
			((SatelliteBiomeChanger)getSatellite(stack)).addBiome(Biome.getIdForBiome(player.world.getBiome(new BlockPos((int)player.posX, 0, (int)player.posZ))));
			player.closeScreen();
		}
		if(id == 0) {
			int biomeId = nbt.getInteger("biome");

			
				setBiomeId(stack, biomeId);
			player.closeScreen();
		}
	}
}
