package com.dreu.traversableleaves.interfaces;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import static com.dreu.traversableleaves.config.TLConfig.*;

public interface ITraversableEntity {

  static boolean canTraverse(Entity entity) {
    if (MOUNTED_ONLY && !(entity.isPassenger() || entity.hasControllingPassenger())) return false;
    return IS_ENTITIES_WHITELIST == TL_ENTITIES.contains(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()));
  }

  default float getArmorBonus() {
    if (this instanceof LivingEntity livingEntity)
      return Mth.clamp(ARMOR_SCALE_FACTOR * livingEntity.getArmorValue(), 0, 1);
    else return 0;
  }

  boolean isTLJumping();

  boolean isStuckInLeaves();

  void setStuckInLeaves(boolean bool);
}