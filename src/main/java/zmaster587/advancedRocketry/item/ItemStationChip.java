package zmaster587.advancedRocketry.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.modules.ModuleStellarBackground;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.INetworkItem;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketItemModifcation;
import zmaster587.libVulpes.util.Vector3F;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;

/**
 * MetaData corresponds to the id
 */
public class ItemStationChip extends ItemIdWithName implements IModularInventory, IButtonInventory, INetworkItem {

	private static final String uuidIdentifier = "UUID";
	private static final String SELECTION_ID = "selectionId";
	private static final String DESTINATION = "dests";
	private static final String TMPNAME = "TmpName";

	private static final int BUTTON_ID_CLEAR = 0;
	private static final int BUTTON_ID_DELETE = 1;
	private static final int BUTTON_ID_ADD = 2;
	private static final int BUTTON_ID_OFFSET = 5;

	public ItemStationChip(Properties props) {
		super(props);
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if(!playerIn.world.isRemote && !stack.isEmpty() && playerIn.isSneaking())
			NetworkHooks.openGui((ServerPlayerEntity)playerIn, (INamedContainerProvider)stack.getItem(), packetBuffer -> {packetBuffer.writeInt(getModularInvType().ordinal()); packetBuffer.writeBoolean(hand == Hand.MAIN_HAND);});

		return super.onItemRightClick(worldIn, playerIn, hand);
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<>();
		final int offset_all = 96;

		ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);

		if(!stack.isEmpty()) {
			modules.add(new ModuleStellarBackground(0, 0, zmaster587.libVulpes.inventory.TextureResources.starryBG));


			List<ModuleBase> list2 = new LinkedList<>();
			ModuleButton btnAdd = new ModuleButton(172-offset_all, 18*2+28, LibVulpes.proxy.getLocalizedString("msg.label.add"), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 128, 18);
			btnAdd.setAdditionalData(BUTTON_ID_ADD);
			ModuleButton btnClear = new ModuleButton(172-offset_all, 18*4+28, LibVulpes.proxy.getLocalizedString("msg.label.clear"), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 128, 18);
			btnClear.setAdditionalData(BUTTON_ID_CLEAR);
			ModuleButton btnDelete = new ModuleButton(172-offset_all, 18*3+28, LibVulpes.proxy.getLocalizedString("msg.label.delete"), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 128, 18);
			btnDelete.setAdditionalData(BUTTON_ID_DELETE);

			modules.add(btnClear);
			modules.add(btnDelete);
			modules.add(btnAdd);

			// Get effective dimension
			ResourceLocation dimId = DimensionManager.getEffectiveDimId(ZUtils.getDimensionIdentifier(player.world), new BlockPos(player.getPositionVec())).getId();
			List<LandingLocation> list = getLandingLocations(stack, dimId);

			int selectedId = getSelectionId(stack, dimId);
			int i = 0;
			ModuleButton button;
			for( LandingLocation pos : list) 
			{
				button = new ModuleButton(0, i*18, pos.toString(), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 128, 18);
				button.setAdditionalData(i+BUTTON_ID_OFFSET);
				list2.add(button);

				if (i == selectedId)
					button.setColor(0xFF22FF22);
				else
					button.setColor(0xFFFF2222);

				i++;
			}

			ModuleContainerPan pan = new ModuleContainerPan(25-offset_all, 50, list2, new LinkedList<>(), null, 512, 256, 0, -48, 258, 256);
			modules.add(pan);
		}

		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "item.advancedrocketry.stationchip";
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		ItemStack stack = Minecraft.getInstance().player.getHeldItem(Hand.MAIN_HAND);
		if(!stack.isEmpty() && stack.getItem() == this) {
			Integer num = (Integer)buttonId.getAdditionalData();
			
			PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getInstance().player, num.byteValue()));
		}
	}


	private String getTempName(@Nonnull ItemStack stack)
	{
		if(stack.hasTag())
			return stack.getTag().getString(TMPNAME);
		return "";
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id, @Nonnull ItemStack stack) {
		if(id == BUTTON_ID_ADD) {
			String str = getTempName(stack);
			byte[] byteArray = str.getBytes();
			short len = (short)byteArray.length;
			out.writeShort(len);
			out.writeBytes(byteArray, 0, len);
		}

	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte id, CompoundNBT nbt, ItemStack stack) {
		if(id == BUTTON_ID_ADD) {
			short len = in.readShort();
			byte[] byteArray = new byte[len];
			in.readBytes(byteArray, 0, len);
			nbt.putString(TMPNAME, new String(byteArray));
		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id, CompoundNBT nbt, ItemStack stack) {
		if(!player.world.isRemote) {
			ResourceLocation dimId = DimensionManager.getEffectiveDimId(ZUtils.getDimensionIdentifier(player.world), new BlockPos(player.getPositionVec())).getId();
			if(id >= BUTTON_ID_OFFSET) {
				setSelectionId(stack, dimId, id-BUTTON_ID_OFFSET);
			} else if(id == BUTTON_ID_DELETE) {
				int selection = getSelectionId(stack, dimId);

				//Can't delete "Last"
				if(selection > 0)
				{
					List<LandingLocation> locs = getLandingLocations(stack, dimId);
					if(selection < locs.size())
						locs.remove(selection);
					setLandingLocations(stack, dimId, locs);
				}
			} else if(id == BUTTON_ID_CLEAR) {
				//Can't delete "Last"
				List<LandingLocation> locs = getLandingLocations(stack, dimId);
				List<LandingLocation> locs2 = new LinkedList<>();
				locs2.add(locs.get(0));
				setLandingLocations(stack, dimId, locs2);
			} else if(id == BUTTON_ID_ADD) {
				// this will be false if on a space station, do not set on space station
				if(ZUtils.getDimensionIdentifier(player.getEntityWorld()).equals(dimId)) {
					List<LandingLocation> locs = getLandingLocations(stack, dimId);
					BlockPos pos = new BlockPos(player.getPositionVec());
					locs.add(new LandingLocation(nbt.getString(TMPNAME), pos.getX(), pos.getY(), pos.getZ()));
					setLandingLocations(stack, dimId, locs);
				}
			}
			//Re-open the UI
			player.closeScreen();
			
			//NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)stack.getItem(), packetBuffer -> {packetBuffer.writeInt(getModularInvType().ordinal()); packetBuffer.writeBoolean(true);});
		}
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity player) {
		return player.getHeldItem(player.getActiveHand()).getItem() == this;
	}

	public int getSelectionId(ItemStack stack, ResourceLocation dimid) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
			if(nbt.contains("dimid" + dimid)) {
				nbt = nbt.getCompound("dimid" + dimid);
				int size = getLandingLocations(stack, dimid).size();
				int selectedId = nbt.getInt(SELECTION_ID);
				return size > selectedId ? selectedId : 0;
			}
		}
		return 0;
	}

	public void setSelectionId(ItemStack stack, ResourceLocation dimid, int slotId) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
			if(nbt.contains("dimid" + dimid)) {
				nbt = nbt.getCompound("dimid" + dimid);
				nbt.putInt(SELECTION_ID, slotId);
			}
		}
	}

	public List<LandingLocation> getLandingLocations(ItemStack stack, ResourceLocation dimid) {
		List<LandingLocation> retList = new LinkedList<>();

		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
			if(nbt.contains("dimid" + dimid)) {
				nbt = nbt.getCompound("dimid" + dimid);
				ListNBT destList = nbt.getList(DESTINATION, NBT.TAG_COMPOUND);

				for(INBT tag : destList) {
					retList.add(LandingLocation.loadFromNBT((CompoundNBT)tag));
				}
			}
		}
		return retList;
	}

	public void setLandingLocations(ItemStack stack, ResourceLocation dimid, List<LandingLocation> locations) {
		if(stack.hasTag()) {
			CompoundNBT stackNBT = stack.getTag();
			CompoundNBT nbt;
			String tagName = "dimid" + dimid;
			if(stackNBT.contains(tagName)) 
				nbt = stackNBT.getCompound("dimid" + dimid);
			else
				nbt = new CompoundNBT();

			ListNBT destList;
			destList = new ListNBT();

			for(LandingLocation loc : locations) {
				CompoundNBT nbtTag = new CompoundNBT();
				loc.savetoNBT(nbtTag);
				destList.add(nbtTag);
			}

			nbt.put(DESTINATION, destList);
			stackNBT.put(tagName, nbt);
		}
	}

	public void setTakeoffCoords(ItemStack stack, Vector3F<Float> pos, ResourceLocation dimid, int slot) {
		setTakeoffCoords(stack, pos.x, pos.y, pos.z, dimid, slot);
	}

	public void setTakeoffCoords(ItemStack stack, float x, float y, float z, ResourceLocation dimid, int slot) {
		LandingLocation landingLoc = new LandingLocation("Last", x,y,z);

		List<LandingLocation> landingLocList = getLandingLocations(stack, dimid);

		if(landingLocList.isEmpty() || slot >= landingLocList.size())
			landingLocList.add(landingLoc);
		else
			landingLocList.set(slot, landingLoc);

		setLandingLocations(stack, dimid, landingLocList);
	}

	/**
	 * @param stack
	 * @param dimid
	 * @return Vector3F containing the takeoff coords or null if there is none
	 */
	public LandingLocation getTakeoffCoords(ItemStack stack, ResourceLocation dimid) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
			if(nbt.contains("dimid" + dimid)) {
				nbt = nbt.getCompound("dimid" + dimid);
				List<LandingLocation> landingLocList = getLandingLocations(stack, dimid);
				int id = getSelectionId(stack, dimid);
				LandingLocation loc;
				if(landingLocList.isEmpty())
					return null;
				else if(id < landingLocList.size() && id > 0)
					loc = landingLocList.get(id);
				else
					loc = landingLocList.get(0);


				return loc;
			}
		}
		return null;
	}

	public static ResourceLocation getUUID(ItemStack stack) {
		if(stack.hasTag())
			return new ResourceLocation(Constants.modId, stack.getTag().getString(uuidIdentifier).split(":")[1]);
		return Constants.INVALID_PLANET;
	}

	public static void setUUID(ItemStack stack, ResourceLocation uuid) {
		CompoundNBT nbt;
		if(stack.hasTag())
			nbt = stack.getTag();
		else
			nbt = new CompoundNBT();

		nbt.putString(uuidIdentifier,uuid.toString());
		stack.setTag(nbt);
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag bool) {
		if(world == null || getUUID(stack) == DimensionManager.overworldProperties.getId())
			list.add(new StringTextComponent(TextFormatting.GRAY + LibVulpes.proxy.getLocalizedString("msg.unprogrammed")));
		else {
			list.add(new StringTextComponent(TextFormatting.GREEN + LibVulpes.proxy.getLocalizedString("msg.stationchip.station") + getUUID(stack)));
			super.addInformation(stack, world, list, bool);
			if(DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world))) {
				Entity p = Minecraft.getInstance().player;
				ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(new BlockPos(p.getPositionVec()));

				if(obj != null) {
					LandingLocation loc = getTakeoffCoords(stack, obj.getOrbitingPlanetId());
					if(loc != null) {
						Vector3F<Float> vec = loc.location;
						list.add(new StringTextComponent("Name: " + loc.name));
						list.add(new StringTextComponent("X: " + vec.x));
						list.add(new StringTextComponent("Z: " + vec.z));
					}
					else {
						list.add(new StringTextComponent("Name: N/A"));
						list.add(new StringTextComponent("X: N/A"));
						list.add(new StringTextComponent("Z: N/A"));
					}
				}
			}
			else {
				LandingLocation loc = getTakeoffCoords(stack, ZUtils.getDimensionIdentifier(world));
				if(loc != null) {
					Vector3F<Float> vec = loc.location;
					list.add(new StringTextComponent("Name: " + loc.name));
					list.add(new StringTextComponent("X: " + vec.x));
					list.add(new StringTextComponent("Z: " + vec.z));
				}
				else {
					list.add(new StringTextComponent("Name: N/A"));
					list.add(new StringTextComponent("X: N/A"));
					list.add(new StringTextComponent("Z: N/A"));
				}
			}
			list.add(new StringTextComponent(TextFormatting.DARK_GRAY + LibVulpes.proxy.getLocalizedString("item.stationchip.openmenu")));
		}
	}

	public static class LandingLocation {
		public String name;
		public Vector3F<Float> location;

		public LandingLocation(String name, Vector3F<Float> location) {
			this.name = name;
			this.location = location;
		}

		public LandingLocation(String name, float x, float y, float z) {
			this.name = name;
			this.location = new Vector3F<>(x, y, z);
		}

		public LandingLocation() {
			this.name = "";
			this.location = new Vector3F<>(0f, 0f, 0f);
		}

		@Override
		public String toString() {
			return String.format("%s: %.0f, %.0f", name, location.x, location.z);
		}

		static LandingLocation loadFromNBT(CompoundNBT nbt) {
			String name = nbt.getString("name");
			Vector3F<Float> vec = new Vector3F<>(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"));

			return new LandingLocation(name, vec);
		}

		void savetoNBT(CompoundNBT nbt) {
			nbt.putString("name", this.name);
			nbt.putFloat("x", this.location.x);
			nbt.putFloat("y", this.location.y);
			nbt.putFloat("z", this.location.z);
		}
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	@ParametersAreNonnullByDefault
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_HELD_ITEM, id, player, getModules(getModularInvType().ordinal(), player), this, getModularInvType());
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return  GuiHandler.guiId.MODULARCENTEREDFULLSCREEN;
	}
}
