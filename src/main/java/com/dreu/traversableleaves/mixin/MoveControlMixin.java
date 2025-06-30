package com.dreu.traversableleaves.mixin;

import com.dreu.traversableleaves.interfaces.ITraversableBlock;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("unused")
@Mixin(MoveControl.class)
public abstract class MoveControlMixin {
  @ModifyVariable(
      method = "tick",
      at = @At(
          value = "INVOKE_ASSIGN",
          target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
      )
  )
  private BlockState modifyBlockState(BlockState original) {
    if (original.getBlock() instanceof ITraversableBlock traversable && traversable.isTraversable()) {
      return Blocks.AIR.defaultBlockState();
    }
    return original;
  }
}

