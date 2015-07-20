package zmaster587.advancedRocketry.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.Inventory.GuiHandler;
import zmaster587.advancedRocketry.tile.TileEntityBlastFurnace;
import zmaster587.libVulpes.block.RotatableMachineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBlastFurnace extends RotatableMachineBlock {

	public BlockBlastFurnace() {
		super(Material.anvil);
		this.setCreativeTab(CreativeTabs.tabTransport).setTickRandomly(true).setHardness(3F).setResistance(10F).setBlockName("blastFurnaceController");
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {

		TileEntity e = world.getTileEntity(x, y, z);
		if(e != null && e instanceof TileEntityBlastFurnace)
			((TileEntityBlastFurnace)e).setIncomplete();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		TileEntityBlastFurnace e = (TileEntityBlastFurnace)world.getTileEntity(x, y, z);
		//TODO: t
		if(!e.isComplete())
			e.setComplete(x,y,z);

		if(!world.isRemote && e.isComplete()) {
			player.openGui(AdvancedRocketry.instance, GuiHandler.guiId.BlastFurnace.ordinal(), world, x, y, z);
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)

	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		int meta = world.getBlockMetadata(x, y, z);
		if((meta & 8) == 8) {
			ForgeDirection front = getFront(meta);
			world.spawnParticle("flame", x + 0.5 + (0.5 * front.offsetX) + (front.offsetZ*(0.5 * random.nextDouble() - 0.25)), y + 0.5 + (0.5 * random.nextDouble() - 0.25), z + 0.5 + (0.5 * front.offsetZ)  + (front.offsetX*(0.5 * random.nextDouble() - 0.25)), (0.001 * front.offsetX), 0.001, (0.001 * front.offsetZ));
		}
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityBlastFurnace();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister icon)
	{
		this.top = icon.registerIcon("advancedRocketry:BlastBrick");
		this.sides = this.top;
		this.bottom = this.top; //icon.registerIcon("advancedRocketry:MonitorTop");
		this.activeFront = icon.registerIcon("advancedRocketry:BlastBrickFrontActive");
		this.front = icon.registerIcon("advancedRocketry:BlastBrickFront");
		this.rear =  this.top;
	}
}
