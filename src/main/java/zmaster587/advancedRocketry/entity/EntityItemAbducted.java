package zmaster587.advancedRocketry.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryEntities;

public class EntityItemAbducted extends Entity {

	private static final DataParameter<ItemStack> ITEM = EntityDataManager.<ItemStack>createKey(ItemEntity.class, DataSerializers.ITEMSTACK);
	public int lifespan = 6000;
	public int age = 0;
	ItemEntity itemEntity;
	
	public EntityItemAbducted(World par1World, double par2, double par4,
			double par6, ItemStack par8ItemStack) {
		super(AdvancedRocketryEntities.ENTITY_ITEM_ABDUCTED, par1World);
		
		this.setEntityItemStack(par8ItemStack);
		setPosition(par2, par4, par6);
		this.noClip = true;
		this.lifespan = 200;
		this.setMotion(new Vector3d(0,2,0));
	}
	
	public EntityItemAbducted(EntityType<?> type, World world) {
		super(AdvancedRocketryEntities.ENTITY_ITEM_ABDUCTED, world);
		this.noClip = true;
		this.lifespan = 200;
		this.setMotion(new Vector3d(0,2,0));
	}
	
	@Override
    protected void registerData()
    {
        this.getDataManager().register(ITEM,  ItemStack.EMPTY);
    }
    
	@Override
	public void tick() {
		ItemStack stack = this.getDataManager().get(ITEM);
		
        if (this.getEntityItem() == null)
        {
            this.remove();
        }
        //super.onEntityUpdate();
        
        this.prevPosX = this.getPosX();
        this.prevPosY = this.getPosY();
        this.prevPosZ = this.getPosZ();
        
        this.move(MoverType.SELF,this.getMotion());

        ++this.age;

        if (!this.world.isRemote && this.age >= lifespan)
        {
        	this.remove();
        }

        if (stack != null && stack.getCount() <= 0)
        {
            this.remove();
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
    }
    
    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeAdditional(CompoundNBT p_70014_1_)
    {
        p_70014_1_.putShort("Age", (short)this.age);
        p_70014_1_.putInt("Lifespan", lifespan);


        if (this.getEntityItem() != null)
        {
            p_70014_1_.put("Item", this.getEntityItem().write(new CompoundNBT()));
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditional(CompoundNBT p_70037_1_)
    {
        this.age = p_70037_1_.getShort("Age");


        CompoundNBT nbttagcompound1 = p_70037_1_.getCompound("Item");
        this.setEntityItemStack(ItemStack.read(nbttagcompound1));

        ItemStack item = (ItemStack)(this.getDataManager().get(ITEM));

        if (item == null || item.getCount() <= 0)
        {
            this.remove();
        }

        if (p_70037_1_.contains("Lifespan"))
        {
            lifespan = p_70037_1_.getInt("Lifespan");
        }
    }
    
    public ItemEntity getItemEntity() {
    	if(itemEntity == null) {
    		itemEntity = new ItemEntity(world, this.getPosX(), this.getPosY(), this.getPosZ(), getEntityItem());
    	}
    	return itemEntity;
    }

	@Override
	public IPacket<?> createSpawnPacket() {
		return new SSpawnObjectPacket(this);
	}
}
