package io.mkrzywanski.yadif.test;

import io.mkrzywanski.yadif.annotation.Instance;

public class CycleConfig1 {
    @Instance
    public A a(final B b) {
        return new A();
    }

    @Instance
    public B b(final A a) {
        return new B();
    }
}
