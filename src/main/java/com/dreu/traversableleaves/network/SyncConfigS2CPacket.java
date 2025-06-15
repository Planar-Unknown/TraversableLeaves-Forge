package com.dreu.traversableleaves.network;

import com.dreu.traversableleaves.events.ForgeEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import static com.dreu.traversableleaves.TraversableLeaves.configHasBeenParsed;
import static com.dreu.traversableleaves.config.TLConfig.*;

public class SyncConfigS2CPacket {



  public SyncConfigS2CPacket(FriendlyByteBuf buf) {
    MOVEMENT_PENALTY = buf.readFloat();
    ARMOR_SCALE_FACTOR = buf.readFloat();
    ARMOR_HELPS = buf.readBoolean();
    IS_ENTITIES_WHITELIST = buf.readBoolean();

    int bounds = buf.readInt();
    for (int i = 0; i < bounds; i++)
      TL_BLOCKS.add(new ResourceLocation(buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString()));

    bounds = buf.readInt();
    for (int i = 0; i < bounds; i++)
      TL_ENTITIES.add(new ResourceLocation(buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString()));
  }

  public SyncConfigS2CPacket() {
    if (!configHasBeenParsed) {
      parse();
      populate();
      configHasBeenParsed = true;
    }
  }

  public void toBytes(FriendlyByteBuf buf) {
    buf.writeFloat(MOVEMENT_PENALTY);
    buf.writeFloat(ARMOR_SCALE_FACTOR);
    buf.writeBoolean(ARMOR_HELPS);
    buf.writeBoolean(IS_ENTITIES_WHITELIST);

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
    for (ResourceLocation block : TL_BLOCKS) {
      System.out.println(block.toString());
    }
    context.get().enqueueWork(() -> ForgeEvents.lastServerWasLocal = Minecraft.getInstance().isLocalServer());
    context.get().setPacketHandled(true);
  }
}
