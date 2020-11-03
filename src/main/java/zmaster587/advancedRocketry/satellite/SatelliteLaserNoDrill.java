package zmaster587.advancedRocketry.satellite;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.libVulpes.util.ZUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SatelliteLaserNoDrill extends SatelliteBase {
	protected boolean  jammed;
	protected IInventory boundChest;
	World world;
	private static List<ItemStack> ores;
	Random random;

	public SatelliteLaserNoDrill(IInventory boundChest) {
		this.boundChest = boundChest;
		random = new Random(System.currentTimeMillis());

		//isEmpty check because <init> is called in post init to register for holo projector
		if(ores == null && !ARConfiguration.getCurrentConfig().standardLaserDrillOres.isEmpty()) {
			ores = new LinkedList<ItemStack>();
			for(int i = 0; i < ARConfiguration.getCurrentConfig().standardLaserDrillOres.size(); i++) {
				String oreDictName = ARConfiguration.getCurrentConfig().standardLaserDrillOres.get(i);

				String args[] = oreDictName.split(";");
				ResourceLocation itemResource = ResourceLocation.tryCreate(args[0]);
				
				if(itemResource == null)
					continue;
				
				int count = 5;
				try
				{
					if(args.length > 1)
						count = Integer.parseInt(args[1]);
				}
				catch(NumberFormatException e) {}


				if(ItemTags.getCollection().getTagByID(new ResourceLocation(args[0])) != null)
				{

					Item item = ItemTags.getCollection().getTagByID(itemResource).getAllElements().get(0);

					ItemStack stack = new ItemStack(item, count);
					ores.add(stack);
				}
				else
				{
					if(ForgeRegistries.ITEMS.containsKey(itemResource))
					{
						Item item = ForgeRegistries.ITEMS.getValue(itemResource);
						ItemStack stack = new ItemStack(item, count);
						ores.add(stack);
					}
				}
			}
		}
	}

	public boolean isAlive() {
		return world != null;
	}

	public boolean isFinished() {
		return false;
	}

	public boolean getJammed() { return jammed; }

	public void setJammed(boolean newJam) { jammed = newJam; }

	public void deactivateLaser() {
		this.world = null;
	}

	/**
	 * creates the laser and begins mining.  This can
	 * fail if the chunk cannot be force loaded
	 * @param world world to spawn the laser into
	 * @param x x coord
	 * @param z z coord
	 * @return whether creating the laser is successful
	 */
	public boolean activateLaser(World world, int x, int z) {
		this.world = world;
		return true;
	}


	public void performOperation() {

		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		if(random.nextInt(10) == 0) {
			ItemStack item = ores.get(random.nextInt(ores.size()));
			ItemStack newStack = item.copy();
			items.add(newStack);
		}
		else
			items.add(new ItemStack(Items.COBBLESTONE, 5));


		//TODO: generate Items

		if(boundChest != null){
			ItemStack stacks[] = new ItemStack[items.size()];

			stacks = items.toArray(stacks);

			ZUtils.mergeInventory(stacks, boundChest);

			if(!ZUtils.isInvEmpty(stacks)) {
				//TODO: drop extra items
				this.deactivateLaser();
				this.jammed = true;
				return;
			}
		}
	}

	@Override
	public String getInfo(World world) {
		return null;
	}

	@Override
	public String getName() {
		return "Laser";
	}

	@Override
	public boolean performAction(PlayerEntity player, World world, BlockPos pos) {
		performOperation();
		return false;
	}

	@Override
	public double failureChance() {
		return 0;
	}

	@Override
	public void writeToNBT(CompoundNBT nbt) {
		nbt.putBoolean("jammed", jammed);
	}

	@Override
	public void readFromNBT(CompoundNBT nbt) {
		jammed = nbt.getBoolean("jammed");
	}

	@Override
	public boolean canTick() {
		return false;
	}

	@Override
	public void tickEntity() {
	}
}