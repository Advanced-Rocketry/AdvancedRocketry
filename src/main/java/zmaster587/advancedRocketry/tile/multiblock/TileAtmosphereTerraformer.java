package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.fx.FxSystemElectricArc;
import zmaster587.advancedRocketry.item.ItemBiomeChanger;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.satellite.SatelliteBiomeChanger;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleImage;
import zmaster587.libVulpes.inventory.modules.ModuleLimitedSlotArray;
import zmaster587.libVulpes.inventory.modules.ModuleLiquidIndicator;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleRadioButton;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.inventory.modules.ModuleToggleSwitch;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine.NetworkPackets;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.IconResource;

public class TileAtmosphereTerraformer extends TileMultiPowerConsumer implements IInventory {

	private ModuleToggleSwitch buttonIncrease, buttonDecrease;
	private ModuleRadioButton radioButton;
	private ModuleText text;
	private EmbeddedInventory inv;
	private boolean outOfFluid;

	private static final Object[][][] structure = new Object[][][]{                                                                                                                                                                                                                                                                                                        
		{   {null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 AdvancedRocketryBlocks.blockOxygenVent,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      AdvancedRocketryBlocks.blockOxygenVent,                                    LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   AdvancedRocketryBlocks.blockOxygenVent,                                      null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               AdvancedRocketryBlocks.blockOxygenVent,    LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         AdvancedRocketryBlocks.blockOxygenVent,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      AdvancedRocketryBlocks.blockOxygenVent,                                    LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   AdvancedRocketryBlocks.blockOxygenVent,                                      null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 AdvancedRocketryBlocks.blockOxygenVent,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
			{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null}},

			{   {null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
				{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
				{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
				{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 LibVulpesBlocks.blockAdvStructureBlock,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
				{null,         null,          null,              null,               null,                                      null,                                    LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   null,                                      null,                                    null,               null,              null,           null},
				{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
				{null,         null,          null,              null,               LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       null,               null,              null,           null},
				{null,         null,          null,              null,               LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       null,               null,              null,           null},
				{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock, LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,               null,              null,           null},
				{null,         null,          null,              null,               LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       null,               null,              null,           null},
				{null,         null,          null,              null,               LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       null,               null,              null,           null},
				{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
				{null,         null,          null,              null,               null,                                      null,                                    LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   null,                                      null,                                    null,               null,              null,           null},
				{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 LibVulpesBlocks.blockAdvStructureBlock,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
				{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
				{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
				{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null}},

				{   {null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
					{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
					{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
					{null,         null,          null,              null,               null,                                      null,                                    LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   null,                                      null,                                    null,               null,              null,           null},
					{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
					{null,         null,          null,              null,               LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       null,               null,              null,           null},
					{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
					{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
					{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
					{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
					{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
					{null,         null,          null,              null,               LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       null,               null,              null,           null},
					{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
					{null,         null,          null,              null,               null,                                      null,                                    LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   null,                                      null,                                    null,               null,              null,           null},
					{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
					{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
					{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null}},

					{   {null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      null,                                    LibVulpesBlocks.blockAdvStructureBlock,   Blocks.clay,                          LibVulpesBlocks.blockAdvStructureBlock,                      Blocks.clay,                             LibVulpesBlocks.blockAdvStructureBlock,   null,                                      null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       null,               null,              null,           null},
						{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
						{null,         Blocks.clay,   Blocks.clay,       Blocks.clay,        LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       Blocks.clay,        Blocks.clay,       Blocks.clay,    null},
						{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
						{null,         Blocks.clay,   Blocks.clay,       Blocks.clay,        LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       Blocks.clay,        Blocks.clay,       Blocks.clay,    null},
						{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
						{null,         null,          null,              null,               LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      null,                                    LibVulpesBlocks.blockAdvStructureBlock,   Blocks.clay,                          LibVulpesBlocks.blockAdvStructureBlock,                      Blocks.clay,                             LibVulpesBlocks.blockAdvStructureBlock,   null,                                      null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null}},

						{   {null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      null,                                    LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   null,                                      null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       null,               null,              null,           null},
							{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
							{Blocks.clay,  Blocks.clay,   null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              Blocks.clay,    Blocks.clay},
							{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
							{Blocks.clay,  Blocks.clay,   null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              Blocks.clay,    Blocks.clay},
							{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
							{null,         null,          null,              null,               LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      null,                                    LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   null,                                      null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null}},

							{   {null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{Blocks.clay,  Blocks.clay,   null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              Blocks.clay,    Blocks.clay},
								{null,         null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           null},
								{Blocks.clay,  Blocks.clay,   null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              Blocks.clay,    Blocks.clay},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null}},

								{   {null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{Blocks.clay,  null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           Blocks.clay},
									{null,         null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           null},
									{Blocks.clay,  null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           Blocks.clay},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null}},

									{   {null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{Blocks.clay,  null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    'c',                                    LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           Blocks.clay},
										{null,         null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           null},
										{Blocks.clay,  null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           Blocks.clay},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null}},

										{   {null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{Blocks.clay,  null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           Blocks.clay},
											{null,         null,          null,              null,               null,                                      null,                                    null,                'P',                    LibVulpesBlocks.blockAdvStructureBlock,                      'P',                       null,                null,                                      null,                                    null,               null,              null,           null},
											{Blocks.clay,  null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    'P',                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           Blocks.clay},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null}},

											{   {null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      null,              AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   null,                                      null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,                    AdvancedRocketryBlocks.blockFuelTank,                      AdvancedRocketryBlocks.blockFuelTank,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,                    AdvancedRocketryBlocks.blockFuelTank,                      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       null,               null,              null,           null},
												{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockConcrete,      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,  null,              null,           null},
												{Blocks.clay,  null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,  null,              null,           Blocks.clay},
												{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,  null,              null,           null},
												{Blocks.clay,  null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,  null,              null,           Blocks.clay},
												{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockConcrete,      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,  null,              null,           null},
												{null,         null,          null,              null,               AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      null,                                    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   null,                                      null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null}},

												{   {null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      null,                                    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   null,                                      null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       null,  null,              null,           null},
													{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,  null, null,           null},
													{Blocks.clay,  Blocks.clay,   Blocks.clay,       AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,  Blocks.clay,       Blocks.clay,    Blocks.clay},
													{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,  null, null,           null},
													{Blocks.clay,  Blocks.clay,   Blocks.clay,       AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,  Blocks.clay,       Blocks.clay,    Blocks.clay},
													{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,  null, null,           null},
													{null,         null,          null,              null,               AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       null,  null,              null,           null},
													{null,         null,          null,              null,               null,                                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      null,                                    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   null,                                      null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null}},

													{   {null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      null,                                    AdvancedRocketryBlocks.blockConcrete,   Blocks.clay,                          'L',                                    Blocks.clay,                             AdvancedRocketryBlocks.blockConcrete,   null,                                      null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       null,               null,              null,           null},
														{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,  null,              null,           null},
														{null,         Blocks.clay,   Blocks.clay,       Blocks.clay,        AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    Blocks.clay,        Blocks.clay,       Blocks.clay,    null},
														{null,         null,          null,              'L',                AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    'L',                null,              null,           null},
														{null,         Blocks.clay,   Blocks.clay,       Blocks.clay,        AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    Blocks.clay,        Blocks.clay,       Blocks.clay,    null},
														{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,  null,              null,           null},
														{null,         null,          null,              null,               AdvancedRocketryBlocks.blockConcrete,      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      null,                                    AdvancedRocketryBlocks.blockConcrete,   Blocks.clay,                          'L',                                    Blocks.clay,                             AdvancedRocketryBlocks.blockConcrete,   null,                                      null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.clay,                          null,                                   Blocks.clay,                             null,                null,                                      null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null}}};


	public TileAtmosphereTerraformer() {
		completionTime = (int) (18000 * Configuration.terraformSpeed);
		buttonIncrease = new ModuleToggleSwitch(40, 20, 1, LibVulpes.proxy.getLocalizedString("msg.terraformer.atminc"), this, TextureResources.buttonScan, 80, 16,true);
		buttonDecrease = new ModuleToggleSwitch(40, 38, 2, LibVulpes.proxy.getLocalizedString("msg.terraformer.atmdec"), this, TextureResources.buttonScan, 80, 16, false);
		text = new ModuleText(10, 100, "", 0x282828);
		powerPerTick = 1000;

		List<ModuleToggleSwitch> buttons = new LinkedList<ModuleToggleSwitch>();
		buttons.add(buttonIncrease);
		buttons.add(buttonDecrease);
		radioButton = new ModuleRadioButton(this, buttons);
		inv = new EmbeddedInventory(1);
		outOfFluid = false;
	}

	private int getCompletionTime() {
		return (int) (18000 * Configuration.terraformSpeed);
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules =  super.getModules(ID, player);
		
		//Backgrounds
		if(worldObj.isRemote) {
			modules.add(new ModuleImage(173, 0, new IconResource(90, 0, 84, 88, CommonResources.genericBackground)));
		}
		
		modules.add(radioButton);
		modules.add(new ModuleProgress(30, 57, 0, zmaster587.advancedRocketry.inventory.TextureResources.terraformProgressBar, this));
		modules.add(text);
		
		setText();

		modules.add(new ModuleLimitedSlotArray(150, 114, this, 0, 1));
		int i = 0;
		modules.add(new ModuleText(180, 10, "Gas Status", 0x282828));
		for(IFluidHandler tile : fluidInPorts) {
			modules.add(new ModuleLiquidIndicator(180 + i*16, 30, tile));
			i++;
		}

		return modules;
	}

	private void setText() {
		String statusText;
		ItemStack biomeChanger = inv.getStackInSlot(0);
		if(isRunning())
			statusText = LibVulpes.proxy.getLocalizedString("msg.terraformer.running");
		else if(!hasValidBiomeChanger())
			statusText = LibVulpes.proxy.getLocalizedString("msg.terraformer.missingbiome");
		else if(outOfFluid)
			statusText = LibVulpes.proxy.getLocalizedString("msg.terraformer.outofgas");
		else
			statusText = LibVulpes.proxy.getLocalizedString("msg.terraformer.notrunning");

		text.setText(String.format("%s:\n%s\n\n%s: %.2f" , LibVulpes.proxy.getLocalizedString("msg.terraformer.status"), statusText, LibVulpes.proxy.getLocalizedString("msg.terraformer.pressure"), DimensionManager.getInstance().getDimensionProperties(worldObj.provider.dimensionId).getAtmosphereDensity()/100f));
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -15,yCoord -15, zCoord -15, xCoord +15, yCoord + 13, zCoord + 15);
	}

	@Override
	protected void onRunningPoweredTick() {
		super.onRunningPoweredTick();

		if(worldObj.isRemote) {
			if(Minecraft.getMinecraft().gameSettings.particleSetting < 2) {
				ForgeDirection dir = RotatableBlock.getFront(this.getBlockMetadata()).getOpposite();

				if(radioButton.getOptionSelected() == 0) {
					if(worldObj.getTotalWorldTime() % 20 == 0) {
						float xMot = (float) ((0.5f - worldObj.rand.nextGaussian())/40f);
						float zMot = (float) ((0.5f - worldObj.rand.nextGaussian())/40f);
						AdvancedRocketry.proxy.spawnParticle("rocketSmoke", worldObj, xCoord + dir.offsetX + 5, yCoord + 7, zCoord + 0.5 + dir.offsetZ, xMot, 0.02f, zMot);
						AdvancedRocketry.proxy.spawnParticle("rocketSmoke", worldObj, xCoord + dir.offsetX - 4, yCoord + 7, zCoord + 0.5 + dir.offsetZ, xMot, 0.02f, zMot);
						AdvancedRocketry.proxy.spawnParticle("rocketSmoke", worldObj, xCoord + dir.offsetX + 0.5f, yCoord + 7, zCoord + dir.offsetZ - 4, xMot, 0.02f, zMot);
						AdvancedRocketry.proxy.spawnParticle("rocketSmoke", worldObj, xCoord + dir.offsetX + 0.5f, yCoord + 7, zCoord + dir.offsetZ + 5, xMot, 0.02f, zMot);
					}
				}
				else {
					float xMot = (float) ((0.5f - worldObj.rand.nextGaussian())/4f);
					float yMot = (float) (worldObj.rand.nextGaussian()/20f);
					float zMot = (float) ((0.5f - worldObj.rand.nextGaussian())/4f);
					AdvancedRocketry.proxy.spawnParticle("rocketSmokeInverse", worldObj, xCoord + dir.offsetX + 5, yCoord + 7, zCoord + 0.5 + dir.offsetZ, xMot, 0.4f + yMot, zMot);
					AdvancedRocketry.proxy.spawnParticle("rocketSmokeInverse", worldObj, xCoord + dir.offsetX - 4, yCoord + 7, zCoord + 0.5 + dir.offsetZ, xMot, 0.4f + yMot, zMot);
					AdvancedRocketry.proxy.spawnParticle("rocketSmokeInverse", worldObj, xCoord + dir.offsetX + 0.5f, yCoord + 7, zCoord + dir.offsetZ - 4, xMot, 0.4f + yMot, zMot);
					AdvancedRocketry.proxy.spawnParticle("rocketSmokeInverse", worldObj, xCoord + dir.offsetX + 0.5f, yCoord + 7, zCoord + dir.offsetZ + 5, xMot, 0.4f + yMot, zMot);
				}
			}
		}

		if(!worldObj.isRemote) {
			if(!Configuration.terraformRequiresFluid)
				return;

			int requiredN2 =  Configuration.terraformliquidRate, requiredO2 =  Configuration.terraformliquidRate;

			for(IFluidHandler handler : fluidInPorts) {
				FluidStack stack = handler.drain(ForgeDirection.UNKNOWN, new FluidStack(AdvancedRocketryFluids.fluidNitrogen, requiredN2), true);

				if(stack != null)
					requiredN2 -= stack.amount;

				stack = handler.drain(ForgeDirection.UNKNOWN, new FluidStack(AdvancedRocketryFluids.fluidOxygen, requiredO2), true);

				if(stack != null)
					requiredO2 -= stack.amount;
			}
			if(!worldObj.isRemote) {
				if(requiredN2 != 0 || requiredO2 != 0) {
					outOfFluid = true;
					this.setMachineEnabled(false);
					this.setMachineRunning(false);
					markDirty();
				}
				else if(!hasValidBiomeChanger()) {
					this.setMachineEnabled(false);
					this.setMachineRunning(false);
				}
			}
		}
	}

	@Override
	public ResourceLocation getSound() {
		return zmaster587.advancedRocketry.inventory.TextureResources.sndMachineLarge;
	}

	@Override
	public int getSoundDuration() {
		return 80;
	}

	private boolean hasValidBiomeChanger() {
		ItemStack biomeChanger = inv.getStackInSlot(0);
		SatelliteBase satellite;
				
		return biomeChanger != null && (biomeChanger.getItem() instanceof ItemBiomeChanger) && DimensionManager.getInstance().getSatellite(((ItemBiomeChanger)biomeChanger.getItem()).getSatelliteId(biomeChanger)) != null &&
				(satellite = ((ItemSatelliteIdentificationChip)AdvancedRocketryItems.itemBiomeChanger).getSatellite(biomeChanger)).getDimensionId() == worldObj.provider.dimensionId &&
				satellite instanceof SatelliteBiomeChanger;
	}

	@Override
	public boolean isRunning() {
		boolean bool = getMachineEnabled() && super.isRunning() && zmaster587.advancedRocketry.api.Configuration.allowTerraforming;

		if(!bool)
			currentTime = 0;

		return bool;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.func_148857_g();
		readFromNBT(nbt);
		setText();
		
	}

	@Override
	protected void processComplete() {
		super.processComplete();
		completionTime = getCompletionTime();

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(worldObj.provider.dimensionId);
		if( !worldObj.isRemote && properties != null && properties.getId() == worldObj.provider.dimensionId && ((worldObj.provider.getClass().equals(WorldProviderPlanet.class) && 
				properties.isNativeDimension || Configuration.allowTerraformNonAR)) ) {
			if(buttonIncrease.getState() && properties.getAtmosphereDensity() < 1600)
				properties.setAtmosphereDensity(properties.getAtmosphereDensity()+1);
			else if(buttonDecrease.getState() && properties.getAtmosphereDensity() > 0) {
				properties.setAtmosphereDensity(properties.getAtmosphereDensity()-1);
			}
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {


		if(packetId == (byte)TileMultiblockMachine.NetworkPackets.TOGGLE.ordinal()) {
			radioButton.setOptionSelected((int)in.readByte());
		}
		super.readDataFromNetwork(in, packetId, nbt);
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {

		if(id == (byte)TileMultiblockMachine.NetworkPackets.TOGGLE.ordinal()) {
			out.writeByte(radioButton.getOptionSelected());
		}
		super.writeDataToNetwork(out, id);
	}

	@Override
	public void setMachineEnabled(boolean enabled) {
		super.setMachineEnabled(enabled);

		if(getMachineEnabled())
			completionTime = getCompletionTime();
	}

	@Override
	public void setMachineRunning(boolean running) {
		super.setMachineRunning(running);
		markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		super.useNetworkData(player, side, id, nbt);
		if(!worldObj.isRemote && id == NetworkPackets.TOGGLE.ordinal()) {
			outOfFluid = false;
			setMachineRunning(isRunning());
		}
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		if(hasValidBiomeChanger()) {
			super.onInventoryButtonPressed(buttonId);
			outOfFluid = false;
			if(buttonId == 1 || buttonId == 2) {
				PacketHandler.sendToServer(new PacketMachine(this,(byte)TileMultiblockMachine.NetworkPackets.TOGGLE.ordinal()));
			}
			setText();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("selected", radioButton.getOptionSelected());
		inv.writeToNBT(nbt);
		nbt.setBoolean("oofluid", outOfFluid);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		radioButton.setOptionSelected(nbt.getInteger("selected"));
		inv.readFromNBT(nbt);
		outOfFluid = nbt.getBoolean("oofluid");
		
	}

	@Override
	public String getMachineName() {
		return "tile.atmoshereTerraformer.name";
	}

	@Override
	public String getInventoryName() {
		return getMachineName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
		if(worldObj.isRemote)
			setText();
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return inv.isItemValidForSlot(index, stack);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inv.getStackInSlotOnClosing(slot);
	}

	@Override
	public void openInventory() {
		
	}

	@Override
	public void closeInventory() {
		
	}
}
