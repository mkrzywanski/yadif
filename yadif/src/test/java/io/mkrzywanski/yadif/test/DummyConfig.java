package io.mkrzywanski.yadif.test;

import io.mkrzywanski.yadif.annotation.Instance;

public class DummyConfig {
    @Instance
    public Object object() {
        return new Object();
    }
}
