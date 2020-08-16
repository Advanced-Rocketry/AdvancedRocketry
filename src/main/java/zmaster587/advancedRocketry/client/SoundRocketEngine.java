package zmaster587.advancedRocketry.client;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityRocket;

public class SoundRocketEngine extends MovingSound {

	EntityRocket rocket;

	public SoundRocketEngine(SoundEvent soundIn, SoundCategory categoryIn, EntityRocket rocket) {
		super(soundIn, categoryIn);
		this.rocket = rocket;
		this.repeat = true;
	}

	@Override
	public void update() {

		if(rocket.isDead)
			this.donePlaying = true;

		this.volume = rocket.getEnginePower();

		this.pitch = (volume + 1f)*0.6f;
		
		if(!rocket.isInOrbit())
			this.pitch += 0.4f*((float) (Math.max(Math.abs(1 - rocket.getMotion().y), 0f) + 1f));
		else
			this.pitch += 0.4f*((float) (Math.max(Math.abs(rocket.getMotion().y/5f), 0f) + 1f));

		this.xPosF = (float) rocket.posX;
		this.yPosF = (float) rocket.posY;
		this.zPosF = (float) rocket.posZ;
	}

}
