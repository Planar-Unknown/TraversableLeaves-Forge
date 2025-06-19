package com.dreu.traversableleaves.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

import static com.dreu.traversableleaves.config.TLConfig.TL_BLOCKS;

public interface ITraversableBlock {
  default boolean isTraversable() {
    return TL_BLOCKS.contains(ForgeRegistries.BLOCKS.getKey((Block) this));
  }
  VoxelShape accessGetCollisionShapeTL(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext collisionContext);
}
