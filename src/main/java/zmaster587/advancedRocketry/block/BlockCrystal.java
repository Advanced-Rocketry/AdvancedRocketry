package zmaster587.advancedRocketry.block;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCrystal extends Block implements INamedMetaBlock {

	private static final int colors[] =  {0xb23fff, 0x3333ff, 0x00ff00, 0xff3434, 0xffff34, 0xff9400};
	private static final String names[] =  {"amethyst", "sapphire", "emerald", "ruby", "citrine", "wulfentite"};
	public static final int numMetas = colors.length;
	
    public BlockCrystal()
    {
        super(Material.glass);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setStepSound(soundTypeGlass);
    }
	
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    @Override
	public String getUnlocalizedName(int itemDamage) {
		return  "tile." + names[itemDamage];
	}
    
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return 1;
    }
    
	@Override
	public int colorMultiplier(IBlockAccess access, int x, int y, int z) {
		int meta = access.getBlockMetadata(x, y, z);
		if(meta < 0 || meta >= colors.length)
			return 0;
		return colors[access.getBlockMetadata(x, y, z)];
	}
    
	@Override
	public int getRenderColor(int meta) {
		if(meta < 0 || meta >= colors.length)
			return 0;
		return colors[meta];
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab,
			List list) {
		for(int i = 0; i < colors.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
    {
        Block block = world.getBlock(x, y, z);
        int blockMeta = world.getBlockMetadata(x, y,z);
        ForgeDirection dir = ForgeDirection.getOrientation(side).getOpposite();
        int thisBlockMeta = world.getBlockMetadata(x + dir.offsetX,y+ dir.offsetY,z+ dir.offsetZ);
       
        return block == this && blockMeta == thisBlockMeta ? false : super.shouldSideBeRendered(world, x, y, z, side);
   
    }
}
