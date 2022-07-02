package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.api.CyclePath;
import io.mkrzywanski.yadif.api.DependencyCycleDetectedException;

import io.mkrzywanski.yadif.test.cycle.case1.A;
import io.mkrzywanski.yadif.test.cycle.case1.B;
import io.mkrzywanski.yadif.test.cycle.case1.CycleConfig1;
import io.mkrzywanski.yadif.test.cycle.case2.CycleConfig2;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

class YadifCycleTest {

    @Test
    void shouldThrowExceptionWhenCycleIsDetected() {
        final ThrowableAssert.ThrowingCallable code = () -> Yadif.fromConfig(CycleConfig1.class);

        assertThatCode(code)
                .isExactlyInstanceOf(DependencyCycleDetectedException.class)
                .extracting("cycles", InstanceOfAssertFactories.ITERABLE)
                .containsExactlyInAnyOrder(path(List.of(A.class, B.class, A.class)), path(List.of(B.class, A.class, B.class)));
    }

    @Test
    void shouldThrowExceptionWhenCycleIsDetected2() {
        final ThrowableAssert.ThrowingCallable code = () -> Yadif.fromConfig(CycleConfig2.class);

        assertThatCode(code)
                .isExactlyInstanceOf(DependencyCycleDetectedException.class)
                .extracting("cycles", InstanceOfAssertFactories.ITERABLE)
                .containsExactlyInAnyOrder(
                        path(List.of(io.mkrzywanski.yadif.test.cycle.case2.A.class, io.mkrzywanski.yadif.test.cycle.case2.B.class, io.mkrzywanski.yadif.test.cycle.case2.C.class, io.mkrzywanski.yadif.test.cycle.case2.A.class)),
                        path(List.of(io.mkrzywanski.yadif.test.cycle.case2.B.class, io.mkrzywanski.yadif.test.cycle.case2.C.class, io.mkrzywanski.yadif.test.cycle.case2.A.class, io.mkrzywanski.yadif.test.cycle.case2.B.class)),
                        path(List.of(io.mkrzywanski.yadif.test.cycle.case2.C.class, io.mkrzywanski.yadif.test.cycle.case2.A.class, io.mkrzywanski.yadif.test.cycle.case2.B.class, io.mkrzywanski.yadif.test.cycle.case2.C.class)));
    }

    private CyclePath path(final List<Class<?>> paths) {
        return new CyclePath(paths);
    }
}
