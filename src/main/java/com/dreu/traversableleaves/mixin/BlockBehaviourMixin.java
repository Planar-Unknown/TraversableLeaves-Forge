package com.dreu.traversableleaves.mixin;

import com.dreu.traversableleaves.interfaces.ITraversableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("unused")
@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin implements ITraversableBlock {

  @Override
  public VoxelShape accessGetCollisionShapeTL(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext collisionContext) {
    return this.getCollisionShape(blockState, level, blockPos, collisionContext);
  }

  @Shadow
  protected abstract VoxelShape getCollisionShape(BlockState p_60572_, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_);
}
