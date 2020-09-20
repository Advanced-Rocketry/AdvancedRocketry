package zmaster587.advancedRocketry.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.network.PacketSatellite;
import zmaster587.advancedRocketry.satellite.SatelliteBiomeChanger;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.INetworkItem;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketItemModifcation;
import zmaster587.libVulpes.util.ZUtils;

import java.util.LinkedList;
import java.util.List;

public class ItemBiomeChanger extends Item {//extends ItemSatelliteIdentificationChip implements IModularInventory, IButtonInventory, INetworkItem {

	public ItemBiomeChanger(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}
	

/*	@Override
	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> list = new LinkedList<ModuleBase>();

		SatelliteBiomeChanger sat = (SatelliteBiomeChanger) getSatellite(player.getHeldItem(Hand.MAIN_HAND));
		if(player.world.isRemote) {
			list.add(new ModuleImage(24, 14, zmaster587.advancedRocketry.inventory.TextureResources.earthCandyIcon));
		}

		List<ModuleBase> list2 = new LinkedList<ModuleBase>();
		int j = 0;
		for(ResourceLocation biomeByte : sat.discoveredBiomes()) {
			Biome biome = Biome.getBiome(biomeByte);
			String biomeName = AdvancedRocketry.proxy.getNameFromBiome(biome);
			
			
			list2.add(new ModuleButton(32, 16 + 24*(j++), Biome.getIdForBiome(biome), biomeName, this, TextureResources.buttonBuild));
		}

		//Relying on a bug, is this safe?
		ModuleContainerPan pan = new ModuleContainerPan(32, 16, list2, new LinkedList<ModuleBase>(), null, 128, 128, 0, -64, 0, 1000);

		list.add(pan);
		list.add(new ModuleButton(120, 124, LibVulpes.proxy.getLocalizedString("msg.biomechanger.scan"), this, TextureResources.buttonScan));
		list.add(new ModulePower(16, 48, (IUniversalEnergy) sat));

		return list;
	}

	@Override
	public void addInformation(ItemStack stack, World player,
			List list, ITooltipFlag arg5) {

		SatelliteBase sat = DimensionManager.getInstance().getSatellite(this.getSatelliteId(stack));

		SatelliteBiomeChanger mapping = null;
		if(sat instanceof SatelliteBiomeChanger)
			mapping = (SatelliteBiomeChanger)sat;

		if(!stack.hasTag())
			list.add( LibVulpes.proxy.getLocalizedString("msg.unprogrammed"));
		else if(mapping == null)
			list.add(LibVulpes.proxy.getLocalizedString("msg.biomechanger.nosat"));
		else if(mapping.getDimensionId().isPresent() && mapping.getDimensionId().get() ==  ZUtils.getDimensionIdentifier(player)) {
			list.add(LibVulpes.proxy.getLocalizedString("msg.connected"));
			list.add(LibVulpes.proxy.getLocalizedString("msg.biomechanger.selBiome") + Biome.getBiome(mapping.getBiome()).getBiomeName());
			list.add(LibVulpes.proxy.getLocalizedString("msg.biomechanger.numBiome") + mapping.discoveredBiomes().size());
		}
		else
			list.add(LibVulpes.proxy.getLocalizedString("msg.notconnected"));

		super.addInformation(stack, player, list, arg5);
	}


	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!world.isRemote) {
			SatelliteBase sat = DimensionManager.getInstance().getSatellite(this.getSatelliteId(stack));
			if(sat != null) {
				if(player.isSneaking()) {
					if(getSatellite(stack ) != null) {
						//Make sure to update player so discoveredBiome Ids match
						PacketHandler.sendToPlayer(new PacketSatellite(getSatellite(stack )), player);
						INamedContainerProvider stack2 = (INamedContainerProvider)stack.getItem();
						NetworkHooks.openGui((ServerPlayerEntity)player, stack2, packetBuffer -> packetBuffer.writeBoolean(hand == Hand.MAIN_HAND));
						//player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARNOINV, world, -1, -1, 0);
					}
				}
				else {
					//Attempt to change biome
					sat.performAction(player, world, new BlockPos(player.getPositionVec()));
				}
			}
		}
		return super.onItemRightClick(world, player, hand);
	}

	private int getBiomeId(ItemStack stack) {
		SatelliteBase sat = getSatellite(stack);
		if(sat != null && sat instanceof SatelliteBiomeChanger)
			return ((SatelliteBiomeChanger)sat).getBiome();
		else
			return -1;
	}

	private void setBiomeId(ItemStack stack, int id) {
		if(Biome.getBiome(id) != null) {
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
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		ItemStack stack = Minecraft.getInstance().player.getHeldItem(Hand.MAIN_HAND);
		if(stack != null && stack.getItem() == this) {
			setBiomeId(stack, buttonId);
			PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getInstance().player, (byte)(buttonId  == -1 ? -1 : 0)));
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id, ItemStack stack) {
		if(id == 0) {
			out.writeInt(getBiomeId(stack));
		}
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt, ItemStack stack) {
		if(packetId == 0) {
			nbt.putInt("biome", in.readInt());
		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt, ItemStack stack) {
		if(id == -1) {
			//If -1 then discover current biome
			((SatelliteBiomeChanger)getSatellite(stack)).addBiome(Biome.getIdForBiome(player.world.getBiome(new BlockPos((int)player.posX, 0, (int)player.posZ))));
			player.closeScreen();
		}
		if(id == 0) {
			int biomeId = nbt.getInt("biome");

			
				setBiomeId(stack, biomeId);
			player.closeScreen();
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_HELD_ITEM, id, player, this.getModules(getModularInvType().ordinal(), player), this, getModularInvType());
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return GuiHandler.guiId.MODULARNOINV;
	}*/
}
