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
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockTorchUnlit extends TorchBlock {

	public BlockTorchUnlit(Properties properties) {
		super(properties, null);
	}

	@Override
<<<<<<< HEAD
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
			PlayerEntity player) {
		return ARConfiguration.getCurrentConfig().dropExTorches.get() ? super.getPickBlock(state, target, world, pos, player) : new ItemStack(Blocks.TORCH);
=======
	@Nonnull
	@ParametersAreNonnullByDefault
	public ItemStack getPickBlock(IBlockState state, @Nullable RayTraceResult target,
			World world, BlockPos pos, @Nullable EntityPlayer player) {
		return ARConfiguration.getCurrentConfig().dropExTorches ? super.getPickBlock(state, target, world, pos, player) : new ItemStack(Blocks.TORCH);
>>>>>>> origin/feature/nuclearthermalrockets
	}
	
	@Override
<<<<<<< HEAD
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
=======
	@Nonnull
	@ParametersAreNullableByDefault
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos,
			IBlockState state, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<>();
>>>>>>> origin/feature/nuclearthermalrockets

		
		ret.add(new ItemStack(ARConfiguration.getCurrentConfig().dropExTorches.get() ? AdvancedRocketryBlocks.blockUnlitTorch : Blocks.TORCH));

		return ret;
	}

	
	
	@Override
<<<<<<< HEAD
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if(player.getHeldItem(Hand.MAIN_HAND) != null) {
			Item item = player.getHeldItem(Hand.MAIN_HAND).getItem();
			if(!world.isRemote && item != null && AtmosphereHandler.getOxygenHandler(world).getAtmosphereType(pos).allowsCombustion() && (item == Item.getItemFromBlock(Blocks.TORCH) || 
=======
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
			float hitZ) {

		if(!player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
			Item item = player.getHeldItem(EnumHand.MAIN_HAND).getItem();
			if(!world.isRemote && !item.equals(Items.AIR) && AtmosphereHandler.getOxygenHandler(world.provider.getDimension()).getAtmosphereType(pos).allowsCombustion() && (item == Item.getItemFromBlock(Blocks.TORCH) ||
>>>>>>> origin/feature/nuclearthermalrockets
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
