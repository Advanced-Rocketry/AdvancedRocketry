package zmaster587.advancedRocketry.item;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.block.BlockTile;
import zmaster587.advancedRocketry.inventory.GuiHandler;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.IButtonInventory;
import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModuleButton;
import zmaster587.advancedRocketry.inventory.modules.ModuleContainerPan;
import zmaster587.advancedRocketry.network.INetworkItem;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketItemModifcation;
import zmaster587.advancedRocketry.tile.TileSchematic;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlock;
import zmaster587.advancedRocketry.tile.multiblock.TilePlaceholder;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.util.Vector3F;
import zmaster587.libVulpes.util.ZUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemProjector extends Item implements IModularInventory, IButtonInventory, INetworkItem {

	ArrayList<TileMultiBlock> machineList;
	ArrayList<BlockTile> blockList;
	private static final String IDNAME = "machineId";

	public ItemProjector() {
		machineList = new ArrayList<TileMultiBlock>();
		blockList = new ArrayList<BlockTile>();
	}

	public void registerMachine(TileMultiBlock multiblock, BlockTile mainBlock) {
		machineList.add(multiblock);
		blockList.add(mainBlock);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void mouseEvent(MouseEvent event) {
		if(Minecraft.getMinecraft().thePlayer.isSneaking() && event.dwheel != 0) {
			ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();

			if(stack != null && stack.getItem() == this && getMachineId(stack) != -1) {
				if(event.dwheel < 0) {
					setYLevel(stack, getYLevel(stack) + 1);
				}
				else
					setYLevel(stack, getYLevel(stack) - 1);
				event.setCanceled(true);

				PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getMinecraft().thePlayer, (byte)1));
			}
		}
	}

	private void RebuildStructure(World world, TileMultiBlock tile, ItemStack stack, int posX, int posY, int posZ, ForgeDirection orientation) {

		int id = getMachineId(stack);
		ForgeDirection direction = ForgeDirection.getOrientation(getDirection(stack));

		TileMultiBlock multiblock = machineList.get(id);

		int prevMachineId = getPrevMachineId(stack);
		Object[][][] structure;
		if(prevMachineId >= 0 && prevMachineId < machineList.size()) {
			structure = machineList.get(prevMachineId).getStructure();

			Vector3F<Integer> basepos = getBasePosition(stack);

			for(int y = 0; y < structure.length; y++) {
				for(int z=0 ; z < structure[0].length; z++) {
					for(int x=0; x < structure[0][0].length; x++) {

						int globalX = basepos.x - x*direction.offsetZ + z*direction.offsetX;
						int globalZ = basepos.z + (x* direction.offsetX) + (z*direction.offsetZ);

						if(world.getBlock(globalX, basepos.y + y, globalZ) == AdvancedRocketryBlocks.blockPhantom) 
							world.setBlockToAir(globalX, basepos.y + y, globalZ);
					}
				}
			}
		}
		structure = multiblock.getStructure();
		direction = orientation;

		int y = getYLevel(stack);
		int endNumber, startNumber;

		if(y == -1) {
			startNumber = 0;
			endNumber = structure.length;
		}
		else {
			startNumber = y;
			endNumber = y + 1;
		}
		for(y=startNumber; y < endNumber; y++) {
			for(int z=0 ; z < structure[0].length; z++) {
				for(int x=0; x < structure[0][0].length; x++) {
					List<BlockMeta> block;
					if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c') {
						block = new ArrayList<BlockMeta>();
						block.add(new BlockMeta(blockList.get(id), orientation.ordinal()));
					}
					else if(multiblock.getAllowableBlocks(structure[y][z][x]).isEmpty())
						continue;
					else
						block = multiblock.getAllowableBlocks(structure[y][z][x]);

					int globalX = posX - x*direction.offsetZ + z*direction.offsetX;
					int globalZ = posZ + (x* direction.offsetX)  + (z*direction.offsetZ);
					int globalY = -y + structure.length + posY - 1;

					if(world.isAirBlock(globalX, globalY, globalZ) && block.get(0).getBlock().getMaterial() != Material.air) {
						//block = (Block)structure[y][z][x];
						world.setBlock(globalX, globalY, globalZ, AdvancedRocketryBlocks.blockPhantom);
						TileEntity newTile = world.getTileEntity(globalX, globalY, globalZ);

						//TODO: compatibility fixes with the tile entity not reflecting current block
						if(newTile instanceof TilePlaceholder) {
							((TileSchematic)newTile).setReplacedBlock(block);
							((TilePlaceholder)newTile).setReplacedTileEntity(block.get(0).getBlock().createTileEntity(null, 0));
						}
					}
				}
			}
		}
		this.setPrevMachineId(stack, id);
		this.setBasePosition(stack, posX, posY, posZ);
		this.setDirection(stack, orientation.ordinal());
	}


	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world,
			EntityPlayer player) {

		if(!world.isRemote && player.isSneaking()) {
			player.openGui(AdvancedRocketry.instance, GuiHandler.guiId.MODULARNOINV.ordinal(), world, -1, -1, 0);
			return super.onItemRightClick(stack, world, player);
		}

		int id = getMachineId(stack);
		if(id != -1) {
			int intDir = ZUtils.getDirectionFacing(player.rotationYaw - 180);
			ForgeDirection direction = ForgeDirection.getOrientation(intDir);
			int xi = (int)player.posX + 4*direction.offsetX;
			int zi = (int)player.posZ + 4*direction.offsetZ;
			int playerY = (int) Math.ceil(player.posY);

			if(!world.isRemote)
				RebuildStructure(world, machineList.get(id), stack, xi, playerY, zi, direction);
		}

		return super.onItemRightClick(stack, world, player);
	}

	@Override
	public List<ModuleBase> getModules() {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		List<ModuleBase> btns = new LinkedList<ModuleBase>();

		for(int i = 0; 	i <	machineList.size(); i++) {
			TileMultiBlock multiblock = machineList.get(i);
			btns.add(new ModuleButton(60, 4 + i*24, i, AdvancedRocketry.proxy.getLocalizedString(multiblock.getMachineName()), this, TextureResources.buttonBuild));
		}

		ModuleContainerPan panningContainer = new ModuleContainerPan(5, 20, btns, new LinkedList<ModuleBase>(), TextureResources.starryBG, 165, 120, 0, 500);
		modules.add(panningContainer);
		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "item.holoProjector.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return !entity.isDead && entity.getHeldItem().getItem() == this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onInventoryButtonPressed(int buttonId) {
		//PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getMinecraft().thePlayer, (byte)buttonId));
		ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();
		if(stack != null && stack.getItem() == this) {
			setMachineId(stack, buttonId);
			PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getMinecraft().thePlayer, (byte)0));
		}
	}

	private void setMachineId(ItemStack stack, int id) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else 
			nbt = new NBTTagCompound();

		nbt.setInteger(IDNAME, id);
		stack.setTagCompound(nbt);
	}

	private int getMachineId(ItemStack stack) {
		if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger(IDNAME);
		}
		else
			return -1;
	}

	private void setYLevel(ItemStack stack, int level) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else 
			nbt = new NBTTagCompound();

		TileMultiBlock machine = machineList.get(getMachineId(stack));

		if(level == -2)
			level = machine.getStructure().length-1;
		else if(level == machine.getStructure().length)
			level = -1;
		nbt.setInteger("yOffset", level);
		stack.setTagCompound(nbt);
	}

	private int getYLevel(ItemStack stack) {
		if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger("yOffset");
		}
		else
			return -1;
	}

	private void setPrevMachineId(ItemStack stack, int id) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else 
			nbt = new NBTTagCompound();

		nbt.setInteger(IDNAME + "Prev", id);
		stack.setTagCompound(nbt);
	}

	private int getPrevMachineId(ItemStack stack) {
		if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger(IDNAME + "Prev");
		}
		else
			return -1;
	}

	private Vector3F<Integer> getBasePosition(ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			Vector3F<Integer> vec = new Vector3F<Integer>(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
			return vec;
		}
		else
			return null;
	}

	private void setBasePosition(ItemStack stack, int x, int y, int z) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else
			nbt = new NBTTagCompound();

		nbt.setInteger("x", x);
		nbt.setInteger("y", y);
		nbt.setInteger("z", z);

		stack.setTagCompound(nbt);
	}

	public int getDirection(ItemStack stack) {
		if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger("dir");
		}
		else
			return -1;
	}

	public void setDirection(ItemStack stack, int dir) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else
			nbt = new NBTTagCompound();

		nbt.setInteger("dir", dir);

		stack.setTagCompound(nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player,
			List list, boolean bool) {
		super.addInformation(stack, player, list, bool);

		list.add("Shift right-click: opens machine selection interface");
		list.add("Shift-scroll: moves cross-section");

		int id = getMachineId(stack);
		if(id != -1)
			list.add(EnumChatFormatting.GREEN + AdvancedRocketry.proxy.getLocalizedString(machineList.get(id).getMachineName()));
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id, ItemStack stack) {
		if(id == 0) {
			out.writeInt(getMachineId(stack));
		}
		if(id == 1)
			out.writeInt(getYLevel(stack));
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt, ItemStack stack) {
		if(packetId == 0) {
			nbt.setInteger(IDNAME, in.readInt());
		}

		if(packetId == 1)
			nbt.setInteger("yLevel", in.readInt());
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt, ItemStack stack) {
		if(id == 0) {
			int machineId = nbt.getInteger(IDNAME);
			setMachineId(stack, nbt.getInteger(IDNAME));
			TileMultiBlock tile = machineList.get(machineId);
			setYLevel(stack, tile.getStructure().length-1);
		}
		if(id == 1) {
			setYLevel(stack, nbt.getInteger("yLevel"));
			Vector3F<Integer> vec = getBasePosition(stack);
			RebuildStructure(player.worldObj, this.machineList.get(getMachineId(stack)), stack, vec.x, vec.y, vec.z, ForgeDirection.getOrientation(getDirection(stack)));
		}
	}
}