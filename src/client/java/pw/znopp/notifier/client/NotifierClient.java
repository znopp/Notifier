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
            "you received",
            "you bought",
            "thank you for voting",
            "activated"
    };

    String[] negativeServerMessages = new String[] {
            "you cannot activate a"
    };

    String[] playerMessages = new String[] {
    };

    @Override
    public void onInitializeClient() {

        // Exclusively for server messages, but can contain messages sent by players depending on server plugins/mods
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {

            boolean hasPinged = false;

            // Player and their username
            ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
            if (clientPlayer == null) return;
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




            // if message matches regex, the message is probably from a user (any user, including the mod user)
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

            // if a message starts with one of the negativeServerMessages keywords
            for (String keyword : negativeServerMessages) {
                if (fromPlayer) break;

                if (messageString.startsWith(keyword.toLowerCase())) {
                    Ping.pingPlayer(clientPlayer, "negative");
                    hasPinged = true;
                    break;
                }
            }

            // if a message starts with one of the serverMessages keywords
            for (String keyword : serverMessages) {
                if (fromPlayer || hasPinged) break;

                if (messageString.startsWith(keyword.toLowerCase()) || messageString.endsWith(keyword.toLowerCase())) {
                    Ping.pingPlayer(clientPlayer, "server");
                    break;
                }
            }


            // if a message is a /msg reply from a player
            if (!fromPlayer && messageString.startsWith("from")) {
                Ping.pingPlayer(clientPlayer, "reply");
            }

            // If a message contains one of the playerMessages keywords

            // this method was intended for custom/private use that can be changed in settings
            // to include your own triggers, but this feature has yet to be added (hence the list is empty)

            for (String keyword : playerMessages) {
                if (messageString.contains(keyword.toLowerCase())) {
                    Ping.pingPlayer(clientPlayer, "client");
                }
            }

            // If a message contains your username, but is not sent by you
            if (messageString.contains(playerName) && !fromSelf && fromPlayer) {
                Ping.pingPlayer(clientPlayer, "username");
            }

        });



        // Exclusively for messages sent by players, should never contain server messages
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {

            if (sender == null) return;

            // Player and their username
            ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
            if (clientPlayer == null) return;
            String playerName = clientPlayer.getGameProfile().getName().toLowerCase();

            String messageString = message.getString().toLowerCase();

            String playerSender = sender.getName().toLowerCase();



            // If a message contains one of the playerMessages keywords

            // this method was intended for custom/private use that can be changed in settings
            // to include your own triggers, but this feature has yet to be added (hence the list is empty)
            for (String keyword : playerMessages) {
                if (messageString.contains(keyword.toLowerCase())) {
                    Ping.pingPlayer(clientPlayer, "client");
                    break;
                }
            }

            // If a message contains your username, but is not sent by you
            if (messageString.contains(playerName) && !Objects.equals(playerSender, playerName)) {
                Ping.pingPlayer(clientPlayer, "username");
            }
        });
    }
}
