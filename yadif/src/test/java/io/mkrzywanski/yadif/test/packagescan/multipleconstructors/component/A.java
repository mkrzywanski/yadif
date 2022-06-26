package io.mkrzywanski.yadif.test.packagescan.multipleconstructors.component;

import io.mkrzywanski.yadif.annotation.Component;
import io.mkrzywanski.yadif.annotation.FactoryConstructor;

@Component
public class A {

    private final B b;

    A() {
        this.b = new B();
    }

    @FactoryConstructor
    A(final B b) {
        this.b = b;
    }
}
