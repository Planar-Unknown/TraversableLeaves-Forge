package com.dreu.traversableleaves.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

import static com.dreu.traversableleaves.TraversableLeaves.MODID;

public class PacketHandler {
  public static final SimpleChannel CHANNEL =
      ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(MODID, "main"))
          .networkProtocolVersion(1)
          .clientAcceptedVersions(Channel.VersionTest.exact(1))
          .serverAcceptedVersions(Channel.VersionTest.exact(1))
          .simpleChannel();

  public static void register() {
    CHANNEL.messageBuilder(SyncConfigS2CPacket.class)
        .decoder(SyncConfigS2CPacket::new)
        .encoder(SyncConfigS2CPacket::toBytes)
        .consumerMainThread(SyncConfigS2CPacket::handle)
        .add();
  }
}
