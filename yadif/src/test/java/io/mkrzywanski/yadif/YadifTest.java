package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.api.YadifBeanInsantiationException;
import io.mkrzywanski.yadif.test.A;
import io.mkrzywanski.yadif.test.B;
import io.mkrzywanski.yadif.test.C;
import io.mkrzywanski.yadif.test.ConfigWithDependencies;
import io.mkrzywanski.yadif.test.DummyConfig;
import io.mkrzywanski.yadif.test.packagescan.simple.Config;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

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
        assertThat(c).isPresent();
        assertThat(b).isPresent().hasValueSatisfying(b1 -> {
            assertThat(b1.getA()).isSameAs(a.get());
            assertThat(b1.getC()).isSameAs(c.get());
        });
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
        assertThat(b).isPresent().hasValueSatisfying(b1 -> assertThat(b1.getA()).isSameAs(a.get()));

    }

    @Test
    void shouldInstantiateBeansWhenUsingPackageScanAndOneBeanHasMultipleConstructors() {
        final Context context = Yadif.fromConfig(io.mkrzywanski.yadif.test.packagescan.multipleconstructors.Config.class);
        final var a = context.getInstance("io.mkrzywanski.yadif.test.packagescan.multipleconstructors.component.A", io.mkrzywanski.yadif.test.packagescan.multipleconstructors.component.A.class);
        final var b = context.getInstance("io.mkrzywanski.yadif.test.packagescan.multipleconstructors.component.B", io.mkrzywanski.yadif.test.packagescan.multipleconstructors.component.B.class);

        assertThat(b).isPresent();
        assertThat(a).isPresent().hasValueSatisfying(a1 -> assertThat(a1.getB()).isSameAs(b.get()));

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

}
