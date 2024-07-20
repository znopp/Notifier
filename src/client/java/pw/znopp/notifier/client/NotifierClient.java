package pw.znopp.notifier.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class NotifierClient implements ClientModInitializer {
    String[] serverMessages = new String[] {
            "your island study",
            "skill points!",
            "skill point!",
            "you found a pet egg!",
            "success! sale breakdown:",
            "You received",
            "You bought"
    };

    String[] playerMessages = new String[] {
    };

    @Override
    public void onInitializeClient() {

        // Exclusively for server messages, but can contain messages sent by players depending on server plugins/mods
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {

            // Player and their username
            ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
            String playerName = clientPlayer.getGameProfile().getName().toLowerCase();

            String messageString = message.getString().toLowerCase();

            // Regex filter for messages sent by any user
            // Examples:
            // [optional rank] (optional asterisk)any_username: message
            // [optional rank] (optional asterisk)<any_username> message
            List<Pattern> USERNAME_PATTERNS = Arrays.asList(
                    Pattern.compile("^(:?\\[.+] )?(:?\\*)?[a-zA-Z0-9_]{3,16}: .+"),
                    Pattern.compile("^(:?\\[.+] )?(:?\\*)?<[a-zA-Z0-9_]{3,16}> .+")
            );

            // Regex filter for messages sent by you
            // Examples:
            // [optional rank] (optional asterisk)your_username: message
            // [optional rank] (optional asterisk)<your_username> message
            List<Pattern> SELF_PATTERNS = Arrays.asList(
                    Pattern.compile("^(:?\\[.+] )?(:?\\*)?" + playerName + ": .+"),
                    Pattern.compile("^(:?\\[.+] )?(:?\\*)?<" + playerName + "> .+")
            );

            boolean fromPlayer = false;
            boolean fromSelf = false;




            // if message matches regex, the message is probably from a user
            for (Pattern pattern : USERNAME_PATTERNS) {
                if (pattern.matcher(messageString).matches()) {
                    fromPlayer = true;
                }
            }

            // if message matches regex, the message is probably from the mod user
            for (Pattern pattern : SELF_PATTERNS) {
                if (pattern.matcher(messageString).matches()) {
                    fromSelf = true;
                }
            }

            // if a message contains one of the serverMessages keywords
            for (String keyword : serverMessages) {
                if (fromPlayer) break;

                if (messageString.contains(keyword.toLowerCase())) {
                    Ping.pingPlayer(clientPlayer, true);
                }
            }

            // If a message contains one of the playerMessages keywords
            for (String keyword : playerMessages) {
                if (messageString.contains(keyword.toLowerCase())) {
                    Ping.pingPlayer(clientPlayer, false);
                }
            }

            // If a message contains your username, but is not sent by you
            if (messageString.contains(playerName) && !fromSelf) {
                Ping.pingPlayer(clientPlayer, false);
            }

        });



        // Exclusively for messages sent by players, should never contain server messages
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {

            // Player and their username
            ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
            String playerName = clientPlayer.getGameProfile().getName().toString().toLowerCase();

            String messageString = message.getString().toLowerCase();

            String playerSender = sender.getName().toLowerCase();



            // If a message contains one of the playerMessages keywords
            for (String keyword : playerMessages) {
                if (messageString.contains(keyword.toLowerCase())) {
                    Ping.pingPlayer(clientPlayer, false);
                }
            }

            // If a message contains your username, but is not sent by you
            if (messageString.contains(playerName) && !Objects.equals(playerSender, playerName)) {
                Ping.pingPlayer(clientPlayer, false);
            }
        });
    }
}
