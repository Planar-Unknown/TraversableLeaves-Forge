package com.dreu.traversableleaves.mixin;

import com.dreu.traversableleaves.ITraversable;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;

import static com.dreu.traversableleaves.config.TLConfig.TL_BLOCKS;

@SuppressWarnings({"DataFlowIssue", "unused"})
@Mixin(Block.class)
public class BlockMixin implements ITraversable {
  @Override
  public boolean isTraversable() {
    return TL_BLOCKS.contains(ForgeRegistries.BLOCKS.getKey((Block) (Object) this));
  }
}
