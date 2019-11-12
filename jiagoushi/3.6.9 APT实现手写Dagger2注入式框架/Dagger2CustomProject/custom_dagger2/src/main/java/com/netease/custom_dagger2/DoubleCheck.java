package com.netease.custom_dagger2;

public final class DoubleCheck<T> implements Provider<T> {

    private static final Object UNINITIALIZED = new Object();

    private volatile Provider<T> provider; // 最上层实例化对象的 接口
    private volatile Object instance = UNINITIALIZED;

    private DoubleCheck(Provider<T> provider) {
        this.provider = provider;
    }

    @Override
    public T get() {
        Object result = instance; // instance = new Student();
        if (result == UNINITIALIZED) {
            synchronized (this) {
                result = instance;
                if (result == UNINITIALIZED) {
                    instance = result = provider.get(); // new Student();
                    provider = null;
                }
            }
        }
        return (T) result;
    }

    public static <T> Provider<T> provider(Provider<T> delegate) {
        Preconditions.checkNotNull(delegate);
        if (delegate instanceof DoubleCheck) {
            return delegate;
        }
        return new DoubleCheck<T>(delegate);
    }

}
