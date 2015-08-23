package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * InvisLight source
 *
 */
public class BlockLightSource extends Block {

	public BlockLightSource() {
		super(Material.glass);
		
		setLightLevel(1F);
	}

	@Override
    public boolean canCollideCheck(int par1, boolean par2)
    {
        return false;
    }
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }	
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }
    
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        return 15;
    }
    
    @Override
	public boolean isOpaqueCube() {return false;}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
	{
		return false;
	}
}