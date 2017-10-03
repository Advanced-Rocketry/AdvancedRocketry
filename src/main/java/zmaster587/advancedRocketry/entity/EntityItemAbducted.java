package zmaster587.advancedRocketry.entity;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityItemAbducted extends Entity {

	private static final DataParameter<ItemStack> ITEM = EntityDataManager.<ItemStack>createKey(EntityItem.class, DataSerializers.ITEM_STACK);
	public int lifespan = 6000;
	public int age = 0;
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
        this.getDataManager().register(ITEM,  ItemStack.EMPTY);
    }
    
	@Override
	public void onUpdate() {
		ItemStack stack = this.getDataManager().get(ITEM);
		
        if (this.getEntityItem() == null)
        {
            this.setDead();
        }
        //super.onEntityUpdate();
        
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        this.move(MoverType.SELF,this.motionX, this.motionY, this.motionZ);

        ++this.age;

        if (!this.world.isRemote && this.age >= lifespan)
        {
        	this.setDead();
        }

        if (stack != null && stack.getCount() <= 0)
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
        ItemStack itemstack = (ItemStack)(this.getDataManager().get(ITEM));

        if (itemstack == null)
        {
            return new ItemStack(Blocks.STONE);
        }
        else
        {
            return itemstack;
        }
    }

    /**
     * Sets the ItemStack for this entity
     */
    public void setEntityItemStack( ItemStack stack)
    {
        this.getDataManager().set(ITEM, stack);
        this.getDataManager().setDirty(ITEM);
    }
    
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
        this.setEntityItemStack(new ItemStack(nbttagcompound1));

        ItemStack item = (ItemStack)(this.getDataManager().get(ITEM));

        if (item == null || item.getCount() <= 0)
        {
            this.setDead();
        }

        if (p_70037_1_.hasKey("Lifespan"))
        {
            lifespan = p_70037_1_.getInteger("Lifespan");
        }
    }
    
    public EntityItem getItemEntity() {
    	if(itemEntity == null) {
    		itemEntity = new EntityItem(world, this.posX, this.posY, this.posZ, getEntityItem());
    	}
    	return itemEntity;
    }
}
