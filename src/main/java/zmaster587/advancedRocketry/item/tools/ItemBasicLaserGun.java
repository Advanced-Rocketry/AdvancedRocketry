package zmaster587.advancedRocketry.item.tools;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.LibVulpes;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.WeakHashMap;

public class ItemBasicLaserGun extends Item {

	int reachDistance = 50;
	private WeakHashMap<LivingEntity, BlockPos> posMap;
	ItemTier toolMaterial;

	public ItemBasicLaserGun( Properties props ) {
		super(props);
		toolMaterial = ItemTier.DIAMOND;
		posMap = new WeakHashMap<>();
	}
	
	public boolean canHarvestBlock(BlockState blockIn)
	{
		Block block = blockIn.getBlock();

		if (block == Blocks.OBSIDIAN)
		{
			return this.toolMaterial.getHarvestLevel() == 3;
		}
		else if (block != Blocks.DIAMOND_BLOCK && block != Blocks.DIAMOND_ORE)
		{
			if (block != Blocks.EMERALD_ORE && block != Blocks.EMERALD_BLOCK)
			{
				if (block != Blocks.GOLD_BLOCK && block != Blocks.GOLD_ORE)
				{
					if (block != Blocks.IRON_BLOCK && block != Blocks.IRON_ORE)
					{
						if (block != Blocks.LAPIS_BLOCK && block != Blocks.LAPIS_ORE)
						{
							if (block != Blocks.REDSTONE_ORE)
							{
								Material material = blockIn.getMaterial();
								return material == Material.ROCK || material == Material.IRON || material == Material.ANVIL;
							}
							else
							{
								return this.toolMaterial.getHarvestLevel() >= 2;
							}
						}
						else
						{
							return this.toolMaterial.getHarvestLevel() >= 1;
						}
					}
					else
					{
						return this.toolMaterial.getHarvestLevel() >= 1;
					}
				}
				else
				{
					return this.toolMaterial.getHarvestLevel() >= 2;
				}
			}
			else
			{
				return this.toolMaterial.getHarvestLevel() >= 2;
			}
		}
		else
		{
			return this.toolMaterial.getHarvestLevel() >= 2;
		}
	}


	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int count) {

		World world = player.getEntityWorld();

		RayTraceResult rayTrace = rayTraceEntity(world,player);

		if(rayTrace != null && rayTrace.hitInfo instanceof Entity) {
			((Entity)rayTrace.hitInfo).attackEntityFrom(DamageSource.GENERIC, 1f);
			if(world.isRemote)
				LibVulpes.proxy.playSound(world, new BlockPos(player.getPositionVec()), AudioRegistry.basicLaser, SoundCategory.PLAYERS, 1, 1f);
			AdvancedRocketry.proxy.spawnLaser(player, rayTrace.getHitVec());
			player.resetActiveHand();
			return;
		}

		rayTrace = rayTrace(world, (PlayerEntity) player, false);

		if(rayTrace == null)
			return;

		if(posMap.get(player) != null && !posMap.get(player).equals(new BlockPos(rayTrace.getHitVec()))) {
			player.resetActiveHand();
			return;
		}
		else if(posMap.get(player) == null && new BlockPos(rayTrace.getHitVec()) != null) {
			posMap.put(player, new BlockPos(rayTrace.getHitVec()));
		}

		if(rayTrace.getType() == Type.BLOCK) {
			BlockState state = world.getBlockState(new BlockPos(rayTrace.getHitVec()));

			if(count % 5 == 0 && world.isRemote)
				LibVulpes.proxy.playSound(world, new BlockPos(player.getPositionVec()), AudioRegistry.basicLaser, SoundCategory.PLAYERS, 1, 1f);
			//
			AdvancedRocketry.proxy.spawnLaser(player, rayTrace.getHitVec());



			super.onUsingTick(stack, player, count);
		}
	}


	protected RayTraceResult rayTrace(World worldIn, PlayerEntity playerIn,
			boolean useLiquids) {
		float f = playerIn.rotationPitch;
		float f1 = playerIn.rotationYaw;
		double d0 = playerIn.getPosX();
		double d1 = playerIn.getPosY() + (double)playerIn.getEyeHeight();
		double d2 = playerIn.getPosZ();
		Vector3d vec3d = new Vector3d(d0, d1, d2);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d3 = reachDistance;

		Vector3d vec3d1 = vec3d.add((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
		
		return worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, BlockMode.COLLIDER, FluidMode.NONE, null));
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entityLiving) {
		RayTraceResult rayTrace = rayTrace(world, (PlayerEntity) entityLiving, false);

		if(rayTrace != null && rayTrace.getType() == Type.BLOCK) {
			BlockState state = world.getBlockState(new BlockPos(rayTrace.getHitVec()));
			if(state.getBlockHardness(world, new BlockPos(rayTrace.getHitVec())) != -1) {

				//
				if(!world.isRemote) {
					((ServerPlayerEntity)entityLiving).interactionManager.tryHarvestBlock(new BlockPos(rayTrace.getHitVec()));
					//world.destroyBlock(rayTrace.getBlockPos(), true);
				}

				//state.getPlayerRelativeBlockHardness((PlayerEntity)player, world, rayTrace.getBlockPos());
			}
		}

		posMap.remove(entityLiving);

		return stack;
	}

	public RayTraceResult rayTraceEntity(World world, Entity entity) {

		Vector3d vec3d = new Vector3d(entity.getPosX(), entity.getPosY() + entity.getEyeHeight(), entity.getPosZ());
		Vector3d vec3d1 = entity.getLook(0);
		Vector3d vec3d2 = vec3d.add(vec3d1.x * reachDistance, vec3d1.y * reachDistance, vec3d1.z * reachDistance);


		List<Entity> list = world.getEntitiesInAABBexcluding(entity, entity.getBoundingBox().grow(vec3d1.x * reachDistance, vec3d1.y * reachDistance, vec3d1.z * reachDistance).expand(1.0D, 1.0D, 1.0D), (Predicate<Entity>) p_apply_1_ -> p_apply_1_ != null && p_apply_1_.canBeCollidedWith());

		for (Entity entity1 : list) {
			AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow(entity1.getCollisionBorderSize());
			boolean raytraceresult = axisalignedbb.intersects(vec3d, vec3d2);

			if (raytraceresult) {
				return new EntityRayTraceResult(entity1);
			}
		}

		return null;
	}



	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand hand) {

		player.setActiveHand(hand);

		posMap.remove(player);
		ItemStack stack = player.getHeldItem(hand);

		World world = player.getEntityWorld();

		RayTraceResult rayTrace = rayTraceEntity(world,player);

		if(rayTrace != null) {
			((EntityRayTraceResult)rayTrace).getEntity().attackEntityFrom(DamageSource.GENERIC, .5f);
			if(world.isRemote)
				LibVulpes.proxy.playSound(worldIn, new BlockPos(player.getPositionVec()), AudioRegistry.basicLaser, SoundCategory.PLAYERS, Minecraft.getInstance().gameSettings.getSoundLevel(SoundCategory.PLAYERS), 1f);

			return new ActionResult<>(ActionResultType.PASS, stack);
		}

		rayTrace = rayTrace(world, player, false);

		if(rayTrace != null && rayTrace.getType() == Type.BLOCK) {
			if(world.isRemote)
				LibVulpes.proxy.playSound(worldIn, new BlockPos(player.getPositionVec()), AudioRegistry.basicLaser, SoundCategory.PLAYERS, Minecraft.getInstance().gameSettings.getSoundLevel(SoundCategory.PLAYERS), 1f);

			return new ActionResult<>(ActionResultType.PASS, stack);
		}
		return new ActionResult<>(ActionResultType.PASS, stack);
	}
}
