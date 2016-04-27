package zmaster587.advancedRocketry.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.util.IAdjBlockUpdate;
import zmaster587.libVulpes.block.RotatableBlock;

public class BlockTile extends RotatableBlock {

	protected IIcon sides_active, front_active, rear_active, bottom_active,top_active;
	protected Class<? extends TileEntity> tileClass;
	protected String textureSideName, textureFrontName, textureTopName, textureBottomName, textureRearName;
	protected String textureSideName_active, textureFrontName_active, textureTopName_active, textureBottomName_active, textureRearName_active;

	protected int guiId;

	public BlockTile(Class<? extends TileEntity> tileClass, int guiId) {
		super(Material.rock);
		this.tileClass = tileClass;
		this.guiId = guiId;
		this.setBlockTextureName("Advancedrocketry:machineGeneric");
	}
	
	public Block setBlockTextureName(String texture1, String textureActive) {
		setBlockTextureName(texture1);
		textureSideName_active = textureFrontName_active = textureTopName_active = textureBottomName_active = textureRearName_active = textureActive;
		return this;
	}

	public Block setSideTexture(String textureName) {
		textureSideName = textureName;
		return this;
	}

	public Block setSideTexture(String textureName, String textureNameActive) {
		textureSideName = textureName;
		textureSideName_active = textureNameActive;
		return this;
	}

	public Block setFrontTexture(String textureName) {
		textureFrontName = textureName;
		return this;
	}

	public Block setFrontTexture(String textureName, String textureNameActive) {
		textureFrontName = textureName;
		textureFrontName_active = textureNameActive;
		return this;
	}

	public Block setTopTexture(String textureName) {
		textureTopName = textureName;
		return this;
	}

	public Block setTopTexture(String textureName, String textureNameActive) {
		textureTopName = textureName;
		textureTopName_active = textureNameActive;
		return this;
	}

	public Block setBottomTexture(String textureName) {
		textureBottomName = textureName;
		return this;
	}

	public Block setBottomTexture(String textureName, String textureNameActive) {
		textureBottomName = textureName;
		textureBottomName_active = textureNameActive;
		return this;
	}

	public Block setRearTexture(String textureName) {
		textureRearName = textureName;
		return this;
	}

	public Block setRearTexture(String textureName, String textureNameActive) {
		textureRearName = textureName;
		textureRearName_active = textureNameActive;
		return this;
	}

	@Override
	public boolean hasTileEntity(int meta) { return true; }

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		try {
			return tileClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		if(!world.isRemote)
			player.openGui(AdvancedRocketry.instance, guiId, world, x, y, z);
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x,
			int y, int z, Block block) {
		super.onNeighborBlockChange(world, x, y, z,
				block);
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof IAdjBlockUpdate)
			((IAdjBlockUpdate)tile).onAdjacentBlockUpdated();

	}

	@Override
	public boolean isBlockNormalCube() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister icons)
	{
		this.blockIcon = icons.registerIcon(this.getTextureName());
		if(this.textureSideName == null)
			this.sides = this.blockIcon;
		else
			this.sides = icons.registerIcon(this.textureSideName);

		if(this.textureSideName_active != null)
			this.sides_active = icons.registerIcon(this.textureSideName_active);
		else
			this.sides_active = this.sides;


		if(this.textureTopName != null)
			this.top = icons.registerIcon(this.textureTopName);
		else
			this.top = sides;


		if(textureTopName_active != null)
			this.top_active = icons.registerIcon(this.textureTopName_active);
		else
			this.top_active = top;


		if(this.textureFrontName == null)
			this.front = this.sides;
		else
			this.front = icons.registerIcon(this.textureFrontName);


		if(this.textureFrontName_active == null) {
			if(this.front != this.sides)
				this.front_active = this.front;
			else
				this.front_active = this.sides_active;
		}
		else
			this.front_active = icons.registerIcon(this.textureFrontName_active);


		if(this.textureRearName == null)
			this.rear = this.sides;
		else
			this.rear = icons.registerIcon(this.textureRearName);

		if(this.textureRearName_active == null)
			this.rear_active = this.sides_active;
		else
			this.rear_active = icons.registerIcon(this.textureRearName_active);


		if(this.textureBottomName == null)
			this.bottom = this.top;
		else
			this.bottom = icons.registerIcon(this.textureBottomName);

		if(this.textureBottomName_active == null)
			this.bottom_active = this.sides_active;
		else
			this.bottom_active = icons.registerIcon(this.textureBottomName_active);
	}

	@Override
	@SideOnly(Side.CLIENT)

	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	public IIcon getIcon(int side, int meta)
	{
		ForgeDirection dir = getRelativeSide(side,meta);


		if((meta & 8) == 8) {
			if(dir == ForgeDirection.UP)
				return this.top_active;
			else if(dir == ForgeDirection.DOWN)
				return this.bottom_active;
			else if(dir == ForgeDirection.NORTH)
				return this.front_active;
			else if(dir == ForgeDirection.EAST)
				return this.sides_active;
			else if(dir == ForgeDirection.SOUTH)
				return this.rear_active;
			else if(dir == ForgeDirection.WEST)
				return this.sides_active;
		}
		else {
			return super.getIcon(side, meta);
		}

		return this.blockIcon;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		//This code could use some optimization -Dark
		if (tile instanceof IInventory)
		{
			IInventory inventory = (IInventory)tile;
			for (int i1 = 0; i1 < inventory.getSizeInventory(); ++i1)
			{
				ItemStack itemstack = inventory.getStackInSlot(i1);

				if (itemstack != null)
				{
					float f = world.rand.nextFloat() * 0.8F + 0.1F;
					float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
					EntityItem entityitem;

					for (float f2 = world.rand.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; world.spawnEntityInWorld(entityitem))
					{
						int j1 = world.rand.nextInt(21) + 10;

						if (j1 > itemstack.stackSize)
						{
							j1 = itemstack.stackSize;
						}

						itemstack.stackSize -= j1;
						entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));
						float f3 = 0.05F;
						entityitem.motionX = (double)((float)world.rand.nextGaussian() * f3);
						entityitem.motionY = (double)((float)world.rand.nextGaussian() * f3 + 0.2F);
						entityitem.motionZ = (double)((float)world.rand.nextGaussian() * f3);

						if (itemstack.hasTagCompound())
						{
							entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
						}
					}
				}
			}

			world.func_147453_f(x, y, z, block);
		}

		super.breakBlock(world, x, y, z, block, meta);
	}
}
