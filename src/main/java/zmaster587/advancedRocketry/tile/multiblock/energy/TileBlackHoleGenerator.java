package zmaster587.advancedRocketry.tile.multiblock.energy;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IUniversalEnergyTransmitter;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerProducer;
import zmaster587.libVulpes.util.MultiBattery;
import zmaster587.libVulpes.util.Vector3F;
import zmaster587.libVulpes.util.ZUtils;

public class TileBlackHoleGenerator extends TileMultiPowerProducer implements ITickable {
	static final Object[][][] structure = new Object[][][] {
		{
			{null, null, null},
			{null, LibVulpesBlocks.blockAdvStructureBlock, null},
			{null, null, null}
		},
		{
			{null, 'c', null},
			{'*', LibVulpesBlocks.blockAdvStructureBlock, '*'},
			{null, '*', null},
		},
		{
			{null, LibVulpesBlocks.blockAdvStructureBlock, null},
			{null, LibVulpesBlocks.blockAdvStructureBlock, null},
			{null, null, null}
		},
		{
			{null, null, null},
			{null, LibVulpesBlocks.blockAdvStructureBlock, null},
			{null, null, null}
		},
		{
			{null, null, null},
			{null, LibVulpesBlocks.blockAdvStructureBlock, null},
			{null, null, null}
		}};

		int powerMadeLastTick, prevPowerMadeLastTick;
		ModuleText textModule;
		boolean initialCheck;
		private long last_usage;

		public TileBlackHoleGenerator() {
			initialCheck = false;
			textModule = new ModuleText(40, 20, LibVulpes.proxy.getLocalizedString("msg.microwaverec.notgenerating"), 0x2b2b2b);
		}

		@Override
		public List<ModuleBase> getModules(int ID, EntityPlayer player) {
			List<ModuleBase> modules = super.getModules(ID, player);

			modules.add(textModule);

			return modules;
		}

		@Override
		public AxisAlignedBB getRenderBoundingBox() {
			return super.getRenderBoundingBox().grow(0, 2000, 0).offset(0, 1000, 0);
		}

		@Override
		public boolean shouldHideBlock(World world, BlockPos pos, IBlockState tile) {
			return world.getBlockState(pos).getBlock() == AdvancedRocketryBlocks.blockBlackHoleGenerator;
		}

		@Override
		public Object[][][] getStructure() {
			return structure;
		}

		@Override
		public List<BlockMeta> getAllowableWildCardBlocks() {
			List<BlockMeta> blocks = super.getAllowableWildCardBlocks();
			blocks.addAll(TileMultiBlock.getMapping('I'));
			blocks.add(new BlockMeta( LibVulpesBlocks.blockAdvStructureBlock));
			blocks.addAll(TileMultiBlock.getMapping('p'));

			return blocks;
		}

		@Override
		public String getMachineName() {
			return "tile.blackholegenerator.name";
		}
		public int getPowerMadeLastTick() {
			return powerMadeLastTick;
		}

		@Override
		public void onInventoryUpdated() {
			super.onInventoryUpdated();
			attemptFire();
		}
		
		private ItemStack consumeItem()
		{
			for (IInventory i : getItemInPorts())
			{
				for(int slot = 0; slot < i.getSizeInventory(); slot++)
				{
					ItemStack stack = i.getStackInSlot(slot);
					
					if(!stack.isEmpty())
					{
						return i.decrStackSize(slot, 1);
					}
				}
			}
			return ItemStack.EMPTY;
		}
		
		private int getTimeFromStack(ItemStack stack)
		{
			for(Entry<ItemStack, Integer>  i : Configuration.blackHoleGeneratorBlocks.entrySet()) {
				if(i.getKey().getItem() == stack.getItem() && i.getKey().getItemDamage() == stack.getItemDamage())
					return i.getValue();
			}
			return Configuration.defaultItemTimeBlackHole;
		}

		private void attemptFire()
		{
			if(enabled && isAroundBlackHole()) {
				if(last_usage <= this.world.getTotalWorldTime() && !isEnergyFull()) {
					ItemStack stack = consumeItem();
					if(!stack.isEmpty())
					{
						last_usage = this.world.getTotalWorldTime() + getTimeFromStack(stack);
					}
				}
			}
		}
		
		private boolean isEnergyFull() {
			MultiBattery battery = getBatteries();
			return battery.getMaxEnergyStored() == battery.getUniversalEnergyStored();
		}
		
		public boolean isProducingPower() {
			return powerMadeLastTick > 0;
		}
		
		private boolean isAroundBlackHole()
		{
			
			if(world.provider.getDimension() == Configuration.spaceDimId)
			{
				ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos);
				if(obj != null)
				{
					DimensionProperties properties = (DimensionProperties) obj.getProperties().getParentProperties();
					return properties != null && (properties.isStar() && properties.getStarData().isBlackHole());
				}
			}

			return false;
		}
		
		@Override
		public void update() {

			if(!initialCheck && !world.isRemote) {
				completeStructure = attemptCompleteStructure(world.getBlockState(pos));
				onInventoryUpdated();
				initialCheck = true;
			}

			if(!isComplete())
				return;

			if(!world.isRemote) {
				if(isAroundBlackHole())
				{
					float energyRecieved = 0;
					
					
					//Check to see if we're ready for another injection
					attemptFire();
					
					energyRecieved = last_usage > this.world.getTotalWorldTime() ? 500f : 0f;
					powerMadeLastTick = (int) (energyRecieved*Configuration.blackHolePowerMultiplier);

					if(powerMadeLastTick != prevPowerMadeLastTick) {
						prevPowerMadeLastTick = powerMadeLastTick;
						PacketHandler.sendToNearby(new PacketMachine(this, (byte)1), world.provider.getDimension(),pos, 128);

					}
					producePower(powerMadeLastTick);
				}
				
			}
			if(world.isRemote)
				textModule.setText(LibVulpes.proxy.getLocalizedString("msg.microwaverec.generating") + ": " + powerMadeLastTick + " " + LibVulpes.proxy.getLocalizedString("msg.powerunit.rfpertick") +
						"\n\nStatus: " + (isAroundBlackHole() ? "ready" : "No black hole"));
		}


		@Override
		public SPacketUpdateTileEntity getUpdatePacket() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("canRender", canRender);
			nbt.setInteger("amtPwr", powerMadeLastTick);
			writeNetworkData(nbt);
			return new SPacketUpdateTileEntity(pos, 0, nbt);
		}

		@Override
		public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
			NBTTagCompound nbt = pkt.getNbtCompound();

			canRender = nbt.getBoolean("canRender");
			powerMadeLastTick = nbt.getInteger("amtPwr");
			readNetworkData(nbt);
		}

		@Override
		public NBTTagCompound getUpdateTag() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("canRender", canRender);
			nbt.setInteger("amtPwr", powerMadeLastTick);
			writeToNBT(nbt);
			return nbt;
		}

		@Override
		public void handleUpdateTag(NBTTagCompound nbt) {
			powerMadeLastTick = nbt.getInteger("amtPwr");
			canRender = nbt.getBoolean("canRender");
			readNetworkData(nbt);
		}



		@Override
		public void writeDataToNetwork(ByteBuf out, byte id) {
			super.writeDataToNetwork(out, id);

			if(id == 1) {
				out.writeInt(powerMadeLastTick);
			}
		}

		@Override
		public void readDataFromNetwork(ByteBuf in, byte packetId,
				NBTTagCompound nbt) {
			super.readDataFromNetwork(in, packetId, nbt);	

			if(packetId == 1) {
				nbt.setInteger("amtPwr", in.readInt());
			}
		}

		@Override
		public void useNetworkData(EntityPlayer player, Side side, byte id,
				NBTTagCompound nbt) {
			super.useNetworkData(player, side, id, nbt);

			if(id == 1) {
				powerMadeLastTick = nbt.getInteger("amtPwr");
			}
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			super.writeToNBT(nbt);
			return nbt;
		}

		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			super.readFromNBT(nbt);

		}
}
