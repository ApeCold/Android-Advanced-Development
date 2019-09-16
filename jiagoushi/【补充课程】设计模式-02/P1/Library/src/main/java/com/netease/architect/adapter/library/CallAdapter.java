package com.netease.architect.adapter.library;

public interface CallAdapter<R, T> {

    T adapt(Call<R> call);

    abstract class Factory {

        public abstract CallAdapter<?, ?> get();
    }
}
