package zmaster587.advancedRocketry.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.Inventory.GuiHandler;
import zmaster587.advancedRocketry.tile.multiblock.TileCuttingMachine;
import zmaster587.libVulpes.block.RotatableMachineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCuttingMachine extends RotatableMachineBlock {
	
	public BlockCuttingMachine() {
		super(Material.circuits);
		this.setCreativeTab(CreativeTabs.tabTransport).setHardness(1F).setResistance(3F);
	}
	
	@Override
	public boolean hasTileEntity(int meta) { return true; }

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileCuttingMachine();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		player.openGui(AdvancedRocketry.instance, GuiHandler.guiId.CuttingMachine.ordinal(), world, x, y, z);
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister icons)
	{
		//this.blockIcon = icons.registerIcon(this.getTextureName());
		this.front = icons.registerIcon("advancedRocketry:CuttingMachine");
		this.activeFront = icons.registerIcon("advancedRocketry:CuttingMachine_active");
		this.bottom = this.top = icons.registerIcon("advancedRocketry:MonitorTop");
    	this.rear = this.sides = icons.registerIcon("advancedRocketry:MonitorSide");
	}
}
