package com.example.puree.logs.plugins;

import android.support.annotation.NonNull;

import com.google.protobuf.MessageLite;
import com.mercari.puree.TagPattern;
import com.mercari.puree.outputs.OutputConfiguration;
import com.mercari.puree.outputs.PureeProtobufOutput;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OutBufferedProtobufTag extends PureeProtobufOutput {
    public static final String TYPE = "protobuf_tag";

    private static TagPattern tagPattern;

    @Override
    public TagPattern getTagPattern() {
        return tagPattern;
    }

    private static WeakReference<OutBufferedProtobufTag.Callback> callbackRef = new WeakReference<>(null);

    public static void register(OutBufferedProtobufTag.Callback callback) {
        callbackRef = new WeakReference<>(callback);
    }

    public static boolean setTag(@NonNull String tag) {
        tagPattern = TagPattern.fromString(tag);
        return tagPattern != null;
    }

    public static void unregister() {
        callbackRef.clear();
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Nonnull
    @Override
    public OutputConfiguration configure(OutputConfiguration conf) {
        return conf;
    }

    @Override
    public void emit(MessageLite protoLog) {
        OutBufferedProtobufTag.Callback callback = callbackRef.get();
        if (callback == null) {
            return;
        }
        callback.onEmit(protoLog);
    }

    public interface Callback {

        void onEmit(MessageLite protoLog);
    }
}
