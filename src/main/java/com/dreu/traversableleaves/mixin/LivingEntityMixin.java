package com.dreu.traversableleaves.mixin;

import com.dreu.traversableleaves.interfaces.ITraversableEntity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("unused")
@Mixin(LivingEntity.class)
public class LivingEntityMixin implements ITraversableEntity {

  @Shadow
  protected boolean jumping;

  @Unique
  private boolean isStuckInLeaves = false;

  @Override
  public boolean isStuckInLeaves() {
    return isStuckInLeaves;
  }

  @Override
  public void setStuckInLeaves(boolean bool) {
    isStuckInLeaves = bool;
  }

  @Override
  public boolean isTLJumping() {
    return this.jumping;
  }
}
