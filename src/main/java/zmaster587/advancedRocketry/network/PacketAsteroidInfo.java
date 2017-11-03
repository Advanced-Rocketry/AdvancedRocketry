package zmaster587.advancedRocketry.network;

import java.io.IOException;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.util.AsteroidSmall;
import zmaster587.libVulpes.network.BasePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class PacketAsteroidInfo extends BasePacket {

	AsteroidSmall asteroid;

	public PacketAsteroidInfo() {
		asteroid = new AsteroidSmall();
	}

	public PacketAsteroidInfo(AsteroidSmall asteroid) {
		this();
		this.asteroid = asteroid;
	}

	@Override
	public void write(ByteBuf out) {
		PacketBuffer packetBuffer = new PacketBuffer(out);
		NBTTagCompound nbt = new NBTTagCompound();
		
		packetBuffer.writeString(asteroid.ID);
		packetBuffer.writeInt(asteroid.distance);
		packetBuffer.writeInt(asteroid.mass);
		packetBuffer.writeInt(asteroid.minLevel);
		packetBuffer.writeFloat(asteroid.massVariability);
		packetBuffer.writeFloat(asteroid.richness);					//factor of the ratio of ore to stone
		packetBuffer.writeFloat(asteroid.richnessVariability);		//variability of richness
		packetBuffer.writeFloat(asteroid.probability);				//probability of the asteroid spawning
		packetBuffer.writeFloat(asteroid.timeMultiplier);
		
		packetBuffer.writeInt(asteroid.stackProbabilites.size());
		for(int i = 0; i < asteroid.stackProbabilites.size(); i++)
		{
			packetBuffer.writeItemStackToBuffer(asteroid.itemStacks.get(i));
			packetBuffer.writeFloat(asteroid.stackProbabilites.get(i));
		}
	}

	@Override
	public void readClient(ByteBuf in) {
		PacketBuffer packetBuffer = new PacketBuffer(in);
		
		asteroid.ID = packetBuffer.readStringFromBuffer(128);
		asteroid.distance = packetBuffer.readInt();
		asteroid.mass = packetBuffer.readInt();
		asteroid.minLevel = packetBuffer.readInt();
		asteroid.massVariability = packetBuffer.readFloat();
		asteroid.richness = packetBuffer.readFloat();					//factor of the ratio of ore to stone
		asteroid.richnessVariability = packetBuffer.readFloat();		//variability of richness
		asteroid.probability = packetBuffer.readFloat();				//probability of the asteroid spawning
		asteroid.timeMultiplier = packetBuffer.readFloat();
		
		int size = packetBuffer.readInt();
		for(int i = 0; i < size; i++)
		{
			try {
				asteroid.itemStacks.add(packetBuffer.readItemStackFromBuffer());
				asteroid.stackProbabilites.add(packetBuffer.readFloat());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void read(ByteBuf in) {
		//Should never be read on the server!
	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {
		Configuration.asteroidTypes.put(asteroid.ID, asteroid);
	}

	@Override
	public void executeServer(EntityPlayerMP player) {}

}
