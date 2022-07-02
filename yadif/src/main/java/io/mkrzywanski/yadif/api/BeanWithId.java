package io.mkrzywanski.yadif.api;

import io.mkrzywanski.yadif.BeanId;

public record BeanWithId(BeanId beanId, Object bean) {
}
