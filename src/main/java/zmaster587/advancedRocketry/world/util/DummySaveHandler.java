package zmaster587.advancedRocketry.world.util;

import java.io.File;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class DummySaveHandler implements ISaveHandler {

	@Override
	public WorldInfo loadWorldInfo() {
		return null;
	}

	@Override
	public void checkSessionLock() throws MinecraftException {
		
	}

	@Override
	public IChunkLoader getChunkLoader(WorldProvider p_75763_1_) {
		return null;
	}

	@Override
	public void saveWorldInfoWithPlayer(WorldInfo p_75755_1_,
			NBTTagCompound p_75755_2_) {
		
	}

	@Override
	public void saveWorldInfo(WorldInfo p_75761_1_) {
		
	}

	@Override
	public void flush() {
		
	}

	@Override
	public File getWorldDirectory() {
		return null;
	}

	@Override
	public File getMapFileFromName(String p_75758_1_) {
		return null;
	}

	@Override
	public IPlayerFileData getPlayerNBTManager() {
		return null;
	}

	@Override
	public TemplateManager getStructureTemplateManager() {
		return null;
	}

}
