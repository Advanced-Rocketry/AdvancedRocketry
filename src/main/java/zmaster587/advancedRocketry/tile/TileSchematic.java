package zmaster587.advancedRocketry.tile;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import zmaster587.advancedRocketry.tile.multiblock.TilePlaceholder;
import zmaster587.libVulpes.block.BlockMeta;

public class TileSchematic extends TilePlaceholder {

	private final int ttl = 6000;
	private int timeAlive = 0;
	List<BlockMeta> possibleBlocks;

	public TileSchematic() {
		possibleBlocks = new ArrayList<BlockMeta>();
	}
	
	@Override
	public boolean canUpdate() {
		return true;
	}

	public void setReplacedBlock(List<BlockMeta> block) {
		possibleBlocks = block;
	}
	
	@Override
	public void setReplacedBlock(Block block) {
		super.setReplacedBlock(block);
		possibleBlocks.clear();
	}
	
	@Override
	public void setReplacedBlockMeta(byte meta) {
		super.setReplacedBlockMeta(meta);
		possibleBlocks.clear();
	}

	@Override
	public Block getReplacedBlock() {
		if(possibleBlocks.isEmpty())
			return super.getReplacedBlock();
		else
			return possibleBlocks.get((timeAlive/20) % possibleBlocks.size()).getBlock();
	}
	
	@Override
	public byte getReplacedBlockMeta() {
		if(possibleBlocks.isEmpty())
			return super.getReplacedBlockMeta();
		else
			return possibleBlocks.get((timeAlive/20) % possibleBlocks.size()).getMeta();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if(!worldObj.isRemote) {
			if(timeAlive == ttl) {
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			}
		}
		timeAlive++;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("timeAlive", timeAlive);
		
		List<Integer> blockIds = new ArrayList<Integer>();
		List<Integer> blockMetas = new ArrayList<Integer>();
		for(int i = 0;  i < possibleBlocks.size();i++) {
			blockIds.add(Block.getIdFromBlock(possibleBlocks.get(i).getBlock()));
			blockMetas.add((int)possibleBlocks.get(i).getMeta());
		}
		
		if(!blockIds.isEmpty()) {
			Integer[] bufferSpace1 = new Integer[blockIds.size()];
			Integer[] bufferSpace2 = new Integer[blockIds.size()];
			nbt.setIntArray("blockIds", ArrayUtils.toPrimitive(blockIds.toArray(bufferSpace1)));
			nbt.setIntArray("blockMetas", ArrayUtils.toPrimitive(blockMetas.toArray(bufferSpace2)));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		timeAlive = nbt.getInteger("timeAlive");
		
		if(nbt.hasKey("blockIds")) {
			int[] block = nbt.getIntArray("blockIds");
			int[] metas = nbt.getIntArray("blockMetas");
			possibleBlocks.clear();
			
			for(int i = 0; i < block.length; i++) {
				possibleBlocks.add(new BlockMeta(Block.getBlockById(block[i]), metas[i]));
			}
		}
	}
}
