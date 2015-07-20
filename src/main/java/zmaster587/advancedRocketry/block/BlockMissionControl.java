package zmaster587.advancedRocketry.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.tile.TileMissionController;
import zmaster587.libVulpes.block.RotatableBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMissionControl extends RotatableBlock {

	
	
	public BlockMissionControl() {
		super(Material.iron);
		setCreativeTab(CreativeTabs.tabTransport);
	}
	
	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
		((TileMissionController)world.getTileEntity(x, y, z)).interactSatellite(player, world,x,y,z);
		
		//player.openGui(AdvancedRocketry.instance, 100, world, x, y, z);
		return true;
    }
	
	/*@Override
	public boolean isOpaqueCube() {return false; }*/
	
	/*@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
	{
		return true;
	}*/
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
			return new TileMissionController();
	}
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        //this.blockIcon = par1IconRegister.registerIcon(this.getTextureName());
        this.front = par1IconRegister.registerIcon("advancedRocketry:MonitorFront");
        this.sides = par1IconRegister.registerIcon("advancedRocketry:MonitorSide");
        this.rear = par1IconRegister.registerIcon("advancedRocketry:MonitorRear");
        this.top = par1IconRegister.registerIcon("advancedRocketry:MonitorTop");
        this.bottom = this.top;
    }
}