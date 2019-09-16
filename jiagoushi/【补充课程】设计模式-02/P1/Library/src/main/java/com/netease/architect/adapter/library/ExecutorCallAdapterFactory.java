package com.netease.architect.adapter.library;

public class ExecutorCallAdapterFactory extends CallAdapter.Factory {

    @Override
    public CallAdapter<?, ?> get() {
        return new CallAdapter<Object, Call<?>>() {
            @Override
            public Call<?> adapt(Call<Object> call) {

                System.out.println("Default >>> ");
                return new ExecutorCallbackCall<>();
            }
        };
    }

    static final class ExecutorCallbackCall<T> implements Call<T> {

        @Override
        public void enqueue() {
            // 不关心请求过程，只关心适配器模式
        }
    }
}
