package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.libVulpes.util.ZUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockTorchUnlit extends TorchBlock {

	public BlockTorchUnlit(Properties properties) {
		super(properties, null);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
			PlayerEntity player) {
		return ARConfiguration.getCurrentConfig().dropExTorches ? super.getPickBlock(state, target, world, pos, player) : new ItemStack(Blocks.TORCH);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		
		ret.add(new ItemStack(ARConfiguration.getCurrentConfig().dropExTorches ? AdvancedRocketryBlocks.blockUnlitTorch : Blocks.TORCH));

		return ret;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if(player.getHeldItem(Hand.MAIN_HAND) != null) {
			Item item = player.getHeldItem(Hand.MAIN_HAND).getItem();
			if(!world.isRemote && item != null && AtmosphereHandler.getOxygenHandler(world).getAtmosphereType(pos).allowsCombustion() && (item == Item.getItemFromBlock(Blocks.TORCH) || 
					item == Items.FLINT_AND_STEEL || 
					item == Items.FIRE_CHARGE)) {

				world.setBlockState(pos, Blocks.TORCH.getDefaultState());

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
	}
	

}
