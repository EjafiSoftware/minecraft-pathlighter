package com.ejafi.toggle;

import com.ejafi.Pathlighter;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TogglePayload() implements CustomPacketPayload {
    public static final Type<TogglePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Pathlighter.MOD_ID, "toggle"));
    static final StreamCodec<ByteBuf, TogglePayload> STREAM_CODEC = StreamCodec.unit(new TogglePayload());

    @Override
    public Type<TogglePayload> type() {
        return TYPE;
    }
}
