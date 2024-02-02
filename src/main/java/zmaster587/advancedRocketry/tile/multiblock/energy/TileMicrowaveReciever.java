package zmaster587.advancedRocketry.tile.multiblock.energy;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IUniversalEnergyTransmitter;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerProducer;
import zmaster587.libVulpes.util.Vector3F;

import java.util.LinkedList;
import java.util.List;

public class TileMicrowaveReciever extends TileMultiPowerProducer implements ITickable {

    static final BlockMeta iron_block = new BlockMeta(AdvancedRocketryBlocks.blockSolarPanel);
    static final Object[][][] structure = new Object[][][]{
            {
                    {iron_block, '*', '*', '*', iron_block},
                    {'*', iron_block, iron_block, iron_block, '*'},
                    {'*', iron_block, 'c', iron_block, '*'},
                    {'*', iron_block, iron_block, iron_block, '*'},
                    {iron_block, '*', '*', '*', iron_block},
            }};

    List<Long> connectedSatellites;
    boolean initialCheck;
    double insolationPowerMultiplier;
    int powerSourceDimensionID;
    int powerMadeLastTick, prevPowerMadeLastTick;
    ModuleText textModule;

    public TileMicrowaveReciever() {
        connectedSatellites = new LinkedList<>();
        initialCheck = false;
        insolationPowerMultiplier = 0;
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
        return false;
    }

    @Override
    public Object[][][] getStructure() {
        return structure;
    }

    @Override
    public List<BlockMeta> getAllowableWildCardBlocks() {
        List<BlockMeta> blocks = super.getAllowableWildCardBlocks();

        blocks.addAll(TileMultiBlock.getMapping('I'));
        blocks.add(iron_block);
        blocks.addAll(TileMultiBlock.getMapping('p'));

        return blocks;
    }

    @Override
    public String getMachineName() {
        return AdvancedRocketryBlocks.blockMicrowaveReciever.getLocalizedName();
    }

    public int getPowerMadeLastTick() {
        return powerMadeLastTick;
    }

    @Override
    public void onInventoryUpdated() {
        super.onInventoryUpdated();

        List<Long> list = new LinkedList<>();

        for (IInventory inv : itemInPorts) {
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof ItemSatelliteIdentificationChip) {
                    list.add(SatelliteRegistry.getSatelliteId(stack));
                }
            }
        }


        connectedSatellites = list;

    }

    @Override
    public void update() {

        if (!initialCheck && !world.isRemote) {
            completeStructure = attemptCompleteStructure(world.getBlockState(pos));
            onInventoryUpdated();
            initialCheck = true;
        }

        //Checks whenever a station changes dimensions or when the multiblock is intialized - ie any time the multipler could concieveably change
        if (insolationPowerMultiplier == 0 || ((world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) && (powerSourceDimensionID != SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos).getOrbitingPlanetId()))) {
            DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension());
            insolationPowerMultiplier = (world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) ? SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos).getInsolationMultiplier() : properties.getPeakInsolationMultiplierWithoutAtmosphere();
            //Sets the ID of the place it's sourcing power from so it does not have to recheck
            if (world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId)
                powerSourceDimensionID = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos).getOrbitingPlanetId();
        }
        if (!isComplete())
            return;

        //Periodically check for obstructing blocks above the panel
        if (!world.isRemote && getPowerMadeLastTick() > 0 && world.getTotalWorldTime() % 100 == 0) {
            Vector3F<Integer> offset = getControllerOffset(getStructure());


            List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.getPos().getX() - offset.x, this.getPos().getY(), this.getPos().getZ() - offset.z, this.getPos().getX() - offset.x + getStructure()[0][0].length, 256, this.getPos().getZ() - offset.z + getStructure()[0].length));

            for (Entity e : entityList) {
                e.setFire(powerMadeLastTick / 10);
            }

            for (int x = 0; x < getStructure()[0][0].length; x++) {
                for (int z = 0; z < getStructure()[0].length; z++) {

                    BlockPos pos2;
                    IBlockState state = world.getBlockState(pos2 = (world.getHeight(pos.add(x - offset.x, 128, z - offset.z)).add(0, -1, 0)));

                    if (pos2.getY() > this.getPos().getY()) {
                        if (!world.isAirBlock(pos2.add(0, 1, 0))) {
                            world.setBlockToAir(pos2);
                            world.playSound(pos2.getX(), pos2.getY(), pos2.getZ(), new SoundEvent(new ResourceLocation("fire.fire")), SoundCategory.BLOCKS, 1f, 3f, false);
                        }
                    }
                }
            }
        }

        DimensionProperties properties;
        int dimId = world.provider.getDimension();
        SpaceStationObject spaceStation = (SpaceStationObject) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos);
        if (!world.isRemote && (DimensionManager.getInstance().isDimensionCreated(dimId) || world.provider.getDimension() == 0)) {
            //This way we check to see if it's on a station, and if so, if it has any satellites in orbit around the planet the station is around to pull from
            properties = (spaceStation != null) ? spaceStation.getOrbitingPlanet() : DimensionManager.getInstance().getDimensionProperties(dimId);
            int energyReceived = 0;
            if (enabled) {
                for (long lng : connectedSatellites) {
                    SatelliteBase satellite = properties.getSatellite(lng);

                    if (satellite instanceof IUniversalEnergyTransmitter) {
                        energyReceived += ((IUniversalEnergyTransmitter) satellite).transmitEnergy(EnumFacing.UP, false);
                    }
                }

                //Multiplied by two for 520W = 1 RF/t becoming 2 RF/t @ 100% efficiency, and by insolation mult for solar stuff
                energyReceived *= 2 * insolationPowerMultiplier;
            }
            powerMadeLastTick = energyReceived;

            if (powerMadeLastTick != prevPowerMadeLastTick) {
                prevPowerMadeLastTick = powerMadeLastTick;
                PacketHandler.sendToNearby(new PacketMachine(this, (byte) 1), world.provider.getDimension(), pos, 128);

            }
            producePower(powerMadeLastTick);
        }
        if (world.isRemote)
            textModule.setText(LibVulpes.proxy.getLocalizedString("msg.microwaverec.generating") + " " + powerMadeLastTick + " " + LibVulpes.proxy.getLocalizedString("msg.powerunit.rfpertick"));
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

        if (id == 1) {
            out.writeInt(powerMadeLastTick);
        }
    }

    @Override
    public void readDataFromNetwork(ByteBuf in, byte packetId, NBTTagCompound nbt) {
        super.readDataFromNetwork(in, packetId, nbt);

        if (packetId == 1) {
            nbt.setInteger("amtPwr", in.readInt());
        }
    }

    @Override
    public void useNetworkData(EntityPlayer player, Side side, byte id, NBTTagCompound nbt) {
        super.useNetworkData(player, side, id, nbt);

        if (id == 1) {
            powerMadeLastTick = nbt.getInteger("amtPwr");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        int[] intArray = new int[connectedSatellites.size() * 2];

        for (int i = 0; i < connectedSatellites.size() * 2; i += 2) {
            intArray[i] = (connectedSatellites.get(i / 2)).intValue();
            intArray[i + 1] = (int) ((connectedSatellites.get(i / 2) >>> 32));
        }

        nbt.setIntArray("satilliteList", intArray);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        int[] intArray = nbt.getIntArray("satilliteList");
        connectedSatellites.clear();
        for (int i = 0; i < intArray.length / 2; i += 2) {
            connectedSatellites.add(intArray[i] | (((long) intArray[i + 1]) << 32));
        }

    }

}
