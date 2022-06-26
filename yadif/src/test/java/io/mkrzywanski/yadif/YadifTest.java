package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.api.CyclePath;
import io.mkrzywanski.yadif.api.DependencyCycleDetectedException;
import io.mkrzywanski.yadif.api.YadifBeanInsantiationException;
import io.mkrzywanski.yadif.test.A;
import io.mkrzywanski.yadif.test.B;
import io.mkrzywanski.yadif.test.C;
import io.mkrzywanski.yadif.test.ConfigWithDependencies;
import io.mkrzywanski.yadif.test.CycleConfig1;
import io.mkrzywanski.yadif.test.CycleConfig2;
import io.mkrzywanski.yadif.test.DummyConfig;
import io.mkrzywanski.yadif.test.packagescan.simple.Config;
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
                .isExactlyInstanceOf(DependencyCycleDetectedException.class);
//                .extracting("cycles", InstanceOfAssertFactories.ITERABLE)
//                .containsExactlyInAnyOrder(path(List.of(A.class, B.class, A.class)), path(List.of(B.class, A.class, B.class)));
    }

    @Test
    void shouldThrowExceptionWhenCycleIsDetected2() {
        final ThrowableAssert.ThrowingCallable code = () -> Yadif.fromConfig(CycleConfig2.class);

        assertThatCode(code)
                .isExactlyInstanceOf(DependencyCycleDetectedException.class)
                .extracting("cycles", InstanceOfAssertFactories.ITERABLE)
                .containsExactlyInAnyOrder(
                        path(List.of(A.class, B.class, C.class, A.class)),
                        path(List.of(B.class, C.class, A.class, B.class)),
                        path(List.of(C.class, A.class, B.class, C.class)));
    }

    @Test
    void shouldInstantiateSimplePackagedScannedBeans() {
        final Context context = Yadif.fromConfig(Config.class);
        final var a = context.getInstance("io.mkrzywanski.yadif.test.packagescan.simple.components.A", io.mkrzywanski.yadif.test.packagescan.simple.components.A.class);
        final var b = context.getInstance("io.mkrzywanski.yadif.test.packagescan.simple.components.B", io.mkrzywanski.yadif.test.packagescan.simple.components.B.class);

        assertThat(a).isPresent();
        assertThat(b).isPresent();

    }

    @Test
    void shouldInstantiateBeansWhenUsingPackageScanAndConfigInstances() {
        final Context context = Yadif.fromConfig(io.mkrzywanski.yadif.test.packagescan.mixed.Config.class);
        final var a = context.getInstance("io.mkrzywanski.yadif.test.packagescan.mixed.A", io.mkrzywanski.yadif.test.packagescan.mixed.A.class);
        final var b = context.getInstance("io.mkrzywanski.yadif.test.packagescan.mixed.component.B", io.mkrzywanski.yadif.test.packagescan.mixed.component.B.class);

        assertThat(a).isPresent();
        assertThat(b).isPresent();

    }

    @Test
    void shouldInstantiateBeansWhenUsingPackageScanAndOneBeanHasMultipleConstructors() {
        final Context context = Yadif.fromConfig(io.mkrzywanski.yadif.test.packagescan.multipleconstructors.Config.class);
        final var a = context.getInstance("io.mkrzywanski.yadif.test.packagescan.multipleconstructors.component.A", io.mkrzywanski.yadif.test.packagescan.multipleconstructors.component.A.class);
        final var b = context.getInstance("io.mkrzywanski.yadif.test.packagescan.multipleconstructors.component.B", io.mkrzywanski.yadif.test.packagescan.multipleconstructors.component.B.class);

        assertThat(a).isPresent();
        assertThat(b).isPresent();

    }

    @Test
    void shouldThrowExceptionWhenBeanHasMultipleAnnotatedConstructors() {
        final ThrowableAssert.ThrowingCallable code = () -> Yadif.fromConfig(io.mkrzywanski.yadif.test.packagescan.multiple_constructors_annotated.Config.class);

        assertThatCode(code).isExactlyInstanceOf(YadifBeanInsantiationException.class);

    }


    @Test
    void shouldThrowExceptionWhenBeanRequiresDependencyThatDoesNotExistInContext() {
        final ThrowableAssert.ThrowingCallable code = () -> Yadif.fromConfig(io.mkrzywanski.yadif.test.packagescan.nobeancreated.Config.class);

        assertThatCode(code).isExactlyInstanceOf(YadifBeanInsantiationException.class);

    }

    private CyclePath path(final List<Class<?>> paths) {
        return new CyclePath(paths);
    }
}
