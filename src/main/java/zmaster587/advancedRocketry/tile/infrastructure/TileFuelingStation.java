package zmaster587.advancedRocketry.tile.infrastructure;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.block.BlockTileRedstoneEmitter;
import zmaster587.advancedRocketry.tile.TileRocketAssemblingMachine;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TileInventoriedRFConsumerTank;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.IconResource;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileFuelingStation extends TileInventoriedRFConsumerTank implements IModularInventory, IMultiblock, IInfrastructure, ILinkableTile, INetworkMachine, IButtonInventory {
    private EntityRocketBase linkedRocket;
    private HashedBlockPosition masterBlock;
    private ModuleRedstoneOutputButton redstoneControl;
    private RedstoneState state;

    public TileFuelingStation() {
        super(1000, 3, 5000);
        masterBlock = new HashedBlockPosition(0, -1, 0);
        redstoneControl = new ModuleRedstoneOutputButton(174, 4, 0, "", this);
        state = RedstoneState.ON;
    }

    @Override
    public int getMaxLinkDistance() {
        return 10;
    }

    private void setRedstoneState(boolean condition) {
        if (state == RedstoneState.INVERTED)
            condition = !condition;
        else if (state == RedstoneState.OFF)
            condition = false;
        ((BlockTileRedstoneEmitter) AdvancedRocketryBlocks.blockFuelingStation).setRedstoneState(world, world.getBlockState(pos), pos, condition);

    }

    @Override
    public void performFunction() {
        if (!world.isRemote) {
            //Lock rocket to a specific fluid so that it has only one oxidizer/bipropellant/monopropellant/etc
            FluidStack currentFluidStack = tank.getFluid();
            if (currentFluidStack != null) {
                Fluid currentFluid = currentFluidStack.getFluid();

                //Check to see if we should set the rocket fuel
                if (linkedRocket.stats.getFuelFluid().equals("null")) {
                    if ((FuelRegistry.instance.isFuel(FuelType.LIQUID_MONOPROPELLANT, currentFluid) && linkedRocket.getFuelCapacity(FuelType.LIQUID_MONOPROPELLANT) > 0) || (FuelRegistry.instance.isFuel(FuelType.LIQUID_BIPROPELLANT, currentFluid) && linkedRocket.getFuelCapacity(FuelType.LIQUID_BIPROPELLANT) > 0))
                        linkedRocket.stats.setFuelFluid(currentFluid.getName());
                }
                if (linkedRocket.stats.getOxidizerFluid().equals("null")) {
                    if (FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, currentFluid))
                        linkedRocket.stats.setOxidizerFluid(currentFluid.getName());
                }
                if (linkedRocket.stats.getWorkingFluid().equals("null")) {
                    if (FuelRegistry.instance.isFuel(FuelType.NUCLEAR_WORKING_FLUID, currentFluid))
                        linkedRocket.stats.setWorkingFluid(currentFluid.getName());
                }

                //Actually fill the fuel if that is the case
                if (currentFluid == FluidRegistry.getFluid(linkedRocket.stats.getFuelFluid()) || currentFluid == FluidRegistry.getFluid(linkedRocket.stats.getOxidizerFluid()) || currentFluid == FluidRegistry.getFluid(linkedRocket.stats.getWorkingFluid())) {
                    if (linkedRocket.getRocketFuelType() == FuelType.LIQUID_BIPROPELLANT && FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, currentFluid)) {
                        int fuelRate = (int) (FuelRegistry.instance.getMultiplier(FuelType.LIQUID_OXIDIZER, currentFluid) * linkedRocket.stats.getBaseFuelRate(FuelType.LIQUID_OXIDIZER));
                        tank.drain(linkedRocket.addFuelAmount(FuelType.LIQUID_OXIDIZER, ARConfiguration.getCurrentConfig().fuelPointsPer10Mb), true);
                        linkedRocket.setFuelConsumptionRate(FuelType.LIQUID_OXIDIZER, fuelRate);
                    } else {
                        int fuelRate = (int) (FuelRegistry.instance.getMultiplier(linkedRocket.getRocketFuelType(), currentFluid) * linkedRocket.stats.getBaseFuelRate(linkedRocket.getRocketFuelType()));
                        tank.drain(linkedRocket.addFuelAmount(linkedRocket.getRocketFuelType(), ARConfiguration.getCurrentConfig().fuelPointsPer10Mb), true);
                        linkedRocket.setFuelConsumptionRate(linkedRocket.getRocketFuelType(), fuelRate);
                    }

                }

                //If the rocket is full then emit redstone
                setRedstoneState(!canRocketFitFluid(currentFluid));
            }
        }
        useBucket(0, inventory.getStackInSlot(0));
    }

    @Override
    public int getPowerPerOperation() {
        return 30;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, getBlockMetadata(), getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }


    @Override
    public boolean canPerformFunction() {
        return linkedRocket != null && (tank.getFluid() != null && tank.getFluidAmount() > 9 && canRocketFitFluid(tank.getFluid().getFluid()));
    }

    @Override
    public boolean canFill(Fluid fluid) {
        return FuelRegistry.instance.isFuel(FuelType.LIQUID_MONOPROPELLANT, fluid) || FuelRegistry.instance.isFuel(FuelType.NUCLEAR_WORKING_FLUID, fluid) || FuelRegistry.instance.isFuel(FuelType.LIQUID_BIPROPELLANT, fluid) || FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, fluid);
    }


    /**
     * @param fluid the fluid to check whether the rocket has space for it
     * @return boolean on whether the rocket can accept the fluid
     */
    public boolean canRocketFitFluid(Fluid fluid) {
        return canFill(fluid) && ((linkedRocket.getRocketFuelType() == FuelType.LIQUID_BIPROPELLANT && FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, fluid)) ? linkedRocket.getFuelCapacity(FuelType.LIQUID_OXIDIZER) > linkedRocket.getFuelAmount(FuelType.LIQUID_OXIDIZER) : linkedRocket.getFuelCapacity(linkedRocket.getRocketFuelType()) > linkedRocket.getFuelAmount(linkedRocket.getRocketFuelType()));
    }


    @Override
    public String getModularInventoryName() {
        return AdvancedRocketryBlocks.blockFuelingStation.getLocalizedName();
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {

        super.setInventorySlotContents(slot, stack);
        while (useBucket(0, getStackInSlot(0))) ;

    }

    /**
     * Handles internal bucket tank interaction
     *
     * @param slot  integer slot to insert into
     * @param stack the itemstack to work fluid handling on
     * @return boolean on whether the fluid stack was successfully filled from or not, returns false if the stack cannot be extracted from or has no fluid left
     */
    private boolean useBucket(int slot, @Nonnull ItemStack stack) {
        return FluidUtils.attemptDrainContainerIInv(inventory, tank, stack, 0, 1);
    }

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP)) {
            FluidStack fstack = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP).getTankProperties()[0].getContents();
            return fstack != null && canFill(fstack.getFluid());
        }
        return false;
    }

    @Override
    public void unlinkRocket() {
        this.linkedRocket = null;
        ((BlockTileRedstoneEmitter) AdvancedRocketryBlocks.blockFuelingStation).setRedstoneState(world, world.getBlockState(pos), pos, false);

    }

    @Override
    public boolean disconnectOnLiftOff() {
        return true;
    }

    @Override
    public boolean linkRocket(EntityRocketBase rocket) {
        this.linkedRocket = rocket;
        if (tank.getFluid() != null)
            setRedstoneState(!canRocketFitFluid(tank.getFluid().getFluid()));
        return true;
    }

    @Override
    public boolean onLinkStart(@Nonnull ItemStack item, TileEntity entity,
                               EntityPlayer player, World world) {

        ItemLinker.setMasterCoords(item, pos);

        if (this.linkedRocket != null) {
            this.linkedRocket.unlinkInfrastructure(this);
            this.unlinkRocket();
        }

        if (player.world.isRemote)
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new TextComponentString(LibVulpes.proxy.getLocalizedString("msg.fuelingStation.link") + ": " + this.pos.getX() + " " + this.pos.getY() + " " + this.pos.getZ())));
        return true;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (getMasterBlock() instanceof TileRocketAssemblingMachine)
            ((TileRocketAssemblingMachine) getMasterBlock()).removeConnectedInfrastructure(this);

        //Mostly for client rendering stuff
        if (linkedRocket != null)
            linkedRocket.unlinkInfrastructure(this);
    }

    @Override
    public boolean onLinkComplete(@Nonnull ItemStack item, TileEntity entity,
                                  EntityPlayer player, World world) {
        if (player.world.isRemote)
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("msg.linker.error.firstMachine"));
        return false;
    }

    @Override
    @Nonnull
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.DOWN)
            return new int[]{1};
        return new int[]{0};
    }

    @Override
    public List<ModuleBase> getModules(int ID, EntityPlayer player) {
        List<ModuleBase> list = new ArrayList<>();

        list.add(new ModulePower(156, 12, this));
        list.add(new ModuleSlotArray(45, 18, this, 0, 1));
        list.add(new ModuleSlotArray(45, 54, this, 1, 2));
        list.add(redstoneControl);

        if (world.isRemote)
            list.add(new ModuleImage(44, 35, new IconResource(194, 0, 18, 18, CommonResources.genericBackground)));
        list.add(new ModuleLiquidIndicator(27, 18, this));

        return list;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean canInteractWithContainer(EntityPlayer entity) {
        return true;
    }

    @Override
    public boolean linkMission(IMission mission) {
        return false;
    }

    @Override
    public void unlinkMission() {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("redstoneState", (byte) state.ordinal());
        if (hasMaster()) {
            nbt.setIntArray("masterPos", new int[]{masterBlock.x, masterBlock.y, masterBlock.z});
        }
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        state = RedstoneState.values()[nbt.getByte("redstoneState")];
        redstoneControl.setRedstoneState(state);

        if (nbt.hasKey("masterPos")) {
            int[] pos = nbt.getIntArray("masterPos");
            setMasterBlock(new BlockPos(pos[0], pos[1], pos[2]));
        }
    }

    @Override
    public boolean hasMaster() {
        return masterBlock.y > -1;
    }

    @Override
    public TileEntity getMasterBlock() {
        return world.getTileEntity(new BlockPos(masterBlock.x, masterBlock.y, masterBlock.z));
    }

    @Override
    public void setMasterBlock(BlockPos pos) {
        masterBlock = new HashedBlockPosition(pos);
    }

    @Override
    public void setComplete(BlockPos pos) {

    }

    @Override
    public void setIncomplete() {
        masterBlock.y = -1;
    }

    public boolean canRenderConnection() {
        return true;
    }

    @Override
    public void onInventoryButtonPressed(int buttonId) {
        state = redstoneControl.getState();
        PacketHandler.sendToServer(new PacketMachine(this, (byte) 0));
    }

    @Override
    public void writeDataToNetwork(ByteBuf out, byte id) {
        out.writeByte(state.ordinal());
    }

    @Override
    public void readDataFromNetwork(ByteBuf in, byte packetId,
                                    NBTTagCompound nbt) {
        nbt.setByte("state", in.readByte());
    }

    @Override
    public void useNetworkData(EntityPlayer player, Side side, byte id,
                               NBTTagCompound nbt) {
        state = RedstoneState.values()[nbt.getByte("state")];

        if (linkedRocket != null && tank.getFluid() != null)
            setRedstoneState(!canRocketFitFluid(tank.getFluid().getFluid()));
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }
}
