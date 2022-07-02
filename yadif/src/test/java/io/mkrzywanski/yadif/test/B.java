package io.mkrzywanski.yadif.test;

public class B {
    private final A a;
    private final C c;

    public B(final A a, final C c) {
        this.a = a;
        this.c = c;
    }

    public A getA() {
        return a;
    }

    public C getC() {
        return c;
    }
}
