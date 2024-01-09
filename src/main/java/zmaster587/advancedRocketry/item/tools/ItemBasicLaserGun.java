package zmaster587.advancedRocketry.item.tools;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.LibVulpes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.WeakHashMap;

public class ItemBasicLaserGun extends Item {

	private int reachDistance = 50;
	private WeakHashMap<EntityLivingBase, BlockPos> posMap;
	private ToolMaterial toolMaterial;

	public ItemBasicLaserGun() {
		super();
		toolMaterial = ToolMaterial.DIAMOND;
		setMaxStackSize(1);
		setMaxDamage(0);
		posMap = new WeakHashMap<>();
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		return 0;
	}

	public boolean canHarvestBlock(IBlockState blockIn)
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
							if (block != Blocks.REDSTONE_ORE && block != Blocks.LIT_REDSTONE_ORE)
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
	public boolean isFull3D() {
		return true;
	}

	@Override
	public void onUsingTick(@NotNull ItemStack stack, EntityLivingBase player, int count) {

		World world = player.getEntityWorld();

		RayTraceResult rayTrace = rayTraceEntity(world,player);

		if(rayTrace != null) {
			rayTrace.entityHit.attackEntityFrom(DamageSource.GENERIC, 1f);
			if(world.isRemote)
				LibVulpes.proxy.playSound(world, player.getPosition(), AudioRegistry.basicLaser, SoundCategory.PLAYERS, 1, 1f);
			AdvancedRocketry.proxy.spawnLaser(player, rayTrace.hitVec);
			player.resetActiveHand();
			return;
		}

		rayTrace = rayTrace(world, (EntityPlayer) player, false);

		if(rayTrace == null)
			return;

		if(posMap.get(player) != null && !posMap.get(player).equals(rayTrace.getBlockPos())) {
			player.resetActiveHand();
			return;
		}
		else if(posMap.get(player) == null) {
			posMap.put(player, rayTrace.getBlockPos());
		}

		if(rayTrace.typeOfHit == Type.BLOCK) {
			IBlockState state = world.getBlockState(rayTrace.getBlockPos());

			if(count % 5 == 0 && world.isRemote)
				LibVulpes.proxy.playSound(world, player.getPosition(), AudioRegistry.basicLaser, SoundCategory.PLAYERS, 1, 1f);
			//
			AdvancedRocketry.proxy.spawnLaser(player, rayTrace.hitVec);



			super.onUsingTick(stack, player, count);
		}
	}


	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(@NotNull ItemStack stack)
	{
		return 16;
	}


	@Override
	protected RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn,
			boolean useLiquids) {
		float f = playerIn.rotationPitch;
		float f1 = playerIn.rotationYaw;
		double d0 = playerIn.posX;
		double d1 = playerIn.posY + (double)playerIn.getEyeHeight();
		double d2 = playerIn.posZ;
		Vec3d vec3d = new Vec3d(d0, d1, d2);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d3 = reachDistance;

		Vec3d vec3d1 = vec3d.add((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
		return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	@NotNull
	public EnumAction getItemUseAction(@NotNull ItemStack stack)
	{
		return EnumAction.NONE;
	}

	@Override
	@NotNull
	public ItemStack onItemUseFinish(@NotNull ItemStack stack, World world, EntityLivingBase entityLiving)
	{
		RayTraceResult rayTrace = rayTrace(world, (EntityPlayer) entityLiving, false);

		if(rayTrace != null && rayTrace.typeOfHit == Type.BLOCK) {
			IBlockState state = world.getBlockState(rayTrace.getBlockPos());
			if(state.getBlockHardness(world, rayTrace.getBlockPos()) != -1) {

				//
				if(!world.isRemote) {
					((EntityPlayerMP)entityLiving).interactionManager.tryHarvestBlock(rayTrace.getBlockPos());
					//world.destroyBlock(rayTrace.getBlockPos(), true);
				}

				//state.getPlayerRelativeBlockHardness((EntityPlayer)player, world, rayTrace.getBlockPos());
			}
		}

		posMap.remove(entityLiving);

		return stack;
	}

	public RayTraceResult rayTraceEntity(World world, Entity entity) {

		Vec3d vec3d = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
		Vec3d vec3d1 = entity.getLook(0);
		Vec3d vec3d2 = vec3d.add(vec3d1.x * reachDistance, vec3d1.y * reachDistance, vec3d1.z * reachDistance);


		List<Entity> list = world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().grow(vec3d1.x * reachDistance, vec3d1.y * reachDistance, vec3d1.z * reachDistance).expand(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, p_apply_1_ -> p_apply_1_ != null && p_apply_1_.canBeCollidedWith()));

		for (Entity value : list) {
			AxisAlignedBB axisalignedbb = value.getEntityBoundingBox().grow(value.getCollisionBorderSize());
			RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

			if (!axisalignedbb.contains(vec3d) && raytraceresult != null) {
				raytraceresult.entityHit = value;
				return raytraceresult;
			}
		}

		return null;
	}



	@Override
	
	@NotNull
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {

		player.setActiveHand(hand);

		posMap.remove(player);
		ItemStack stack = player.getHeldItem(hand);



		//if(true)
		//	return super.onItemRightClick(stack, worldIn, player, hand);
		World world = player.getEntityWorld();

		RayTraceResult rayTrace = rayTraceEntity(world,player);

		if(rayTrace != null) {
			rayTrace.entityHit.attackEntityFrom(DamageSource.GENERIC, .5f);
			if(world.isRemote)
				LibVulpes.proxy.playSound(worldIn, player.getPosition(), AudioRegistry.basicLaser, SoundCategory.PLAYERS, Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.PLAYERS), 1f);

			return new ActionResult<>(EnumActionResult.PASS, stack);
		}

		rayTrace = rayTrace(world, player, false);

		if(rayTrace != null && rayTrace.typeOfHit == Type.BLOCK) {
			IBlockState state = world.getBlockState(rayTrace.getBlockPos());

			if(world.isRemote)
				LibVulpes.proxy.playSound(worldIn, player.getPosition(), AudioRegistry.basicLaser, SoundCategory.PLAYERS, Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.PLAYERS), 1f);

			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
		return new ActionResult<>(EnumActionResult.PASS, stack);
	}
}
