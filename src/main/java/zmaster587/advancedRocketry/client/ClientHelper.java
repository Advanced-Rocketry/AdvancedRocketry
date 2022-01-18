package zmaster587.advancedRocketry.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IDayTimeReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.client.render.planet.ISkyRenderer;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.client.render.planet.RenderSpaceSky;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.util.ZUtils;

public class ClientHelper {

	@OnlyIn(Dist.CLIENT)
	public static boolean callCustomSkyRenderer(MatrixStack matrix, float partialTicks) {
		World world = Minecraft.getInstance().world;
		if(!DimensionManager.getInstance().isDimensionCreated(world))
			return true;


		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(world),  new BlockPos(Minecraft.getInstance().player.getPositionVec()));

		ISkyRenderer renderer =  properties.getSkyRenderer();

		
		if(renderer == null) {
			if(properties.isStation()) {
				if(!ARConfiguration.getCurrentConfig().stationSkyOverride.get()) {
					properties.setSkyRenderer(null);
					return true;
				}
				properties.setSkyRenderer(new RenderSpaceSky());
			} else {
				if(!ARConfiguration.getCurrentConfig().planetSkyOverride.get()) {
					properties.setSkyRenderer(null);
					return true;
				}
				properties.setSkyRenderer(new RenderPlanetarySky());
			}
			renderer = properties.getSkyRenderer();
		}
		renderer.render(matrix, partialTicks);
		return false;
	}

	public static float callTimeOfDay(float ogTime, IDayTimeReader reader) {
		if(!(reader instanceof World) || !DimensionManager.getInstance().isDimensionCreated((World)reader))
			return ogTime;

		DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier((World)reader));

		if(properties.isStation() || properties.getId().equals(DimensionManager.spaceId))
			return AdvancedRocketry.proxy.calculateCelestialAngleSpaceStation();
		
		double d0 = MathHelper.frac((double)reader.func_241851_ab() / ((double)properties.rotationalPeriod) - 0.25D);
		double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
		return (float)(d0 * 2.0D + d1) / 3.0F;
	}

	/* gravRotation
	 * 1: north
	 * 2: east
	 * 3: south
	 * 4: west
	 * 5: up
	 */

	/*public static boolean rotate = true;

	private static float roll = 90;

	public static void setCameraRoll(float roll) {
		ClientHelper.roll = roll;
	}

	public static void transformCamera() {
		GL11.glRotatef(roll, 0, 0, 1);
	}

	public static void netHandlerSetPlayerLocation(ServerPlayerEntity player,double a, double b, double c, float d, float e, int rotation) {
		if(rotation == 0)
			player.playerNetServerHandler.sendPacket(new S08PacketPlayerPosLook(a, b + 1.6200000047683716D, c, d, e, false));
		else 
			player.playerNetServerHandler.sendPacket(new S08PacketPlayerPosLook(player.posX, player.posY + player.width/2f, player.posZ, d, e, false));
		System.out.println(a + " " + b + " " + c);
	}

	public static void transformEntity(int rotation, LivingEntity base) {

		if(rotation == 1) {
			GL11.glRotatef(90, 1f, 0f, 0f);
			GL11.glRotatef(-90, 0f, 1f, 0f);
		}
		if(rotation == 2) {
			GL11.glRotatef(90, 0f, 0f, 1f);
		}
		if(rotation == 3) {
			GL11.glRotatef(-90, 1f, 0f, 0f);
			GL11.glRotatef(-90, 0f, 1f, 0f);
		}
		if(rotation == 4) {
			GL11.glRotatef(-90, 0f, 0f, 1f);
		}
	}

	public static float transformStrafe(int rotation, float strafe, float forward) {
		return strafe;
	}

	public static float transformForward(int rotation, float strafe, float forward) {
		return forward;
	}

	public static void transformGravity(int rotation, LivingEntity player) {
		/*if(!player.worldObj.isRemote)
			player.boundingBox.setBounds(-1, -1, -1, 1, 1, 1);* /

		player.getMotion().y *= 0.5F;
		if(!(player instanceof PlayerEntity) || !((PlayerEntity)player).capabilities.isFlying)
		switch(rotation) {
		case 1:
			player.motionZ -= 0.08F;
			break;
		case 2:
			player.motionX += 0.08F;
			break;
		case 3:
			player.motionZ += 0.08F;
			break;
		case 4:
			player.motionX -= 0.08F;
			break;
		}
	}

	public static void moveFlyingVerticalOverride(EntityPlayerSP entity, int rotation) {
		if(rotation == 1) {
			if(entity.movementInput.jump)
				entity.motionZ += 0.15D;
			if(entity.movementInput.sneak)
				entity.motionZ -= 0.15D;
		}
		if(rotation == 2) {
			if(entity.movementInput.jump)
				entity.motionX -= 0.15D;
			if(entity.movementInput.sneak)
				entity.motionX += 0.15D;
		}
		if(rotation == 3) {
			if(entity.movementInput.jump)
				entity.motionZ -= 0.15D;
			if(entity.movementInput.sneak)
				entity.motionZ += 0.15D;
		}
		if(rotation == 4) {
			if(entity.movementInput.jump)
				entity.motionX += 0.15D;
			if(entity.movementInput.sneak)
				entity.motionX -= 0.15D;
		}
	}

	public static void setPosition(LivingEntity entity, int rotation,double x, double y, double z) {

		float f = entity.width / 2.0F;
		float f1 = entity.height;
		double xLNew, yLNew, zLNew, xMNew, yMNew, zMNew;

		/*xLNew = x - (double)f;
		yLNew = y - (double)entity.yOffset + (double)entity.ySize;
		zLNew = z - (double)f;
		xMNew = x + (double)f;
		yMNew = y - (double)entity.yOffset + (double)entity.ySize + (double)f1;
		zMNew = z + (double)f;* /

		switch(rotation) {

		case 1:
			xLNew = x - (double)f;
			yLNew = y - (double)f;
			zLNew = z - (double)entity.yOffset + (double)entity.ySize;
			xMNew = x + (double)f;
			yMNew = y + (double)f;
			zMNew = z - (double)entity.yOffset + (double)entity.ySize + (double)f1;
			break;

		default:
			xLNew = x - (double)f;
			yLNew = y - (double)entity.yOffset + (double)entity.ySize;
			zLNew = z - (double)f;
			xMNew = x + (double)f;
			yMNew = y - (double)entity.yOffset + (double)entity.ySize + (double)f1;
			zMNew = z + (double)f;
		}

		entity.boundingBox.setBounds(xLNew, yLNew, zLNew, xMNew, yMNew, zMNew);
	}

	public static void livingEntityJump(int rotation,LivingEntity entity) {
		double motion;
		motion = 0.61999998688697815D;
		if (entity.isPotionActive(Potion.jump))
		{
			motion += (double)((float)(entity.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
		}

		/*if (entity.isSprinting())
        {
            float f = entity.rotationYaw * 0.017453292F;
            entity.motionX -= (double)(MathHelper.sin(f) * 0.2F);
            entity.motionZ += (double)(MathHelper.cos(f) * 0.2F);
        }* /


		if(rotation == 1)
			entity.motionZ = motion;
		entity.isAirBorne = true;
		ForgeHooks.onLivingJump(entity);
	}

	//TODO?
	public static void moveEntity(LivingEntity entity, int rotation,double x, double y, double z) {

		if(rotation == 1) {

			float f = entity.width / 2.0F;
			float f1 = entity.height;
			double xLNew, yLNew, zLNew, xMNew, yMNew, zMNew;

			xLNew = entity.getPosX() - (double)f;
			yLNew = entity.posY - (double)f;
			zLNew = entity.getPosZ() - (double)entity.yOffset + (double)entity.ySize;
			xMNew = entity.getPosX() + (double)f;
			yMNew = entity.posY + (double)f;
			zMNew = entity.getPosZ() - (double)entity.yOffset + (double)entity.ySize + (double)f1;

			entity.boundingBox.setBounds(xLNew, yLNew, zLNew, xMNew, yMNew, zMNew);
		} else {

			float f = entity.width / 2.0F;
			float f1 = entity.height;
			double xLNew, yLNew, zLNew, xMNew, yMNew, zMNew;

			xLNew = x - (double)f;
			yLNew = y - (double)entity.yOffset + (double)entity.ySize;
			zLNew = z - (double)f;
			xMNew = x + (double)f;
			yMNew = y - (double)entity.yOffset + (double)entity.ySize + (double)f1;
			zMNew = z + (double)f;

			entity.boundingBox.setBounds(xLNew, yLNew, zLNew, xMNew, yMNew, zMNew);
		}

		if (entity.noClip)
		{
			entity.boundingBox.offset(x, y, z);
			entity.getPosX() = (entity.boundingBox.minX + entity.boundingBox.maxX) / 2.0D;
			entity.posY = entity.boundingBox.minY + (double)entity.yOffset - (double)entity.ySize;
			entity.getPosZ() = (entity.boundingBox.minZ + entity.boundingBox.maxZ) / 2.0D;
		}
		else
		{
			entity.worldObj.theProfiler.startSection("move");
			entity.ySize *= 0.4F;
			double d3 = entity.getPosX();
			double d4 = entity.posY;
			double d5 = entity.getPosZ();

			if (entity.isInWeb)
			{
				entity.isInWeb = false;
				x *= 0.25D;
				y *= 0.05000000074505806D;
				z *= 0.25D;
				entity.motionX = 0.0D;
				entity.getMotion().y = 0.0D;
				entity.motionZ = 0.0D;
			}

			double d6 = x;
			double d7 = y;
			double d8 = z;
			AxisAlignedBB axisalignedbb = entity.boundingBox.copy();
			boolean flag = entity.onGround && entity.isSneaking() && entity instanceof PlayerEntity;

			if (flag)
			{
				double d9;

				for (d9 = 0.05D; x != 0.0D && entity.worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox.getOffsetBoundingBox(x, -1.0D, 0.0D)).isEmpty(); d6 = x)
				{
					if (x < d9 && x >= -d9)
					{
						x = 0.0D;
					}
					else if (x > 0.0D)
					{
						x -= d9;
					}
					else
					{
						x += d9;
					}
				}

				for (; z != 0.0D && entity.worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox.getOffsetBoundingBox(0.0D, -1.0D, z)).isEmpty(); d8 = z)
				{
					if (z < d9 && z >= -d9)
					{
						z = 0.0D;
					}
					else if (z > 0.0D)
					{
						z -= d9;
					}
					else
					{
						z += d9;
					}
				}

				while (x != 0.0D && z != 0.0D && entity.worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox.getOffsetBoundingBox(x, -1.0D, z)).isEmpty())
				{
					if (x < d9 && x >= -d9)
					{
						x = 0.0D;
					}
					else if (x > 0.0D)
					{
						x -= d9;
					}
					else
					{
						x += d9;
					}

					if (z < d9 && z >= -d9)
					{
						z = 0.0D;
					}
					else if (z > 0.0D)
					{
						z -= d9;
					}
					else
					{
						z += d9;
					}

					d6 = x;
					d8 = z;
				}
			}

			List list = entity.worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox.addCoord(x, y, z));

			for (int i = 0; i < list.size(); ++i)
			{
				y = ((AxisAlignedBB)list.get(i)).calculateYOffset(entity.boundingBox, y);
			}

			entity.boundingBox.offset(0.0D, y, 0.0D);

			if (!entity.field_70135_K && d7 != y)
			{
				z = 0.0D;
				y = 0.0D;
				x = 0.0D;
			}

			boolean flag1 = entity.onGround || d7 != y && d7 < 0.0D;
			int j;

			for (j = 0; j < list.size(); ++j)
			{
				x = ((AxisAlignedBB)list.get(j)).calculateXOffset(entity.boundingBox, x);
			}

			entity.boundingBox.offset(x, 0.0D, 0.0D);

			if (!entity.field_70135_K && d6 != x)
			{
				z = 0.0D;
				y = 0.0D;
				x = 0.0D;
			}

			for (j = 0; j < list.size(); ++j)
			{
				z = ((AxisAlignedBB)list.get(j)).calculateZOffset(entity.boundingBox, z);
			}

			entity.boundingBox.offset(0.0D, 0.0D, z);

			if (!entity.field_70135_K && d8 != z)
			{
				z = 0.0D;
				y = 0.0D;
				x = 0.0D;
			}

			double d10;
			double d11;
			int k;
			double d12;

			if (entity.stepHeight > 0.0F && flag1 && (flag || entity.ySize < 0.05F) && (d6 != x || d8 != z))
			{
				d12 = x;
				d10 = y;
				d11 = z;
				x = d6;
				y = (double)entity.stepHeight;
				z = d8;
				AxisAlignedBB axisalignedbb1 = entity.boundingBox.copy();
				entity.boundingBox.setBB(axisalignedbb);
				list = entity.worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox.addCoord(d6, y, d8));

				for (k = 0; k < list.size(); ++k)
				{
					y = ((AxisAlignedBB)list.get(k)).calculateYOffset(entity.boundingBox, y);
				}

				entity.boundingBox.offset(0.0D, y, 0.0D);

				if (!entity.field_70135_K && d7 != y)
				{
					z = 0.0D;
					y = 0.0D;
					x = 0.0D;
				}

				for (k = 0; k < list.size(); ++k)
				{
					x = ((AxisAlignedBB)list.get(k)).calculateXOffset(entity.boundingBox, x);
				}

				entity.boundingBox.offset(x, 0.0D, 0.0D);

				if (!entity.field_70135_K && d6 != x)
				{
					z = 0.0D;
					y = 0.0D;
					x = 0.0D;
				}

				for (k = 0; k < list.size(); ++k)
				{
					z = ((AxisAlignedBB)list.get(k)).calculateZOffset(entity.boundingBox, z);
				}

				entity.boundingBox.offset(0.0D, 0.0D, z);

				if (!entity.field_70135_K && d8 != z)
				{
					z = 0.0D;
					y = 0.0D;
					x = 0.0D;
				}

				if (!entity.field_70135_K && d7 != y)
				{
					z = 0.0D;
					y = 0.0D;
					x = 0.0D;
				}
				else
				{
					y = (double)(-entity.stepHeight);

					for (k = 0; k < list.size(); ++k)
					{
						y = ((AxisAlignedBB)list.get(k)).calculateYOffset(entity.boundingBox, y);
					}

					entity.boundingBox.offset(0.0D, y, 0.0D);
				}

				if (d12 * d12 + d11 * d11 >= x * x + z * z)
				{
					x = d12;
					y = d10;
					z = d11;
					entity.boundingBox.setBB(axisalignedbb1);
				}
			}

			entity.worldObj.theProfiler.endSection();
			entity.worldObj.theProfiler.startSection("rest");


			entity.isCollidedHorizontally = d6 != x || d8 != z;
			entity.isCollidedVertically = d7 != y;


			//TODO
			if(rotation == 1) {
				entity.getPosX() = (entity.boundingBox.minX + entity.boundingBox.maxX) / 2.0D;
				entity.posY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0D;
				entity.getPosZ() = entity.boundingBox.minZ + (double)entity.yOffset - (double)entity.ySize;

				entity.onGround = d8 != z && d8 < 0.0D;

			}else {
				entity.getPosX() = (entity.boundingBox.minX + entity.boundingBox.maxX) / 2.0D;
				entity.posY = entity.boundingBox.minY + (double)entity.yOffset - (double)entity.ySize;
				entity.getPosZ() = (entity.boundingBox.minZ + entity.boundingBox.maxZ) / 2.0D;

				entity.onGround = d7 != y && d7 < 0.0D;
			}



			entity.isCollided = entity.isCollidedHorizontally || entity.isCollidedVertically;

			entity.updateFallState(y, entity.onGround);

			if (d6 != x)
			{
				entity.motionX = 0.0D;
			}

			if (d7 != y)
			{
				entity.getMotion().y = 0.0D;
			}

			if (d8 != z)
			{
				entity.motionZ = 0.0D;
			}

			d12 = entity.getPosX() - d3;
			d10 = entity.posY - d4;
			d11 = entity.getPosZ() - d5;

			if (entity.canTriggerWalking() && !flag && entity.ridingEntity == null)
			{
				int j1 = MathHelper.floor_double(entity.getPosX());
				k = MathHelper.floor_double(entity.posY - 0.20000000298023224D - (double)entity.yOffset);
				int l = MathHelper.floor_double(entity.getPosZ());
				Block block = entity.worldObj.getBlock(j1, k, l);
				int i1 = entity.worldObj.getBlock(j1, k - 1, l).getRenderType();

				if (i1 == 11 || i1 == 32 || i1 == 21)
				{
					block = entity.worldObj.getBlock(j1, k - 1, l);
				}

				if (block != Blocks.ladder)
				{
					d10 = 0.0D;
				}

				entity.distanceWalkedModified = (float)((double)entity.distanceWalkedModified + (double)MathHelper.sqrt_double(d12 * d12 + d11 * d11) * 0.6D);
				entity.distanceWalkedOnStepModified = (float)((double)entity.distanceWalkedOnStepModified + (double)MathHelper.sqrt_double(d12 * d12 + d10 * d10 + d11 * d11) * 0.6D);

				if (entity.distanceWalkedOnStepModified > (float)entity.nextStepDistance && block.getMaterial() != Material.air)
				{
					entity.nextStepDistance = (int)entity.distanceWalkedOnStepModified + 1;

					if (entity.isInWater())
					{
						float f = MathHelper.sqrt_double(entity.motionX * entity.motionX * 0.20000000298023224D + entity.getMotion().y * entity.getMotion().y + entity.motionZ * entity.motionZ * 0.20000000298023224D) * 0.35F;

						if (f > 1.0F)
						{
							f = 1.0F;
						}

						entity.playSound(entity.getSwimSound(), f, 1.0F + (entity.rand.nextFloat() - entity.rand.nextFloat()) * 0.4F);
					}

					entity.func_145780_a(j1, k, l, block);
					block.onEntityWalking(entity.worldObj, j1, k, l, entity);
				}
			}

			try
			{
				entity.func_145775_I();
			}
			catch (Throwable throwable)
			{
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
				entity.addEntityCrashInfo(crashreportcategory);
				throw new ReportedException(crashreport);
			}

			boolean flag2 = entity.isWet();

			if (entity.worldObj.func_147470_e(entity.boundingBox.contract(0.001D, 0.001D, 0.001D)))
			{
				entity.dealFireDamage(1);

				if (!flag2)
				{
					++entity.fire;

					if (entity.fire == 0)
					{
						entity.setFire(8);
					}
				}
			}
			else if (entity.fire <= 0)
			{
				entity.fire = -entity.fireResistance;
			}

			if (flag2 && entity.fire > 0)
			{
				entity.playSound("random.fizz", 0.7F, 1.6F + (entity.rand.nextFloat() - entity.rand.nextFloat()) * 0.4F);
				entity.fire = -entity.fireResistance;
			}

			entity.worldObj.theProfiler.endSection();
		}
	}

	public static void moveFlying(LivingEntity entity, int rotation,float a, float b, float c) {
		float f3 = a * a + b * b;

		if (f3 >= 1.0E-4F)
		{
			f3 = MathHelper.sqrt_float(f3);

			if (f3 < 1.0F)
			{
				f3 = 1.0F;
			}

			f3 = c / f3;
			a *= f3;
			b *= f3;
			float f4 = MathHelper.sin(entity.rotationYaw * (float)Math.PI / 180.0F);
			float f5 = MathHelper.cos(entity.rotationYaw * (float)Math.PI / 180.0F);
			if(rotation == 1) {
				entity.getMotion().y -= (double)(a * f5 - b * f4);
				entity.motionX -= (double)(b * f5 + a * f4);
			}

			if(rotation == 2) {
				entity.getMotion().y += (double)(a * f5 - b * f4);
				entity.motionZ += (double)(b * f5 + a * f4);
			}

			if(rotation == 3) {
				entity.getMotion().y += (double)(a * f5 - b * f4);
				entity.motionX -= (double)(b * f5 + a * f4);
			}

			if(rotation == 4) {
				entity.getMotion().y -= (double)(a * f5 - b * f4);
				entity.motionZ += (double)(b * f5 + a * f4);
			}
		}
	}

	public static void transformCamera2(int rotation, LivingEntity base/*float yaw, float pitch, float prevYaw, float prevPitch* /,float p_78467_1_) {

		//Debug code
		if(rotation == 0)
			return;


		if(rotation == 1) {
			GL11.glRotatef(base.rotationPitch + (base.rotationPitch - base.prevRotationPitch) * p_78467_1_, 0.0F, -1.0F, 0.0F);
			GL11.glRotatef(base.rotationYaw + (base.rotationYaw - base.prevRotationYaw) * p_78467_1_ + 180.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(90, 0f, 1f, 0f);
		}
		if(rotation == 2) {
			GL11.glRotatef(base.rotationPitch + (base.rotationPitch - base.prevRotationPitch) * p_78467_1_, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(base.rotationYaw + (base.rotationYaw - base.prevRotationYaw) * p_78467_1_ + 180.0F, -1.0F, 0.0F, 0.0F);
		}
		if(rotation == 3) {
			GL11.glRotatef(base.rotationPitch + (base.rotationPitch - base.prevRotationPitch) * p_78467_1_, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(base.rotationYaw + (base.rotationYaw - base.prevRotationYaw) * p_78467_1_ + 180.0F, -1.0F, 0.0F, 0.0F);
			GL11.glRotatef(90, 0f, 1f, 0f);
		}

		if(rotation == 4) {
			GL11.glRotatef(base.rotationPitch + (base.rotationPitch - base.prevRotationPitch) * p_78467_1_, 0.0F, -1.0F, 0.0F);
			GL11.glRotatef(base.rotationYaw + (base.rotationYaw - base.prevRotationYaw) * p_78467_1_ + 180.0F, 1.0F, 0.0F, 0.0F);
		}

	}

	//TODO: optimize
	public static Vec3 createModifiedLookVector(LivingEntity base, int rotation, float p_70676_1_) {
		float f1;
		float f2;
		float f3;
		float f4;
		if(rotation == 1) {
			if (p_70676_1_ == 1.0F)
			{
				f1 = MathHelper.cos(-base.rotationYaw * 0.017453292F - (float)Math.PI);
				f2 = MathHelper.sin(-base.rotationYaw * 0.017453292F - (float)Math.PI);
				f3 = MathHelper.cos(-base.rotationPitch * 0.017453292F);
				f4 = MathHelper.sin(-base.rotationPitch * 0.017453292F);
				return Vec3.createVectorHelper((double)(f1 * f3),(double)(f2 * f3),(double)f4);
			}
			else
			{
				f1 = base.prevRotationPitch + (base.rotationPitch - base.prevRotationPitch) * p_70676_1_;
				f2 = base.prevRotationYaw + (base.rotationYaw - base.prevRotationYaw) * p_70676_1_;
				f3 = -MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
				f4 = -MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
				float f5 = -MathHelper.cos(-f1 * 0.017453292F);
				float f6 = MathHelper.sin(-f1 * 0.017453292F);
				return Vec3.createVectorHelper((double)(f3 * f5),(double)(f4 * f5),(double)f6);
			}
		}
		else if(rotation == 2) {
			if (p_70676_1_ == 1.0F)
			{
				f1 = MathHelper.cos(-base.rotationYaw * 0.017453292F - (float)Math.PI);
				f2 = MathHelper.sin(-base.rotationYaw * 0.017453292F - (float)Math.PI);
				f3 = -MathHelper.cos(-base.rotationPitch * 0.017453292F);
				f4 = -MathHelper.sin(-base.rotationPitch * 0.017453292F);
				return Vec3.createVectorHelper((double)f4,(double)(f2 * f3),(double)(f1 * f3));
			}
			else
			{
				f1 = base.prevRotationPitch + (base.rotationPitch - base.prevRotationPitch) * p_70676_1_;
				f2 = base.prevRotationYaw + (base.rotationYaw - base.prevRotationYaw) * p_70676_1_;
				f3 = MathHelper.cos(f2 * 0.017453292F - (float)Math.PI);
				f4 = -MathHelper.sin(f2 * 0.017453292F - (float)Math.PI);
				float f5 = -MathHelper.cos(f1 * 0.017453292F);
				float f6 = MathHelper.sin(f1 * 0.017453292F);
				return Vec3.createVectorHelper((double)f6,(double)(f4 * f5),(double)(f3 * f5));
			}
		}
		else if(rotation == 3) {
			if (p_70676_1_ == 1.0F)
			{
				f1 = -MathHelper.cos(-base.rotationYaw * 0.017453292F - (float)Math.PI);
				f2 = MathHelper.sin(-base.rotationYaw * 0.017453292F - (float)Math.PI);
				f3 = -MathHelper.cos(-base.rotationPitch * 0.017453292F);
				f4 = -MathHelper.sin(-base.rotationPitch * 0.017453292F);
				return Vec3.createVectorHelper((double)(f1 * f3),(double)(f2 * f3),(double)f4);
			}
			else
			{
				f1 = base.prevRotationPitch + (base.rotationPitch - base.prevRotationPitch) * p_70676_1_;
				f2 = base.prevRotationYaw + (base.rotationYaw - base.prevRotationYaw) * p_70676_1_;
				f3 = -MathHelper.cos(f2 * 0.017453292F - (float)Math.PI);
				f4 = -MathHelper.sin(f2 * 0.017453292F - (float)Math.PI);
				float f5 = -MathHelper.cos(f1 * 0.017453292F);
				float f6 = MathHelper.sin(f1 * 0.017453292F);
				return Vec3.createVectorHelper((double)(f3 * f5),(double)(f4 * f5),(double)f6);
			}
		}
		else if(rotation == 4) {
			if (p_70676_1_ == 1.0F)
			{
				f1 = -MathHelper.cos(-base.rotationYaw * 0.017453292F - (float)Math.PI);
				f2 = MathHelper.sin(-base.rotationYaw * 0.017453292F - (float)Math.PI);
				f3 = MathHelper.cos(-base.rotationPitch * 0.017453292F);
				f4 = MathHelper.sin(-base.rotationPitch * 0.017453292F);
				return Vec3.createVectorHelper((double)f4,(double)(f2 * f3),(double)(f1 * f3));
			}
			else
			{
				f1 = base.prevRotationPitch + (base.rotationPitch - base.prevRotationPitch) * p_70676_1_;
				f2 = base.prevRotationYaw + (base.rotationYaw - base.prevRotationYaw) * p_70676_1_;
				f3 = MathHelper.cos(f2 * 0.017453292F - (float)Math.PI);
				f4 = MathHelper.sin(f2 * 0.017453292F - (float)Math.PI);
				float f5 = -MathHelper.cos(-f1 * 0.017453292F);
				float f6 = MathHelper.sin(-f1 * 0.017453292F);
				return Vec3.createVectorHelper((double)f6,(double)(f4 * f5),(double)(f3 * f5));
			}
		}
		else {
			if (p_70676_1_ == 1.0F)
			{
				f1 = MathHelper.cos(-base.rotationYaw * 0.017453292F - (float)Math.PI);
				f2 = MathHelper.sin(-base.rotationYaw * 0.017453292F - (float)Math.PI);
				f3 = -MathHelper.cos(-base.rotationPitch * 0.017453292F);
				f4 = MathHelper.sin(-base.rotationPitch * 0.017453292F);
				return Vec3.createVectorHelper((double)(f2 * f3), (double)f4, (double)(f1 * f3));
			}
			else
			{
				f1 = base.prevRotationPitch + (base.rotationPitch - base.prevRotationPitch) * p_70676_1_;
				f2 = base.prevRotationYaw + (base.rotationYaw - base.prevRotationYaw) * p_70676_1_;
				f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
				f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
				float f5 = -MathHelper.cos(-f1 * 0.017453292F);
				float f6 = MathHelper.sin(-f1 * 0.017453292F);
				return Vec3.createVectorHelper((double)(f4 * f5), (double)f6, (double)(f3 * f5));
			}
		}
	}*/
}
