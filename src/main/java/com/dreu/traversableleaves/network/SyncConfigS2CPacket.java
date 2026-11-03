package com.dreu.traversableleaves.network;

import com.dreu.traversableleaves.TraversableLeaves;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import static com.dreu.traversableleaves.TraversableLeaves.configHasBeenPopulated;
import static com.dreu.traversableleaves.config.TLConfig.*;

public class SyncConfigS2CPacket {
  public SyncConfigS2CPacket(FriendlyByteBuf buf) {
    TL_ENTITIES.clear();
    TL_BLOCKS.clear();
    MOVEMENT_MULTIPLIER = buf.readFloat();
    ARMOR_SCALE_FACTOR = buf.readFloat();
    IS_ENTITIES_WHITELIST = buf.readBoolean();
    CAN_CLIMB = buf.readBoolean();
    MOUNTED_ONLY = buf.readBoolean();

    int bounds = buf.readInt();
    for (int i = 0; i < bounds; i++) {
      ResourceLocation block = new ResourceLocation(buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString());
      if (ForgeRegistries.BLOCKS.containsKey(block))
        TL_BLOCKS.add(ForgeRegistries.BLOCKS.getValue(block));
    }

    bounds = buf.readInt();
    for (int i = 0; i < bounds; i++) {
      ResourceLocation entity = new ResourceLocation(buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString());
      if (ForgeRegistries.ENTITY_TYPES.containsKey(entity))
        TL_ENTITIES.add(ForgeRegistries.ENTITY_TYPES.getValue(entity));
    }
  }

  public SyncConfigS2CPacket() {
    if (!configHasBeenPopulated) {
      parse();
      populate();
      configHasBeenPopulated = true;
    }
  }

  public void toBytes(FriendlyByteBuf buf) {
    buf.writeFloat(MOVEMENT_MULTIPLIER);
    buf.writeFloat(ARMOR_SCALE_FACTOR);
    buf.writeBoolean(IS_ENTITIES_WHITELIST);
    buf.writeBoolean(CAN_CLIMB);
    buf.writeBoolean(MOUNTED_ONLY);

    buf.writeInt(TL_BLOCKS.size());
    for (Block block : TL_BLOCKS) {
      String blockId = ForgeRegistries.BLOCKS.getKey(block).toString();
      buf.writeInt(blockId.length());
      buf.writeCharSequence(blockId, StandardCharsets.UTF_8);
    }

    buf.writeInt(TL_ENTITIES.size());
    for (EntityType<?> entity : TL_ENTITIES) {
      String entityId = ForgeRegistries.ENTITY_TYPES.getKey(entity).toString();
      buf.writeInt(entityId.length());
      buf.writeCharSequence(entityId, StandardCharsets.UTF_8);
    }
  }

  public void handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> TraversableLeaves.configHasBeenPopulated = false);
    context.get().setPacketHandled(true);
  }
}
