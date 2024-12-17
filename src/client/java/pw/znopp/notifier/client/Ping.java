package pw.znopp.notifier.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class Ping {
    public static void pingPlayer(ClientPlayerEntity player, String messageType) {

        switch (messageType) {
            case "server" -> playSound(player, SoundEvents.ENTITY_PLAYER_LEVELUP, 0.6f);
            case "negative" -> playSound(player, SoundEvents.BLOCK_BEACON_DEACTIVATE, 1.5f);
            case "client" -> playSound(player, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.7f);
            case "username" -> playSound(player, SoundEvents.BLOCK_AMETHYST_BLOCK_PLACE, 0.5f);
            case "reply" -> playSound(player, SoundEvents.BLOCK_BELL_USE, 1f);
        }
    }

    private static void playSound(ClientPlayerEntity player, SoundEvent sound, float pitch) {
        player.playSoundToPlayer(sound, SoundCategory.MASTER, 1.0f, pitch);
    }
}
