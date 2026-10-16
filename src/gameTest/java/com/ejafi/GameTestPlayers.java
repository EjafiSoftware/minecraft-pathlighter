package com.ejafi;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.UUID;
import net.datafaker.Faker;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

/** Joins a real server player over an in-memory connection, replacing vanilla's deprecated mock player. */
public final class GameTestPlayers {
    private static final Faker FAKER = new Faker();
    private static final String PLAYER_NAME_PATTERN = "[a-z]{3,16}";

    private GameTestPlayers() {}

    public static ServerPlayer joinPlayer(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        MinecraftServer server = level.getServer();
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(
                new GameProfile(UUID.fromString(FAKER.internet().uuid()), FAKER.regexify(PLAYER_NAME_PATTERN)), false);
        ServerPlayer player = new ServerPlayer(server, level, cookie.gameProfile(), cookie.clientInformation());
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        NetworkRegistry.configureMockConnection(connection);
        server.getPlayerList().placeNewPlayer(connection, player, cookie);
        return player;
    }

    public static void sendToServer(ServerPlayer player, CustomPacketPayload payload) {
        player.connection.handleCustomPayload(new ServerboundCustomPayloadPacket(payload));
    }
}
