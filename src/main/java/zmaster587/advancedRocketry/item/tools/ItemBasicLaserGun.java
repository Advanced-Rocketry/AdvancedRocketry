package zmaster587.advancedRocketry.item.tools;

import java.util.List;
import java.util.WeakHashMap;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.libVulpes.util.BlockPosition;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemBasicLaserGun extends Item {

	int reachDistance = 25;
	private WeakHashMap<EntityLivingBase, BlockPosition> posMap;
	ToolMaterial toolMaterial;

	public ItemBasicLaserGun() {
		super();
		toolMaterial = ToolMaterial.GOLD;
		setMaxStackSize(1);
		setMaxDamage(0);
		posMap = new WeakHashMap<EntityLivingBase, BlockPosition>();
	}


	@Override
	public float func_150893_a(ItemStack stack, Block state) {
		return 0;
	}

	@Override
	public boolean canHarvestBlock(Block block,  ItemStack itemStack)
	{

		return block == Blocks.obsidian ? this.toolMaterial.getHarvestLevel() == 3 : (block != Blocks.diamond_block && block != Blocks.diamond_ore ? (block != Blocks.emerald_ore && block != Blocks.emerald_block ? (block != Blocks.gold_block && block != Blocks.gold_ore ? (block != Blocks.iron_block && block != Blocks.iron_ore ? (block != Blocks.lapis_block && block != Blocks.lapis_ore ? (block != Blocks.redstone_ore && block != Blocks.lit_redstone_ore ? (block.getMaterial() == Material.rock ? true : (block.getMaterial() == Material.iron ? true : block.getMaterial() == Material.anvil)) : this.toolMaterial.getHarvestLevel() >= 2) : this.toolMaterial.getHarvestLevel() >= 1) : this.toolMaterial.getHarvestLevel() >= 1) : this.toolMaterial.getHarvestLevel() >= 2) : this.toolMaterial.getHarvestLevel() >= 2) : this.toolMaterial.getHarvestLevel() >= 2);
	}


	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {

		World world = player.getEntityWorld();

		MovingObjectPosition rayTrace = rayTraceEntity(world,player);

		if(rayTrace != null) {
			rayTrace.entityHit.attackEntityFrom(DamageSource.generic, 1f);
			AdvancedRocketry.proxy.spawnLaser(player, rayTrace.hitVec);
			return;
		}

		rayTrace = getMovingObjectPositionFromPlayer(world, (EntityPlayer) player, false);

		if(rayTrace == null || rayTrace.typeOfHit != MovingObjectType.BLOCK)
			return;

		if(posMap.get(player) != null && !posMap.get(player).equals(new BlockPosition(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ))) {
			player.clearItemInUse();
			return;
		}
		else if(posMap.get(player) == null) {
			posMap.put(player, new BlockPosition(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ));
		}

		if(count % 5 == 0 && world.isRemote) {
			world.playSound(player.posX, player.posY, player.posZ, "advancedrocketry:basicLaserGun", Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.PLAYERS),  1f, false);	
		}

		AdvancedRocketry.proxy.spawnLaser(player, rayTrace.hitVec);


		if(count == 1) {
			if(world.getBlock(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ).getBlockHardness(world, rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ) != -1) {

				//
				if(!world.isRemote) {
					((EntityPlayerMP)player).theItemInWorldManager.tryHarvestBlock(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
					//world.destroyBlock(rayTrace.getBlockPos(), true);
				}
				player.clearItemInUse();
				posMap.remove(player);

				//state.getPlayerRelativeBlockHardness((EntityPlayer)player, world, rayTrace.getBlockPos());
			}
		}

		super.onUsingTick(stack, player, count);

	}


	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 16;
	}

	@Override
	protected MovingObjectPosition getMovingObjectPositionFromPlayer(World p_77621_1_, EntityPlayer p_77621_2_, boolean p_77621_3_)
	{
		float f = 1.0F;
		float f1 = p_77621_2_.prevRotationPitch + (p_77621_2_.rotationPitch - p_77621_2_.prevRotationPitch) * f;
		float f2 = p_77621_2_.prevRotationYaw + (p_77621_2_.rotationYaw - p_77621_2_.prevRotationYaw) * f;
		double d0 = p_77621_2_.prevPosX + (p_77621_2_.posX - p_77621_2_.prevPosX) * (double)f;
		double d1 = p_77621_2_.prevPosY + (p_77621_2_.posY - p_77621_2_.prevPosY) * (double)f + (double)(p_77621_1_.isRemote ? p_77621_2_.getEyeHeight() - p_77621_2_.getDefaultEyeHeight() : p_77621_2_.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
		double d2 = p_77621_2_.prevPosZ + (p_77621_2_.posZ - p_77621_2_.prevPosZ) * (double)f;
		Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = reachDistance;
		Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
		return p_77621_1_.func_147447_a(vec3, vec31, p_77621_3_, !p_77621_3_, false);
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.none;
	}



	/*@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityPlayer entityLiving)
	{
		MovingObjectPosition rayTrace = getMovingObjectPositionFromPlayer(world, (EntityPlayer) entityLiving, false);

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
	}*/

	public MovingObjectPosition rayTraceEntity(World world, Entity entity) {

		Vec3 vec3d = Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
		Vec3 vec3d1 = entity.getLookVec();
		Vec3 vec3d2 = vec3d.addVector(vec3d1.xCoord * reachDistance, vec3d1.yCoord * reachDistance, vec3d1.zCoord * reachDistance);

		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.addCoord(vec3d1.xCoord * reachDistance, vec3d1.yCoord * reachDistance, vec3d1.zCoord * reachDistance).expand(1.0D, 1.0D, 1.0D), new IEntitySelector() {
			
			@Override
			public boolean isEntityApplicable(Entity p_apply_1_) {
				// TODO Auto-generated method stub
				return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
			}
		});

		for (int j = 0; j < list.size(); ++j)
		{
			Entity entity1 = (Entity)list.get(j);
			AxisAlignedBB axisalignedbb = entity1.boundingBox.expand((double)entity1.getCollisionBorderSize(),(double)entity1.getCollisionBorderSize(),(double)entity1.getCollisionBorderSize());
			MovingObjectPosition raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

			if (axisalignedbb.isVecInside(vec3d))
			{
			}
			else if (raytraceresult != null)
			{
				raytraceresult.entityHit = entity1;
				return raytraceresult;
			}
		}

		return null;
	}


	@Override
	public ItemStack onItemRightClick(ItemStack stack,
			World worldIn, EntityPlayer player) {

		player.setItemInUse(stack, getMaxItemUseDuration(stack));
		posMap.remove(player);

		

		//if(true)
		//	return super.onItemRightClick(stack, worldIn, player, hand);
		World world = player.getEntityWorld();

		MovingObjectPosition rayTrace = rayTraceEntity(world,player);

		if(rayTrace != null) {
			rayTrace.entityHit.attackEntityFrom(DamageSource.generic, .5f);

			if(world.isRemote)
				world.playSound(player.posX, player.posY, player.posZ, "advancedrocketry:basicLaserGun", Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.PLAYERS),  1f, false);

			return stack;
		}

		rayTrace = getMovingObjectPositionFromPlayer(world, (EntityPlayer) player, false);

		if(rayTrace != null && rayTrace.typeOfHit == MovingObjectType.BLOCK) {

			if(world.isRemote)
				world.playSound(player.posX, player.posY, player.posZ, "advancedrocketry:basicLaserGun", Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.PLAYERS),  1f, false);

			return stack;
		}
		return stack;
	}
}
