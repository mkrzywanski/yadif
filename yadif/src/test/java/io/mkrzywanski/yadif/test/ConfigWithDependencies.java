package io.mkrzywanski.yadif.test;

import io.mkrzywanski.yadif.annotation.Instance;

public class ConfigWithDependencies {
    @Instance
    public A a() {
        return new A();
    }

    @Instance
    public C c() {
        return new C();
    }

    @Instance
    public B b(final A a, final C c) {
        return new B(a, c);
    }
}

