package io.mkrzywanski.yadif.test.qualifier.simple;

import io.mkrzywanski.yadif.annotation.Instance;
import io.mkrzywanski.yadif.annotation.Qualifier;
import io.mkrzywanski.yadif.test.qualifier.simple.component.A;
import io.mkrzywanski.yadif.test.qualifier.simple.component.B;

public class Config {
    @Instance
    public A a() {
        return new A();
    }

    @Instance
    public A aa() {
        return new A();
    }

    @Instance
    public B b(@Qualifier("a") final A a) {
        return new B(a);
    }
}
