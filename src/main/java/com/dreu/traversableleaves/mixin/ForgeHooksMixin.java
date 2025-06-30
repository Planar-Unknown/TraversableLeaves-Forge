package com.dreu.traversableleaves.mixin;

import com.dreu.traversableleaves.interfaces.ITraversableBlock;
import com.dreu.traversableleaves.interfaces.ITraversableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.dreu.traversableleaves.config.TLConfig.CAN_CLIMB;
import static com.dreu.traversableleaves.interfaces.ITraversableEntity.canTraverse;

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
    if (CAN_CLIMB && blockState.getBlock() instanceof ITraversableBlock traversable && traversable.isTraversable()) {
      if (livingEntity instanceof ITraversableEntity iTraversableEntity) {
        if (livingEntity instanceof Player player) {
          if (iTraversableEntity.isTLJumping() && shouldCollide(blockState, level, blockPos, livingEntity))
            return !player.isCrouching() || (player.isCreative() && player.getAbilities().flying);
        } else if (shouldCollide(blockState, level, blockPos, livingEntity)){
          return canTraverse(livingEntity);
        }
      }
    }
    return blockState.isLadder(level, blockPos, livingEntity);
  }

  private static boolean shouldCollide(BlockState blockState, LevelReader level, BlockPos blockPos, LivingEntity livingEntity) {
    return !(livingEntity.position().y >= blockState.getBlock().getCollisionShape(blockState, level, blockPos, CollisionContext.empty()).max(Direction.Axis.Y) + blockPos.getY());
  }
}

