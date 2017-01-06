package zmaster587.advancedRocketry.client;

import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityRocket;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;

public class SoundRocketEngine extends MovingSound {

	EntityRocket rocket;

	public SoundRocketEngine(ResourceLocation loc, EntityRocket rocket) {
		super(loc);
		this.rocket = rocket;
		this.repeat = true;
	}

	@Override
	public void update() {

		if(rocket.isDead)
			this.donePlaying = true;

		if(rocket.isInFlight() && rocket.areEnginesRunning())
			this.volume = Math.max(DimensionManager.getInstance().getDimensionProperties(rocket.worldObj.provider.dimensionId).getAtmosphereDensityAtHeight(rocket.posY), 0.05f);
		else
			this.volume = 0;

		this.field_147663_c = (volume + 1f)*0.6f;
		
		if(!rocket.isInOrbit())
			this.field_147663_c += 0.4f*((float) (Math.max(Math.abs(1 - rocket.motionY), 0f) + 1f));
		else
			this.field_147663_c += 0.4f*((float) (Math.max(Math.abs(rocket.motionY/5f), 0f) + 1f));

		this.xPosF = (float) rocket.posX;
		this.yPosF = (float) rocket.posY;
		this.zPosF = (float) rocket.posZ;
	}

}
