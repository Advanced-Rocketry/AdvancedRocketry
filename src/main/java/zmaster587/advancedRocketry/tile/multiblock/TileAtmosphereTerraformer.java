package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.item.ItemBiomeChanger;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.satellite.SatelliteBiomeChanger;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine.NetworkPackets;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.IconResource;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

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
						{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      null,                                    LibVulpesBlocks.blockAdvStructureBlock,   Blocks.CLAY,                          LibVulpesBlocks.blockAdvStructureBlock,                      Blocks.CLAY,                             LibVulpesBlocks.blockAdvStructureBlock,   null,                                      null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       null,               null,              null,           null},
						{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
						{null,         Blocks.CLAY,   Blocks.CLAY,       Blocks.CLAY,        LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       Blocks.CLAY,        Blocks.CLAY,       Blocks.CLAY,    null},
						{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
						{null,         Blocks.CLAY,   Blocks.CLAY,       Blocks.CLAY,        LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       Blocks.CLAY,        Blocks.CLAY,       Blocks.CLAY,    null},
						{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
						{null,         null,          null,              null,               LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      null,                                    LibVulpesBlocks.blockAdvStructureBlock,   Blocks.CLAY,                          LibVulpesBlocks.blockAdvStructureBlock,                      Blocks.CLAY,                             LibVulpesBlocks.blockAdvStructureBlock,   null,                                      null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
						{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null}},

						{   {null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      null,                                    LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   null,                                      null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       null,               null,              null,           null},
							{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
							{Blocks.CLAY,  Blocks.CLAY,   null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              Blocks.CLAY,    Blocks.CLAY},
							{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
							{Blocks.CLAY,  Blocks.CLAY,   null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              Blocks.CLAY,    Blocks.CLAY},
							{null,         null,          null,              LibVulpesBlocks.blockAdvStructureBlock,  LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,  null,              null,           null},
							{null,         null,          null,              null,               LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         LibVulpesBlocks.blockAdvStructureBlock,                       null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                         null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      null,                                    LibVulpesBlocks.blockAdvStructureBlock,   LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       LibVulpesBlocks.blockAdvStructureBlock,   null,                                      null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
							{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null}},

							{   {null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{Blocks.CLAY,  Blocks.CLAY,   null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              Blocks.CLAY,    Blocks.CLAY},
								{null,         null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           null},
								{Blocks.CLAY,  Blocks.CLAY,   null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              Blocks.CLAY,    Blocks.CLAY},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
								{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null}},

								{   {null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{Blocks.CLAY,  null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           Blocks.CLAY},
									{null,         null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           null},
									{Blocks.CLAY,  null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           Blocks.CLAY},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
									{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null}},

									{   {null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{Blocks.CLAY,  null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    'c',                                    LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           Blocks.CLAY},
										{null,         null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           null},
										{Blocks.CLAY,  null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           Blocks.CLAY},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
										{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null}},

										{   {null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{Blocks.CLAY,  null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    LibVulpesBlocks.blockAdvStructureBlock,                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           Blocks.CLAY},
											{null,         null,          null,              null,               null,                                      null,                                    null,                'P',                    LibVulpesBlocks.blockAdvStructureBlock,                      'P',                       null,                null,                                      null,                                    null,               null,              null,           null},
											{Blocks.CLAY,  null,          null,              null,               null,                                      null,                                    null,                LibVulpesBlocks.blockAdvStructureBlock,                    'P',                      LibVulpesBlocks.blockAdvStructureBlock,                       null,                null,                                      null,                                    null,               null,              null,           Blocks.CLAY},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
											{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null}},

											{   {null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      null,              AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   null,                                      null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,                    AdvancedRocketryBlocks.blockFuelTank,                      AdvancedRocketryBlocks.blockFuelTank,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,                    AdvancedRocketryBlocks.blockFuelTank,                      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       null,               null,              null,           null},
												{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockConcrete,      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,  null,              null,           null},
												{Blocks.CLAY,  null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,  null,              null,           Blocks.CLAY},
												{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,  null,              null,           null},
												{Blocks.CLAY,  null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,  null,              null,           Blocks.CLAY},
												{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockConcrete,      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,  null,              null,           null},
												{null,         null,          null,              null,               AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      null,                                    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   null,                                      null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
												{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null}},

												{   {null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      null,                                    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   null,                                      null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       null,  null,              null,           null},
													{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,  null, null,           null},
													{Blocks.CLAY,  Blocks.CLAY,   Blocks.CLAY,       AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,  Blocks.CLAY,       Blocks.CLAY,    Blocks.CLAY},
													{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,  null, null,           null},
													{Blocks.CLAY,  Blocks.CLAY,   Blocks.CLAY,       AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,  Blocks.CLAY,       Blocks.CLAY,    Blocks.CLAY},
													{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,  null, null,           null},
													{null,         null,          null,              null,               AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       null,  null,              null,           null},
													{null,         null,          null,              null,               null,                                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      null,                                    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   null,                                      null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
													{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null}},

													{   {null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      null,                                    AdvancedRocketryBlocks.blockConcrete,   Blocks.CLAY,                          'L',                                    Blocks.CLAY,                             AdvancedRocketryBlocks.blockConcrete,   null,                                      null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       null,               null,              null,           null},
														{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,  null,              null,           null},
														{null,         Blocks.CLAY,   Blocks.CLAY,       Blocks.CLAY,        AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    Blocks.CLAY,        Blocks.CLAY,       Blocks.CLAY,    null},
														{null,         null,          null,              'L',                AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    'L',                null,              null,           null},
														{null,         Blocks.CLAY,   Blocks.CLAY,       Blocks.CLAY,        AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank,      AdvancedRocketryBlocks.blockFuelTank,    Blocks.CLAY,        Blocks.CLAY,       Blocks.CLAY,    null},
														{null,         null,          null,              AdvancedRocketryBlocks.blockConcrete,  AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                    AdvancedRocketryBlocks.blockConcrete,                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,  null,              null,           null},
														{null,         null,          null,              null,               AdvancedRocketryBlocks.blockConcrete,      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         AdvancedRocketryBlocks.blockConcrete,                       null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      AdvancedRocketryBlocks.blockConcrete,                       AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockFuelTank, AdvancedRocketryBlocks.blockFuelTank,   AdvancedRocketryBlocks.blockFuelTank,    AdvancedRocketryBlocks.blockConcrete,   AdvancedRocketryBlocks.blockConcrete,                         null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      null,                                    AdvancedRocketryBlocks.blockConcrete,   Blocks.CLAY,                          'L',                                    Blocks.CLAY,                             AdvancedRocketryBlocks.blockConcrete,   null,                                      null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      null,                                    null,                Blocks.CLAY,                          null,                                   Blocks.CLAY,                             null,                null,                                      null,                                    null,               null,              null,           null},
														{null,         null,          null,              null,               null,                                      null,                                    null,                null,                                 null,                                   null,                                    null,                null,                                      null,                                    null,               null,              null,           null}}};


	public TileAtmosphereTerraformer() {
		completionTime = (int) (18000 * ARConfiguration.getCurrentConfig().terraformSpeed);
		buttonIncrease = new ModuleToggleSwitch(40, 20, 1, LibVulpes.proxy.getLocalizedString("msg.terraformer.atminc"), this, TextureResources.buttonScan, 80, 16,true);
		buttonDecrease = new ModuleToggleSwitch(40, 38, 2, LibVulpes.proxy.getLocalizedString("msg.terraformer.atmdec"), this, TextureResources.buttonScan, 80, 16, false);
		text = new ModuleText(10, 100, "", 0x282828);
		powerPerTick = 1000;

		List<ModuleToggleSwitch> buttons = new LinkedList<>();
		buttons.add(buttonIncrease);
		buttons.add(buttonDecrease);
		radioButton = new ModuleRadioButton(this, buttons);
		inv = new EmbeddedInventory(1);
		outOfFluid = false;
	}

	private int getCompletionTime() {
		return (int) (18000 * ARConfiguration.getCurrentConfig().terraformSpeed);
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules =  super.getModules(ID, player);
		
		//Backgrounds
		if(world.isRemote) {
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

		text.setText(String.format("%s:\n%s\n\n%s: %.2f" , LibVulpes.proxy.getLocalizedString("msg.terraformer.status"), statusText, LibVulpes.proxy.getLocalizedString("msg.terraformer.pressure"), DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getAtmosphereDensity()/100f));
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-15,-15,-15), pos.add(15,15,15));
	}

	@Override
	protected void onRunningPoweredTick() {
		super.onRunningPoweredTick();

		if(world.isRemote) {
			if(Minecraft.getMinecraft().gameSettings.particleSetting < 2) {
				EnumFacing dir = RotatableBlock.getFront(world.getBlockState(pos)).getOpposite();

				if(radioButton.getOptionSelected() == 0) {
					if(world.getTotalWorldTime() % 20 == 0) {
						float xMot = (float) ((0.5f - world.rand.nextGaussian())/40f);
						float zMot = (float) ((0.5f - world.rand.nextGaussian())/40f);
						BlockPos offsetPos = pos.offset(dir);
						AdvancedRocketry.proxy.spawnParticle("rocketSmoke", world, offsetPos.getX() + 5, pos.getY() + 7, offsetPos.getZ() + 0.5, xMot, 0.02f, zMot);
						AdvancedRocketry.proxy.spawnParticle("rocketSmoke", world, offsetPos.getX() - 4, pos.getY() + 7, offsetPos.getZ() + 0.5, xMot, 0.02f, zMot);
						AdvancedRocketry.proxy.spawnParticle("rocketSmoke", world, offsetPos.getX() + 0.5f, pos.getY() + 7, offsetPos.getZ() - 4, xMot, 0.02f, zMot);
						AdvancedRocketry.proxy.spawnParticle("rocketSmoke", world, offsetPos.getX() + 0.5f, pos.getY() + 7, offsetPos.getZ() + 5, xMot, 0.02f, zMot);
					}
				}
				else {
					float xMot = (float) ((0.5f - world.rand.nextGaussian())/4f);
					float yMot = (float) (world.rand.nextGaussian()/20f);
					float zMot = (float) ((0.5f - world.rand.nextGaussian())/4f);
					BlockPos offsetPos = pos.offset(dir);
					AdvancedRocketry.proxy.spawnParticle("rocketSmokeInverse", world, offsetPos.getX() + 5, pos.getY() + 7, offsetPos.getZ() + 0.5, xMot, 0.4f + yMot, zMot);
					AdvancedRocketry.proxy.spawnParticle("rocketSmokeInverse", world, offsetPos.getX() - 4, pos.getY() + 7, offsetPos.getZ() + 0.5, xMot, 0.4f + yMot, zMot);
					AdvancedRocketry.proxy.spawnParticle("rocketSmokeInverse", world, offsetPos.getX() + 0.5f, pos.getY() + 7, offsetPos.getZ() - 4, xMot, 0.4f + yMot, zMot);
					AdvancedRocketry.proxy.spawnParticle("rocketSmokeInverse", world, offsetPos.getX() + 0.5f, pos.getY() + 7, offsetPos.getZ() + 5, xMot, 0.4f + yMot, zMot);
				}
			}
		}

		if(!ARConfiguration.getCurrentConfig().terraformRequiresFluid)
			return;

		if(!world.isRemote) {
			int requiredN2 = ARConfiguration.getCurrentConfig().terraformliquidRate, requiredO2 =  ARConfiguration.getCurrentConfig().terraformliquidRate;

			for(IFluidHandler handler : fluidInPorts) {
				FluidStack fStack = handler.drain(new FluidStack(AdvancedRocketryFluids.fluidNitrogen, requiredN2), true);

				if(fStack != null)
					requiredN2 -= fStack.amount;

				fStack = handler.drain(new FluidStack(AdvancedRocketryFluids.fluidOxygen, requiredO2), true);

				if(fStack != null)
					requiredO2 -= fStack.amount;
			}

			if(!world.isRemote) {
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

	public SoundEvent getSound() {
		return AudioRegistry.machineLarge;
	}

	@Override
	public int getSoundDuration() {
		return 80;
	}

	private boolean hasValidBiomeChanger() {
		ItemStack biomeChanger = inv.getStackInSlot(0);
		SatelliteBase satellite;
				
		return !biomeChanger.isEmpty() && (biomeChanger.getItem() instanceof ItemBiomeChanger) && SatelliteRegistry.getSatellite(biomeChanger) != null &&
				(satellite = ((ItemSatelliteIdentificationChip)AdvancedRocketryItems.itemBiomeChanger).getSatellite(biomeChanger)).getDimensionId() == world.provider.getDimension() &&
				satellite instanceof SatelliteBiomeChanger;
	}

	@Override
	protected void playMachineSound(SoundEvent event) {
		world.playSound(getPos().getX(), getPos().getY() + 7, getPos().getZ(), event, SoundCategory.BLOCKS, Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.BLOCKS),  0.975f + world.rand.nextFloat()*0.05f, false);
	}

	@Override
	public boolean isRunning() {
		boolean bool = getMachineEnabled() && super.isRunning() && zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableTerraforming;

		if(!bool)
			currentTime = 0;

		return bool;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new SPacketUpdateTileEntity(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.getNbtCompound();
		readFromNBT(nbt);
		setText();
		
	}

	@Override
	protected void processComplete() {
		super.processComplete();
		completionTime = getCompletionTime();

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension());
		if( !world.isRemote && properties != null && properties.getId() == world.provider.getDimension() && ((world.provider.getClass().equals(WorldProviderPlanet.class) && 
				properties.isNativeDimension) || ARConfiguration.getCurrentConfig().allowTerraformNonAR) ) {
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
			radioButton.setOptionSelected(in.readByte());
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
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		super.useNetworkData(player, side, id, nbt);
		if(!world.isRemote && id == NetworkPackets.TOGGLE.ordinal()) {
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("selected", radioButton.getOptionSelected());
		inv.writeToNBT(nbt);

		nbt.setBoolean("oofluid", outOfFluid);
		
		return nbt;

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
		return AdvancedRocketryBlocks.blockAtmosphereTerraformer.getLocalizedName();
	}

	@Override
	public String getName() {
		return getMachineName();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = inv.removeStackFromSlot(index);
		if(world.isRemote)
			setText();
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
		if(world.isRemote)
			setText();
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
		return inv.isItemValidForSlot(index, stack);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

}
