/*
 * Purpose: basis of all machines with 3d renders
 * 
 */

package zmaster587.advancedRocketry.tile;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class TileModelRender extends TileEntity {
	
	//Registry of models and textures
	public enum models {
		ROCKET(AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/combustion.obj")),
				new ResourceLocation("advancedrocketry:textures/models/combustion.png")),
		TANKMIDDLE(AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/middleTank.obj")),new ResourceLocation("advancedrocketry:textures/models/tank.png")),
		TANKEND(AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/endTank.obj")),new ResourceLocation("advancedrocketry:textures/models/tank.png")),
		TANKTOP(AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/topTank.obj")),new ResourceLocation("advancedrocketry:textures/models/tank.png"));
		
		
		private IModelCustom model;
		private ResourceLocation texture;
		
		models(IModelCustom model, ResourceLocation texture) {
			this.model = model;
			this.texture = texture;
		}
	}

	
	int type;
	
	public TileModelRender() {
		super();
	}
	
	public TileModelRender(int type) {
		this.type = type;
	}
	
	@Override
	public boolean canUpdate() {
		return false;
	}
	
	public IModelCustom getModel() {return models.values()[type].model;}
	
	public ResourceLocation getTexture(){ return models.values()[type].texture;}
	
	@Override
	public Packet getDescriptionPacket() {
		
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("type", type);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		type = pkt.func_148857_g().getInteger("type");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		nbt.setInteger("type", type);
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		
		super.readFromNBT(nbt);
		
		type = nbt.getInteger("type");
	}

	public void setType(models model) {
		type = model.ordinal();
	}
}
