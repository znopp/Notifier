package pw.znopp.notifier.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class Ping {
    public static void pingPlayer(ClientPlayerEntity player, boolean serverMessage) {
        if (serverMessage) {
            player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1.0f, 0.6f);
        }
        else {
            player.playSoundToPlayer(SoundEvents.BLOCK_AMETHYST_BLOCK_PLACE, SoundCategory.MASTER, 1.0f, 0.5f);
        }
    }
}
