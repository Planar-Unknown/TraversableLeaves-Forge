package com.dreu.traversableleaves.mixin;

import com.dreu.traversableleaves.ITraversable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.dreu.traversableleaves.config.TLConfig.*;

@SuppressWarnings({"unused", "deprecation"})
@Mixin(Entity.class)
public class EntityMixin {

  @Redirect(
      method = "checkInsideBlocks",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/level/block/state/BlockState;entityInside(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)V"
      )
  )
  private void redirectEntityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
    if (blockState.getBlock() instanceof ITraversable block && block.isTraversable()) {
      if (!(entity.position().y >= blockState.getBlock().getCollisionShape(blockState, level, blockPos, CollisionContext.empty()).max(Direction.Axis.Y) + blockPos.getY())) {
        if (!(entity instanceof Player player && player.isCreative() && player.getAbilities().flying) && entity instanceof LivingEntity livingEntity) {
          if (level.getBlockState(new BlockPos(livingEntity.position())).getBlock() instanceof ITraversable iTraversable && !iTraversable.isTraversable()) {
            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().multiply((MOVEMENT_PENALTY + getArmorBonus(livingEntity)) * 0.5f, 1, (MOVEMENT_PENALTY + getArmorBonus(livingEntity)) * 0.5f));
          } else {
            livingEntity.makeStuckInBlock(blockState, new Vec3(MOVEMENT_PENALTY + getArmorBonus(livingEntity), 1.0, MOVEMENT_PENALTY + getArmorBonus(livingEntity)));
          }
          createAmbience(entity, blockPos, blockState);
        }
      }
    }

    blockState.entityInside(level, blockPos, entity);
  }

  private float getArmorBonus(LivingEntity entity) {
    return ARMOR_HELPS ? ARMOR_SCALE_FACTOR * Mth.clamp(entity.getArmorValue(), 0, 20) : 0;
  }

  private void createAmbience(Entity entity, BlockPos blockPos, BlockState blockState){
    if (!entity.position().equals(new Vec3(entity.xOld, entity.yOld, entity.zOld))) {
      if (entity.level.getGameTime() % 15 == 1) {
        entity.playSound(blockState.getSoundType().getStepSound(), 0.1f, 0.6f);
      }
      if (entity.level.getGameTime() % 4 == 1){
        double d0 = (double) blockPos.getX() + entity.level.random.nextDouble();
        double d1 = (double) blockPos.getY() + 1;
        double d2 = (double) blockPos.getZ() + entity.level.random.nextDouble();
        entity.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), d0, d1, d2, 0, 0, 0);
      }
    }
  }
}

