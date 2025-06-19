package com.dreu.traversableleaves.mixin;

import com.dreu.traversableleaves.interfaces.ITraversableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(WalkNodeEvaluator.class)
public class WalkNodeEvaluatorMixin {

  @Redirect(
      method = "getFloorLevel(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)D",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"
      )
  )
  private static VoxelShape redirectCollisionShape(BlockState blockState, BlockGetter level, BlockPos blockPos) {
    if (blockState.getBlock() instanceof ITraversableBlock iTraversable && iTraversable.isTraversable())
      return Shapes.empty();
    return blockState.getCollisionShape(level, blockPos);
  }

  @Inject(method = "getPathTypeStatic", at = @At("HEAD"), cancellable = true)
  private static void injectGetBlockPathTypeStatic(Mob mob, BlockPos blockPos, CallbackInfoReturnable<PathType> cir) {
    if (mob.level().getBlockState(blockPos).getBlock() instanceof ITraversableBlock iTraversable && iTraversable.isTraversable())
      cir.setReturnValue(PathType.WALKABLE);
  }
}
