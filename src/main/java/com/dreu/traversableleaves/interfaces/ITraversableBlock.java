package com.dreu.traversableleaves.interfaces;

import static com.dreu.traversableleaves.config.TLConfig.TL_BLOCKS;

public interface ITraversableBlock {
  default boolean isTraversable() {
    return TL_BLOCKS.contains(this);
  }
}
