package com.dreu.traversableleaves.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import static com.dreu.traversableleaves.TraversableLeaves.configHasBeenPopulated;
import static com.dreu.traversableleaves.config.TLConfig.*;

public class SyncConfigS2CPacket {



  public SyncConfigS2CPacket(FriendlyByteBuf buf) {
    MOVEMENT_MULTIPLIER = buf.readFloat();
    ARMOR_SCALE_FACTOR = buf.readFloat();
    IS_ENTITIES_WHITELIST = buf.readBoolean();
    CAN_CLIMB = buf.readBoolean();

    int bounds = buf.readInt();
    for (int i = 0; i < bounds; i++)
      TL_BLOCKS.add(new ResourceLocation(buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString()));

    bounds = buf.readInt();
    for (int i = 0; i < bounds; i++)
      TL_ENTITIES.add(new ResourceLocation(buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString()));
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

    buf.writeInt(TL_BLOCKS.size());
    for (ResourceLocation block : TL_BLOCKS) {
      buf.writeInt(block.toString().length());
      buf.writeCharSequence(block.toString(), StandardCharsets.UTF_8);
    }

    buf.writeInt(TL_ENTITIES.size());
    for (ResourceLocation entity : TL_ENTITIES) {
      buf.writeInt(entity.toString().length());
      buf.writeCharSequence(entity.toString(), StandardCharsets.UTF_8);
    }
  }

  public void handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> configHasBeenPopulated = false);
    context.get().setPacketHandled(true);
  }
}
