package zmaster587.advancedRocketry.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTorchUnlit extends BlockTorch {

	public BlockTorchUnlit() {
		this.setTickRandomly(true);
		this.setCreativeTab(null);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target,
			World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Blocks.TORCH);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos,
			IBlockState state, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();


		ret.add(new ItemStack(Blocks.TORCH));

		return ret;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {

		if(player.getHeldItem(EnumHand.MAIN_HAND) != null) {
			Item item = player.getHeldItem(EnumHand.MAIN_HAND).getItem();
			if(!world.isRemote && item != null && AtmosphereHandler.getOxygenHandler(world.provider.getDimension()).getAtmosphereType(pos).allowsCombustion() && (item == Item.getItemFromBlock(Blocks.TORCH) || 
					item == Items.FLINT_AND_STEEL || 
					item == Items.FIRE_CHARGE)) {

				world.setBlockState(pos, Blocks.TORCH.getDefaultState().withProperty(FACING, state.getValue(FACING)));

				return true;
			}
		}

		return true;
	}


	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn,
			BlockPos pos, Random rand) {
		//Do nothing
	}

}
