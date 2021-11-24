package zmaster587.advancedRocketry.api;

import java.util.function.Supplier;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpaceSuitArmorMaterial implements IArmorMaterial {

   private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
   private final String name;
   private final int maxDamageFactor;
   private final int[] damageReductionAmountArray;
   private final int enchantability;
   private final SoundEvent soundEvent;
   private final float toughness;
   private final float field_234660_o_;
   private final LazyValue<Ingredient> repairMaterial;

   public SpaceSuitArmorMaterial(String name, int damage, int[] damageReductionArray, int enchange, SoundEvent sound, float toughness, float noIdea, Supplier<Ingredient> repair) {
      this.name = name;
      this.maxDamageFactor = damage;
      this.damageReductionAmountArray = damageReductionArray;
      this.enchantability = enchange;
      this.soundEvent = sound;
      this.toughness = toughness;
      this.field_234660_o_ = noIdea;
      this.repairMaterial = new LazyValue<>(repair);
   }

   public int getDurability(EquipmentSlotType slotIn) {
      return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamageFactor;
   }

   public int getDamageReductionAmount(EquipmentSlotType slotIn) {
      return this.damageReductionAmountArray[slotIn.getIndex()];
   }

   public int getEnchantability() {
      return this.enchantability;
   }

   public SoundEvent getSoundEvent() {
      return this.soundEvent;
   }

   public Ingredient getRepairMaterial() {
      return this.repairMaterial.getValue();
   }

   @OnlyIn(value=Dist.CLIENT)
   public String getName() {
      return this.name;
   }

   public float getToughness() {
      return this.toughness;
   }

   @Override
   //public float getKnockbackResistance() {
   //   return 0;
   //}

   public float getKnockbackResistance() {
      return this.field_234660_o_;
   }

}
