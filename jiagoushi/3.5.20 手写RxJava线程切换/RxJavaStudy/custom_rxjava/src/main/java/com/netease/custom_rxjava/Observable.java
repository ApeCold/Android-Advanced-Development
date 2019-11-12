package com.netease.custom_rxjava;

// todo 被观察者 上游
public class Observable<T> implements ObservableOnSubscribe<T> { // 类声明的泛型T  Int

    ObservableOnSubscribe source;

    private Observable(ObservableOnSubscribe source) {
        this.source = source;
    }

    // 静态方法声明的<T>泛型        ObservableOnSubscribe<T>==静态方法声明的<T>泛型
    // 参数中：ObservableOnSubscribe<? extends T> 和可读可写模式没有任何关系，还是我们之前的那一套思想（上限和下限）
    public static <T> Observable<T> create(ObservableOnSubscribe<? extends T> source) { // int
        return new Observable<T>(source); // 静态方法声明的<T>泛型 int
    }

    // 1 2 3 4 可变参数
    public static <T> Observable<T> just(final T... t) { // just 内部发射了
        // 想办法让 source 是不为null的，  而我们的create操作符是，使用者自己传递进来的
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) { // observableEmitter == Observer
                for (T t1 : t) {

                    // Observer.onNext(1);

                    // 发射用户传递的参数数据 去发射事件
                    observableEmitter.onNext(t1);
                }

                // 调用完毕
                observableEmitter.onComplete(); // 发射完毕事件
            }
        });
    }

    // 单一
    public static <T> Observable<T> just(final T t) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 发射用户传递的参数数据 去发射事件
                observableEmitter.onNext(t);

                // 调用完毕
                observableEmitter.onComplete(); // 发射完毕事件
            }
        });
    }

    // 2个参数
    public static <T> Observable<T> just(final T t, final T t2) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 发射用户传递的参数数据 去发射事件
                observableEmitter.onNext(t);
                observableEmitter.onNext(t2);

                // 调用完毕
                observableEmitter.onComplete(); // 发射完毕事件
            }
        });
    }

    // 3个参数
    public static <T> Observable<T> just(final T t, final T t2, final T t3) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 发射用户传递的参数数据 去发射事件
                observableEmitter.onNext(t);
                observableEmitter.onNext(t2);
                observableEmitter.onNext(t3);

                // 调用完毕
                observableEmitter.onComplete(); // 发射完毕事件
            }
        });
    }

    // 4个参数的
    public static <T> Observable<T> just(final T t, final T t2, final T t3, final T t4) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 发射用户传递的参数数据 去发射事件
                observableEmitter.onNext(t);
                observableEmitter.onNext(t2);
                observableEmitter.onNext(t3);
                observableEmitter.onNext(t4);

                // 调用完毕
                observableEmitter.onComplete(); // 发射完毕事件
            }
        });
    }

    // 给同学们新增加的
    // 5个参数的
    public static <T> Observable<T> just(final T t, final T t2, final T t3, final T t4, final T t5) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 发射用户传递的参数数据 去发射事件
                observableEmitter.onNext(t);
                observableEmitter.onNext(t2);
                observableEmitter.onNext(t3);
                observableEmitter.onNext(t4);
                observableEmitter.onNext(t5);

                // 调用完毕
                observableEmitter.onComplete(); // 发射完毕事件
            }
        });
    }

    // 给同学们新增加的
    // 6个参数的
    public static <T> Observable<T> just(final T t, final T t2, final T t3, final T t4, final T t5, final T t6) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 发射用户传递的参数数据 去发射事件
                observableEmitter.onNext(t);
                observableEmitter.onNext(t2);
                observableEmitter.onNext(t3);
                observableEmitter.onNext(t4);
                observableEmitter.onNext(t5);
                observableEmitter.onNext(t6);

                // 调用完毕
                observableEmitter.onComplete(); // 发射完毕事件
            }
        });
    }

    // 给同学们新增加的
    // 7个参数的
    public static <T> Observable<T> just(final T t, final T t2, final T t3, final T t4, final T t5, final T t6, final T t7) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 发射用户传递的参数数据 去发射事件
                observableEmitter.onNext(t);
                observableEmitter.onNext(t2);
                observableEmitter.onNext(t3);
                observableEmitter.onNext(t4);
                observableEmitter.onNext(t5);
                observableEmitter.onNext(t6);
                observableEmitter.onNext(t7);

                // 调用完毕
                observableEmitter.onComplete(); // 发射完毕事件
            }
        });
    }

    // 给同学们新增加的
    // 8个参数的
    public static <T> Observable<T> just(final T t, final T t2, final T t3, final T t4, final T t5, final T t6, final T t7, final T t8) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 发射用户传递的参数数据 去发射事件
                observableEmitter.onNext(t);
                observableEmitter.onNext(t2);
                observableEmitter.onNext(t3);
                observableEmitter.onNext(t4);
                observableEmitter.onNext(t5);
                observableEmitter.onNext(t6);
                observableEmitter.onNext(t7);
                observableEmitter.onNext(t8);

                // 调用完毕
                observableEmitter.onComplete(); // 发射完毕事件
            }
        });
    }

    // 给同学们新增加的
    // 9个参数的
    public static <T> Observable<T> just(final T t, final T t2, final T t3, final T t4, final T t5, final T t6, final T t7, final T t8, final T t9) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 发射用户传递的参数数据 去发射事件
                observableEmitter.onNext(t);
                observableEmitter.onNext(t2);
                observableEmitter.onNext(t3);
                observableEmitter.onNext(t4);
                observableEmitter.onNext(t5);
                observableEmitter.onNext(t6);
                observableEmitter.onNext(t7);
                observableEmitter.onNext(t8);
                observableEmitter.onNext(t9);

                // 调用完毕
                observableEmitter.onComplete(); // 发射完毕事件
            }
        });
    }

    // 给同学们新增加的
    // 10个参数的
    public static <T> Observable<T> just(final T t, final T t2, final T t3, final T t4, final T t5, final T t6, final T t7, final T t8, final T t9, final T t10) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 发射用户传递的参数数据 去发射事件
                observableEmitter.onNext(t);
                observableEmitter.onNext(t2);
                observableEmitter.onNext(t3);
                observableEmitter.onNext(t4);
                observableEmitter.onNext(t5);
                observableEmitter.onNext(t6);
                observableEmitter.onNext(t7);
                observableEmitter.onNext(t8);
                observableEmitter.onNext(t9);
                observableEmitter.onNext(t10);

                // 调用完毕
                observableEmitter.onComplete(); // 发射完毕事件
            }
        });
    }

    // 给同学们新增加的
    // fromArray
    public static <T> Observable<T> fromArray(final T[] ts) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 根据使用者 传递的参数 分发事件下去
                for (T t : ts) {
                    observableEmitter.onNext(t);
                }
                // 分发完毕的事件
                observableEmitter.onComplete();
            }
        });
    }

    // 给同学们新增加的
    // fromArray
    public static <T> Observable<T> fromArray(final T[] ts, final T[] ts2) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 根据使用者 传递的参数 分发事件下去
                for (T t : ts) {
                    observableEmitter.onNext(t);
                }
                // 根据使用者 传递的参数 分发事件下去
                for (T t : ts2) {
                    observableEmitter.onNext(t);
                }
                // 分发完毕的事件
                observableEmitter.onComplete();
            }
        });
    }

    // 给同学们新增加的
    // fromArray
    public static <T> Observable<T> fromArray(final T[] ts, final T[] ts2, final T[] ts3) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 根据使用者 传递的参数 分发事件下去
                for (T t : ts) {
                    observableEmitter.onNext(t);
                }
                // 根据使用者 传递的参数 分发事件下去
                for (T t : ts2) {
                    observableEmitter.onNext(t);
                }
                // 根据使用者 传递的参数 分发事件下去
                for (T t : ts3) {
                    observableEmitter.onNext(t);
                }
                // 分发完毕的事件
                observableEmitter.onComplete();
            }
        });
    }

    // 给同学们新增加的
    // fromArray
    public static <T> Observable<T> fromArray(final T[] ts, final T[] ts2, final T[] ts3, final T[] ts4) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 根据使用者 传递的参数 分发事件下去
                for (T t : ts) {
                    observableEmitter.onNext(t);
                }
                // 根据使用者 传递的参数 分发事件下去
                for (T t : ts2) {
                    observableEmitter.onNext(t);
                }
                // 根据使用者 传递的参数 分发事件下去
                for (T t : ts3) {
                    observableEmitter.onNext(t);
                }
                // 根据使用者 传递的参数 分发事件下去
                for (T t : ts4) {
                    observableEmitter.onNext(t);
                }
                // 分发完毕的事件
                observableEmitter.onComplete();
            }
        });
    }

    // 给同学们新增加的
    // fromArray
    public static <T> Observable<T> fromArray(final T[]... ts) {
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                // 根据使用者 传递的参数 分发事件下去
                for (T[] t : ts) {
                    for (T t1 : t) {
                        observableEmitter.onNext(t1);
                    }
                }
                // 分发完毕的事件
                observableEmitter.onComplete();
            }
        });
    }

    // new Observable<T>(source).subscribe(Observer<Int>)
    // 参数中：Observer<? extends T> 和可读可写模式没有任何关系，还是我们之前的那一套思想（上限和下限）
    /*public void subscribe(Observer<? extends T> observer) {

        // todo 2
        observer.onSubscribe();

        // todo 3
        source.subscribe(observer); // 这个source就有了  观察者 Observer

    }*/

    @Override
    public void subscribe(Observer<? super T> observableEmitter) { // == Observer
        // todo 2
        observableEmitter.onSubscribe();

        // todo 3
        source.subscribe(observableEmitter); // 这个source就有了  观察者 Observer
    }

    /**
     * map变换操作符
     *
     * T == 上一层传递过来的类型  Integer  变换前的类型
     * R == 给一层的类型         String   变换后的类型
     *
     */
    public <R> Observable<R> map(Function<? super T, ? extends R> function) { // ? super T 可写模式

        ObservableMap<T, R> observableMap = new ObservableMap(source, function); // source 上一层的能力

        return new Observable<R>(observableMap); // source  该怎么来？     observableMap是source的实现类
    }

    // todo 给所有上游分配异步线程
    public Observable<T> observables_On() {
        // 实例化 处理上游的线程操作符
        return create(new ObservableOnIO(source));
    }

    // todo 给下游分配Android主线程
    public Observable<T> observers_AndroidMain_On() {
        // 实例化 处理下游的线程操作符
        ObserverAndroidMain_On<T> observerAndroidMain_on = new ObserverAndroidMain_On(source);
        return create(observerAndroidMain_on);
    }
}
