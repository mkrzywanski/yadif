package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.test.ConfigWithDependencies;
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

    @Test
    void shouldInitializeContextWithConfigContainingDependencies() {
        final Context context = Yadif.fromConfig(ConfigWithDependencies.class);
        final var a = context.getInstance("io.mkrzywanski.yadif.test.ConfigWithDependencies$A", ConfigWithDependencies.A.class);
        final var b = context.getInstance("io.mkrzywanski.yadif.test.ConfigWithDependencies$B", ConfigWithDependencies.B.class);
        final var c = context.getInstance("io.mkrzywanski.yadif.test.ConfigWithDependencies$C", ConfigWithDependencies.C.class);

        assertThat(a).isPresent();
        assertThat(b).isPresent();
        assertThat(c).isPresent();
    }
}
