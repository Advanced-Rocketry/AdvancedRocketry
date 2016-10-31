package zmaster587.advancedRocketry.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityItemAbducted extends Entity {

	public int age;
	public int lifespan;
	EntityItem itemEntity;
	public EntityItemAbducted(World par1World, double par2, double par4,
			double par6, ItemStack par8ItemStack) {
		super(par1World);
		
		this.setEntityItemStack(par8ItemStack);
		setPosition(par2, par4, par6);
		this.setSize(0.25F, 0.25F);
		this.noClip = true;
		this.lifespan = 200;
		this.motionX = 0;
		this.motionY = 2;
		this.motionZ = 0;
	}
	
	public EntityItemAbducted(World world) {
		super(world);
		this.noClip = true;
		this.lifespan = 200;
		this.motionX = 0;
		this.motionY = 2;
		this.motionZ = 0;
	}
	
    protected void entityInit()
    {
        this.getDataWatcher().addObjectByDataType(10, 5);
    }
	
    public EntityItem getItemEntity() {
    	if(itemEntity == null) {
    		itemEntity = new EntityItem(worldObj, this.posX, this.posY, this.posZ, getEntityItem());
    	}
    	return itemEntity;
    }
    
	@Override
	public void onUpdate() {
		ItemStack stack = this.getDataWatcher().getWatchableObjectItemStack(10);
        
        //super.onEntityUpdate();
        
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        ++this.age;

        ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);

        if (!this.worldObj.isRemote && this.age >= lifespan)
        {
        	this.setDead();
        }

        if (item != null && item.stackSize <= 0)
        {
            this.setDead();
        }
	}
	
    /**
     * Returns the ItemStack corresponding to the Entity (Note: if no item exists, will log an error but still return an
     * ItemStack containing Block.stone)
     */
    public ItemStack getEntityItem()
    {
        ItemStack itemstack = this.getDataWatcher().getWatchableObjectItemStack(10);
        return itemstack == null ? new ItemStack(Blocks.stone) : itemstack;
    }

    /**
     * Sets the ItemStack for this entity
     */
    public void setEntityItemStack(ItemStack p_92058_1_)
    {
        this.getDataWatcher().updateObject(10, p_92058_1_);
        this.getDataWatcher().setObjectWatched(10);
    }
    
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {}
    

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound p_70014_1_)
    {
        p_70014_1_.setShort("Age", (short)this.age);
        p_70014_1_.setInteger("Lifespan", lifespan);


        if (this.getEntityItem() != null)
        {
            p_70014_1_.setTag("Item", this.getEntityItem().writeToNBT(new NBTTagCompound()));
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound p_70037_1_)
    {
        this.age = p_70037_1_.getShort("Age");


        NBTTagCompound nbttagcompound1 = p_70037_1_.getCompoundTag("Item");
        this.setEntityItemStack(ItemStack.loadItemStackFromNBT(nbttagcompound1));

        ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);

        if (item == null || item.stackSize <= 0)
        {
            this.setDead();
        }

        if (p_70037_1_.hasKey("Lifespan"))
        {
            lifespan = p_70037_1_.getInteger("Lifespan");
        }
    }
}
