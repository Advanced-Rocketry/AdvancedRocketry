package zmaster587.advancedRocketry.Inventory;

import zmaster587.advancedRocketry.Inventory.multiblock.ContainerMultiblock;
import zmaster587.advancedRocketry.Inventory.multiblock.GuiCrystallizer;
import zmaster587.advancedRocketry.Inventory.multiblock.GuiCuttingMachine;
import zmaster587.advancedRocketry.entity.EntityRocket;
//import zmaster587.advancedRocketry.tile.TileCrystallizer;
import zmaster587.advancedRocketry.tile.TileEntityBlastFurnace;
import zmaster587.advancedRocketry.tile.TileEntityFuelingStation;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.advancedRocketry.tile.TileSpaceLaser;
import zmaster587.advancedRocketry.tile.multiblock.TilePrecisionAssembler;
import zmaster587.advancedRocketry.tile.multiblock.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlockMachine;
import zmaster587.libVulpes.api.IUniversalEnergy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public enum guiId {
		RocketBuilder,
		Rocket,
		FuelingStation,
		BlastFurnace,
		CuttingMachine,
		SpaceLaser,
		Assembler,
		Hatch,
		PowerStorage,
		CRYSTALLIZER
	}
	
	//X coord is entity ID num if entity
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(ID  == guiId.RocketBuilder.ordinal())
		{
			return new ContainerRocketBuilder(player.inventory, (TileRocketBuilder) tile);
		}
		else if(ID == guiId.Rocket.ordinal()) {
			return new ContainerRocket(player, (EntityRocket)world.getEntityByID(x),y,z);
		}
		else if(ID == guiId.FuelingStation.ordinal()) {
			return new ContainerFuelingStation(player.inventory , (TileEntityFuelingStation) tile);
		}
		else if(ID == guiId.BlastFurnace.ordinal()) {
			return new ContainerBlastFurnace(player.inventory, (TileEntityBlastFurnace)tile);
		}
		else if(ID == guiId.SpaceLaser.ordinal()) {
			return new ContainerSpaceLaser(player.inventory, (TileSpaceLaser)tile);
		}
		else if(ID == guiId.Hatch.ordinal()) {
			return new ContainerVariableSlotNumber(player, (IInventory)tile, 0, ((IInventory)tile).getSizeInventory());
		}
		else if(ID == guiId.PowerStorage.ordinal()) {
			return new ContainerPowerStorageBox(player.inventory, (IUniversalEnergy)tile);
		}
		else if(ID == guiId.CRYSTALLIZER.ordinal() || ID == guiId.CuttingMachine.ordinal() || ID == guiId.Assembler.ordinal()) {
			return new ContainerMultiblock(player.inventory, (TileMultiBlockMachine)tile);
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(ID  == guiId.RocketBuilder.ordinal())
		{
			return new GuiRocketBuilder(player.inventory, (TileRocketBuilder) tile);
		}
		else if(ID == guiId.Rocket.ordinal()) {
			return new GuiRocket(player, (EntityRocket)world.getEntityByID(x),y,z);
		}
		else if(ID == guiId.FuelingStation.ordinal()) {
			return new GuiFuelingStation(player.inventory, (TileEntityFuelingStation) tile);
		}		
		else if(ID == guiId.BlastFurnace.ordinal()) {
			return new GuiBlastFurnace(player.inventory, (TileEntityBlastFurnace)tile);
		}
		else if(ID == guiId.CuttingMachine.ordinal()) {
			return new GuiCuttingMachine(player.inventory, (TileCuttingMachine)tile);
		}		
		else if(ID == guiId.Assembler.ordinal()) {
			return new GuiPrecisionAssembler(player.inventory, (TilePrecisionAssembler)tile);
		}
		else if(ID == guiId.SpaceLaser.ordinal()) {
			return new GuiSpaceLaser(player.inventory, (TileSpaceLaser)tile);
		}
		else if(ID == guiId.Hatch.ordinal()) {
			return new GuiVariableSlotNumber(player, (IInventory)tile, 0, ((IInventory)tile).getSizeInventory());
		}
		else if(ID == guiId.PowerStorage.ordinal()) {
			return new GuiPowerStorage(player.inventory, (IUniversalEnergy)tile);
		}
		else if(ID == guiId.CRYSTALLIZER.ordinal()) {
			return new GuiCrystallizer(player.inventory, (TileMultiBlockMachine)tile);
		}
		return null;
	}
}