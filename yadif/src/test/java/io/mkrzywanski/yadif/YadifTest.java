package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.test.A;
import io.mkrzywanski.yadif.test.B;
import io.mkrzywanski.yadif.test.C;
import io.mkrzywanski.yadif.test.ConfigWithDependencies;
import io.mkrzywanski.yadif.test.CycleConfig1;
import io.mkrzywanski.yadif.test.DummyConfig;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

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
        final var a = context.getInstance("io.mkrzywanski.yadif.test.A", A.class);
        final var b = context.getInstance("io.mkrzywanski.yadif.test.B", B.class);
        final var c = context.getInstance("io.mkrzywanski.yadif.test.C", C.class);

        assertThat(a).isPresent();
        assertThat(b).isPresent();
        assertThat(c).isPresent();
    }

    @Test
    void shouldThrowExceptionWhenCycleIsDetected() {
        final ThrowableAssert.ThrowingCallable code = () -> Yadif.fromConfig(CycleConfig1.class);

        assertThatCode(code)
                .isExactlyInstanceOf(DependencyCycleDetectedException.class)
                .extracting("cycles", InstanceOfAssertFactories.ITERABLE)
                .containsExactlyInAnyOrder(path(List.of(A.class, B.class, A.class)), path(List.of(B.class, A.class, B.class)));
    }

    private Path path(final List<Class<?>> paths) {
        final Path path = new Path();
        paths.forEach(path::add);
        return path;
    }
}
