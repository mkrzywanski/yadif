package io.mkrzywanski.yadif.test.packagescan.mixed;

import io.mkrzywanski.yadif.annotation.Instance;
import io.mkrzywanski.yadif.annotation.PackageScan;

@PackageScan("io.mkrzywanski.yadif.test.packagescan.mixed.component")
public class Config {
    @Instance
    public A a() {
        return new A();
    }
}
