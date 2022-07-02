package io.mkrzywanski.yadif.test.cycle.case2;

import io.mkrzywanski.yadif.annotation.Instance;

public class CycleConfig2 {
    @Instance
    public A a(final B b) {
        return new A(b);
    }

    @Instance
    public B b(final C c) {
        return new B(c);
    }

    @Instance
    public C c(final A a) {
        return new C(a);
    }

}
