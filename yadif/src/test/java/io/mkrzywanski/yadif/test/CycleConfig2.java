package io.mkrzywanski.yadif.test;

import io.mkrzywanski.yadif.annotation.Instance;

public class CycleConfig2 {
    @Instance
    public A a(final B b) {
        return new A();
    }

    @Instance
    public B b(final C c) {
        return new B();
    }

    @Instance
    public C c(final A a) {
        return new C();
    }

}
