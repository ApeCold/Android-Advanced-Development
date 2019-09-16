package com.netease.architect.adapter.library;

public class ServiceMethod<R, T> {

    private CallAdapter<R, T> callAdapter;

    private ServiceMethod(Builder<R, T> builder) {
        callAdapter = builder.callAdapter;
    }

    T adapt(Call<R> call) {
        return callAdapter.adapt(call);
    }

    static final class Builder<R, T> {

        Retrofit retrofit;
        CallAdapter<R, T> callAdapter;

        Builder (Retrofit retrofit) {
            this.retrofit = retrofit;
        }

        public ServiceMethod build() {
            callAdapter = createCallAdapter();
            return new ServiceMethod<>(this);
        }

        @SuppressWarnings("unchecked")
        private CallAdapter<R, T> createCallAdapter() {
            return (CallAdapter<R, T>) retrofit.callAdapter();
        }
    }
}
