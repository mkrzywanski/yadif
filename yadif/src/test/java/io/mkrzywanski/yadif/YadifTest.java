package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.test.DummyConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class YadifTest {

    @Test
    void shouldInitializeContext() {
        final Context context = Yadif.fromConfig(new DummyConfig());
        final var instance = context.getInstance("java.lang.Object", Object.class);

        assertThat(instance).isPresent();
    }

    @Test
    void shouldInitializeContextByClass() {
        final Context context = Yadif.fromConfig(DummyConfig.class);
        final var instance = context.getInstance("java.lang.Object", Object.class);

        assertThat(instance).isPresent();
    }
}
