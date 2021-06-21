package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

<<<<<<< HEAD
public class BlockQuartzCrucible extends CauldronBlock {
=======
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.Random;

public class BlockQuartzCrucible extends BlockCauldron {
>>>>>>> origin/feature/nuclearthermalrockets
	
	public BlockQuartzCrucible(Properties properties) {
		super(properties);
	}

	
	@Override
<<<<<<< HEAD
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		return ActionResultType.FAIL;
=======
	@ParametersAreNullableByDefault
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		return false;
>>>>>>> origin/feature/nuclearthermalrockets
	}
    
	@Override
	public void fillWithRain(World worldIn, BlockPos pos) {
	}
<<<<<<< HEAD
=======
    
	@Override
	@Nonnull
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return AdvancedRocketryItems.itemQuartzCrucible;
	}
	
	@Override
	@Nonnull
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
>>>>>>> origin/feature/nuclearthermalrockets
}
