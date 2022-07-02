package io.mkrzywanski.yadif.test.packagescan.mixed.component;

import io.mkrzywanski.yadif.annotation.Component;
import io.mkrzywanski.yadif.test.packagescan.mixed.A;

@Component
public class B {

    private final A a;

    public B(final A a) {
        this.a = a;
    }

    public A getA() {
        return a;
    }
}
