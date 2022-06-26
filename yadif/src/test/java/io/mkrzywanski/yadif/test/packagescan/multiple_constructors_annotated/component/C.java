package io.mkrzywanski.yadif.test.packagescan.multiple_constructors_annotated.component;

import io.mkrzywanski.yadif.annotation.Component;
import io.mkrzywanski.yadif.annotation.FactoryConstructor;

@Component
class C {
    @FactoryConstructor
    C(final A a) {
    }

    @FactoryConstructor
    C(final B b) {
    }
}
