package zmaster587.advancedRocketry.block;

import java.util.Random;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockQuartzCrucible extends BlockCauldron {
	
	public BlockQuartzCrucible() {
		super();
	}

	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		return false;
	}
    
	@Override
	public void fillWithRain(World worldIn, BlockPos pos) {
	}
    
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return AdvancedRocketryItems.itemQuartzCrucible;
	}
	
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		
		return new ItemStack(AdvancedRocketryItems.itemQuartzCrucible);//getItemPicked(worldIn, pos, ar3, par4)
	}
    
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_,
    		int p_149694_4_) {
    	return getItemPicked(p_149694_1_, p_149694_2_, p_149694_3_, p_149694_4_);
    }
    
    @SideOnly(Side.CLIENT)
    public Item getItemPicked(World par1World, int par2, int par3, int par4)
    {
        return AdvancedRocketryItems.itemQuartzCrucible;
    }
}
