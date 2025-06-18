package com.dreu.traversableleaves.interfaces;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import static com.dreu.traversableleaves.config.TLConfig.TL_BLOCKS;

public interface ITraversableBlock {
  default boolean isTraversable() {
    return TL_BLOCKS.contains(ForgeRegistries.BLOCKS.getKey((Block) this));
  }
}
