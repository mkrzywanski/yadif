package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.api.NoUniqueBeanDefinitionException;
import io.mkrzywanski.yadif.test.qualifier.simple.Config;
import io.mkrzywanski.yadif.test.qualifier.simple.component.A;
import io.mkrzywanski.yadif.test.qualifier.simple.component.B;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class YadifQualifierTest {
    @Test
    void shouldCreateBeansWhenUsingQualifier() {
        final Context context = Yadif.fromConfig(Config.class);
        final var a = context.getByType(A.class);
        final var b = context.getInstanceOptional("io.mkrzywanski.yadif.test.qualifier.simple.component.B", B.class);

        assertThat(a).hasSize(2);
        assertThat(b).isPresent()
                .hasValueSatisfying(b1 -> {
                    assertThat(a).contains(b1.getA());
                });
    }

    @Test
    void shouldThrowExceptionWhenTryingToGetSingleBeanButMoreOfThisTypeExist() {
        final Context context = Yadif.fromConfig(Config.class);
        final ThrowableAssert.ThrowingCallable code = () -> context.getInstanceOptional("io.mkrzywanski.yadif.test.qualifier.simple.component.A", A.class);
        assertThatCode(code).isExactlyInstanceOf(NoUniqueBeanDefinitionException.class);
    }
}
