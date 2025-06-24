package com.dreu.traversableleaves.events;

import com.dreu.traversableleaves.network.PacketHandler;
import com.dreu.traversableleaves.network.SyncConfigS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import static com.dreu.traversableleaves.TraversableLeaves.MODID;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MODID, bus = FORGE, value = Dist.DEDICATED_SERVER)
public class ForgeEvents {

  @SubscribeEvent
  public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    if (event.getEntity() instanceof ServerPlayer serverPlayer) {
      PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncConfigS2CPacket());
    }
  }
}
