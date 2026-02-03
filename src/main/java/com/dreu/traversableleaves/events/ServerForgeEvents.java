package com.dreu.traversableleaves.events;

import com.dreu.traversableleaves.network.PacketHandler;
import com.dreu.traversableleaves.network.SyncConfigS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(modid = "traversable_leaves", bus = EventBusSubscriber.Bus.FORGE)
public class ServerForgeEvents {
  @SubscribeEvent
  public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    Player player = event.getEntity();
    if (player instanceof ServerPlayer serverPlayer) {
      PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncConfigS2CPacket());
    }
  }
}