package com.ejafi.preference;

import com.ejafi.Pathlighter;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PreferencesPayload(Preferences preferences) implements CustomPacketPayload {
    public static final Type<PreferencesPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Pathlighter.MOD_ID, "preferences"));
    static final StreamCodec<ByteBuf, PreferencesPayload> STREAM_CODEC =
            ByteBufCodecs.fromCodec(Preferences.CODEC).map(PreferencesPayload::new, PreferencesPayload::preferences);

    @Override
    public Type<PreferencesPayload> type() {
        return TYPE;
    }
}
