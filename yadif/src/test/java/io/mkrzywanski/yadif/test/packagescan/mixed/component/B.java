package io.mkrzywanski.yadif.test.packagescan.mixed.component;

import io.mkrzywanski.yadif.annotation.Component;
import io.mkrzywanski.yadif.annotation.FactoryConstructor;
import io.mkrzywanski.yadif.test.packagescan.mixed.A;

@Component
public class B {
    @FactoryConstructor
    public B(final A a) {

    }
}
