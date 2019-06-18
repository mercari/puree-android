package com.mercari.puree;

import com.google.protobuf.MessageLite;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface PureeProtobufFilter {

    @Nullable
    MessageLite apply(MessageLite protoLog);
}
