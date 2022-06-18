package io.mkrzywanski.yadif;

public final class Yadif {

    private static final FromConfigContextFactory CONTEXT_FROM_CONFIG_CREATOR = new FromConfigContextFactory();

    public static Context fromConfig(final Object object) {
        return CONTEXT_FROM_CONFIG_CREATOR.fromConfig(object);
    }

    public static Context fromConfig(final Class<?> clazz) {
        return CONTEXT_FROM_CONFIG_CREATOR.fromConfig(clazz);
    }
}
