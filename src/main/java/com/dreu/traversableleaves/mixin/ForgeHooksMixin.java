package com.dreu.traversableleaves.mixin;

import com.dreu.traversableleaves.ITraversable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.dreu.traversableleaves.config.TLConfig.IS_ENTITIES_WHITELIST;
import static com.dreu.traversableleaves.config.TLConfig.TL_ENTITIES;

@SuppressWarnings({"unused", "deprecation"})
@Mixin(ForgeHooks.class)
public class ForgeHooksMixin {

  @Redirect(
      method = "isLivingOnLadder",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/level/block/state/BlockState;isLadder(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)Z"
      ),
      remap = false
  )
  private static boolean redirectIsLadder(BlockState blockState, LevelReader level, BlockPos blockPos, LivingEntity livingEntity) {
    if (blockState.getBlock() instanceof ITraversable traversable && traversable.isTraversable()) {
      if (!(livingEntity.position().y >= blockState.getBlock().getCollisionShape(blockState, level, blockPos, CollisionContext.empty()).max(Direction.Axis.Y) + blockPos.getY())) {
        if (livingEntity instanceof Player player) {
          return !player.isCrouching() || (player.isCreative() && player.getAbilities().flying);
        } else {
          return IS_ENTITIES_WHITELIST == TL_ENTITIES.contains(ForgeRegistries.ENTITY_TYPES.getKey(livingEntity.getType()));
        }
      }
    }
    return blockState.isLadder(level, blockPos, livingEntity);
  }
}

