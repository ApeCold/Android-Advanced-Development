package com.netease.eventbus.library;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.netease.eventbus.annotation.SubscriberInfoIndex;
import com.netease.eventbus.annotation.mode.SubscriberInfo;
import com.netease.eventbus.annotation.mode.SubscriberMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ArrayList的底层是数组，查询和修改直接根据索引可以很快找到对应的元素（替换）
 * 而增加和删除就涉及到数组元素的移动，所以会比较慢
 * <p>
 * CopyOnWriteArrayList实现了List接口（读写分离）
 * Vector是增删改查方法都加了synchronized，保证同步，但是每个方法执行的时候都要去获得锁，性能就会大大下降
 * 而CopyOnWriteArrayList 只是在增删改上加锁，但是读不加锁，在读方面的性能就好于Vector
 * <p>
 * CopyOnWriteArrayList支持读多写少的并发情况
 */
public class EventBus {

    // volatile修饰的变量不允许线程内部缓存和重排序,即直接修改内存
    private static volatile EventBus defaultInstance;
    // 索引接口
    private SubscriberInfoIndex subscriberInfoIndexes;
    // 订阅者类型集合，比如：订阅者MainActivity订阅了哪些EventBean，或者解除订阅的缓存。
    // key：订阅者MainActivity.class，value：EventBean集合
    private Map<Object, List<Class<?>>> typesBySubscriber;
    // 方法缓存：key：订阅者MainActivity.class，value：订阅方法集合
    private static final Map<Class<?>, List<SubscriberMethod>> METHOD_CACHE = new ConcurrentHashMap<>();
    // EventBean缓存，key：UserInfo.class，value：订阅者（可以是多个Activity）中所有订阅的方法集合
    private Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType;
    // 粘性事件缓存，key：UserInfo.class，value：UserInfo
    private final Map<Class<?>, Object> stickyEvents;
    // 发送（子线程） - 订阅（主线程）
    private Handler handler;
    // 发送（主线程） - 订阅（子线程）
    private ExecutorService executorService;

    private EventBus() {
        // 初始化缓存集合
        typesBySubscriber = new HashMap<>();
        subscriptionsByEventType = new HashMap<>();
        stickyEvents = new HashMap<>();
        // Handler高级用法：将handler放在主线程使用
        handler = new Handler(Looper.getMainLooper());
        // 创建一个子线程（缓存线程池）
        executorService = Executors.newCachedThreadPool();
    }

    // 单例，全局唯一，参考EventBus.java 80行
    public static EventBus getDefault() {
        if (defaultInstance == null) {
            synchronized (EventBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new EventBus();
                }
            }
        }
        return defaultInstance;
    }

    // 添加索引（简化），接口 = 接口实现类，参考EventBusBuilder.java 136行
    public void addIndex(SubscriberInfoIndex index) {
        subscriberInfoIndexes = index;
    }

    // 注册 / 订阅事件，参考EventBus.java 138行
    public void register(Object subscriber) {
        // 获取MainActivity.class
        Class<?> subscriberClass = subscriber.getClass();
        // 寻找（MainActivity.class）订阅方法集合
        List<SubscriberMethod> subscriberMethods = findSubscriberMethods(subscriberClass);
        synchronized (this) { // 同步锁，并发少可考虑删除（参考源码）
            for (SubscriberMethod subscriberMethod : subscriberMethods) {
                // 遍历后，开始订阅
                subscribe(subscriber, subscriberMethod);
            }
        }
    }

    // 寻找（MainActivity.class）订阅方法集合，参考SubscriberMethodFinder.java 55行
    private List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
        // 从方法缓存中读取
        List<SubscriberMethod> subscriberMethods = METHOD_CACHE.get(subscriberClass);
        // 找到了缓存，直接返回
        if (subscriberMethods != null) {
            return subscriberMethods;
        }
        // 找不到，从APT生成的类文件中寻找
        subscriberMethods = findUsingInfo(subscriberClass);
        if (subscriberMethods != null) {
            // 存入缓存
            METHOD_CACHE.put(subscriberClass, subscriberMethods);
        }
        return subscriberMethods;
    }

    // 遍历中……并开始订阅，参考EventBus.java 149行
    private void subscribe(Object subscriber, SubscriberMethod subscriberMethod) {
        // 获取订阅方法参数类型，如：UserInfo.class
        Class<?> eventType = subscriberMethod.getEventType();
        // 临时对象存储
        Subscription subscription = new Subscription(subscriber, subscriberMethod);
        // 读取EventBean缓存
        CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEventType.get(eventType);
        if (subscriptions == null) {
            // 初始化集合
            subscriptions = new CopyOnWriteArrayList<>();
            // 存入缓存
            subscriptionsByEventType.put(eventType, subscriptions);
        } else {
            if (subscriptions.contains(subscription)) {
                Log.e("netease >>> ", subscriber.getClass() + "重复注册粘性事件！");
                // 执行多次粘性事件，但不添加到集合，避免订阅方法多次执行
                sticky(subscriberMethod, eventType, subscription);
                return;
            }
        }

        // 订阅方法优先级处理。第一次进来肯定是0，参考EventBus.java 163行
        int size = subscriptions.size();
        // 这里的i <= size，否则进不了下面条件
        for (int i = 0; i <= size; i++) {
            // 如果满足任一条件则进入循环（第1次 i = size = 0）
            // 第2次，size不为0，新加入的订阅方法匹配集合中所有订阅方法的优先级
            if (i == size || subscriberMethod.getPriority() > subscriptions.get(i).subscriberMethod.getPriority()) {
                // 如果新加入的订阅方法优先级大于集合中某订阅方法优先级，则插队到它之前一位
                if (!subscriptions.contains(subscription)) subscriptions.add(i, subscription);
                // 优化：插队成功就跳出（找到了加入集合点）
                break;
            }
        }

        // 订阅者类型集合，比如：订阅者MainActivity订阅了哪些EventBean，或者解除订阅的缓存
        List<Class<?>> subscribedEvents = typesBySubscriber.get(subscriber);
        if (subscribedEvents == null) {
            subscribedEvents = new ArrayList<>();
            // 存入缓存
            typesBySubscriber.put(subscriber, subscribedEvents);
        }
        // 注意：subscribe()方法在遍历过程中，所以一直在添加
        subscribedEvents.add(eventType);

        sticky(subscriberMethod, eventType, subscription);
    }

    // 抽取原因：可执行多次粘性事件，而不会出现闪退，参考EventBus.java 158行
    private void sticky(SubscriberMethod subscriberMethod, Class<?> eventType, Subscription subscription) {
        // 粘性事件触发：注册事件就激活方法，因为整个源码只有此处遍历了。
        // 最佳切入点原因：1，粘性事件的订阅方法加入了缓存。2，注册时只有粘性事件直接激活方法（隔离非粘性事件）
        // 新增开关方法弊端：粘性事件未在缓存中，无法触发订阅方法。且有可能多次执行post()方法
        if (subscriberMethod.isSticky()) { // 参考EventBus.java 178行
            // 源码中做了继承关系的处理，也说明了迭代效率和更改数据结构方便查找，这里就省略了（真实项目极少）
            Object stickyEvent = stickyEvents.get(eventType);
            // 发送事件 到 订阅者的所有订阅方法，并激活方法
            if (stickyEvent != null) postToSubscription(subscription, stickyEvent);
        }
    }

    // 从APT生成的类文件中寻找订阅方法集合，参考SubscriberMethodFinder.java 64行
    private List<SubscriberMethod> findUsingInfo(Class<?> subscriberClass) {
        // app在运行时寻找索引，报错了则说明没有初始化索引方法
        if (subscriberInfoIndexes == null) {
            throw new RuntimeException("未添加索引方法：addIndex()");
        }
        // 接口持有实现类的引用
        SubscriberInfo info = subscriberInfoIndexes.getSubscriberInfo(subscriberClass);
        // 数组转List集合，参考EventBus生成的APT类文件
        if (info != null) return Arrays.asList(info.getSubscriberMethods());
        return null;
    }

    // 是否已经注册 / 订阅，参考EventBus.java 217行
    public synchronized boolean isRegistered(Object subscriber) {
        return typesBySubscriber.containsKey(subscriber);
    }

    // 解除某订阅者关系，参考EventBus.java 239行
    public synchronized void unregister(Object subscriber) {
        // 从缓存中移除
        List<Class<?>> subscribedTypes = typesBySubscriber.get(subscriber);
        if (subscribedTypes != null) {
            // 移除前清空集合
            subscribedTypes.clear();
            typesBySubscriber.remove(subscriber);
        }
    }

    // 发送粘性事件，最终还是调用了post方法，参考EventBus.java 301行
    public void postSticky(Object event) {
        // 同步锁保证并发安全（小项目可忽略此处）
        synchronized (stickyEvents) {
            // 加入粘性事件缓存集合
            stickyEvents.put(event.getClass(), event);
        }
        // 巨坑！！！源码这么写我也不知道什么意图。恶心的后果：只要参数匹配，粘性/非粘性订阅方法全部执行
        // post(event);
    }

    // 获取指定类型的粘性事件，参考EventBus.java 314行
    public <T> T getStickyEvent(Class<T> eventType) {
        // 同步锁保证并发安全（小项目可忽略此处）
        synchronized (stickyEvents) {
            // cast方法做转换类型时安全措施（简化stickyEvents.get(eventType)）
            return eventType.cast(stickyEvents.get(eventType));
        }
    }

    // 移除指定类型的粘性事件（此处返回值看自己需求，可为boolean），参考EventBus.java 325行
    public <T> T removeStickyEvent(Class<T> eventType) {
        // 同步锁保证并发安全（小项目可忽略此处）
        synchronized (stickyEvents) {
            return eventType.cast(stickyEvents.remove(eventType));
        }
    }

    // 移除所有粘性事件，参考EventBus.java 352行
    public void removeAllStickyEvents() {
        // 同步锁保证并发安全（小项目可忽略此处）
        synchronized (stickyEvents) {
            // 清理集合
            stickyEvents.clear();
        }
    }

    // 发送消息 / 事件
    public void post(Object event) {
        // 此处两个参数，简化了源码，参考EventBus.java 252 - 265 - 384 - 400行
        postSingleEventForEventType(event, event.getClass());
    }

    // 为EventBean事件类型发布单个事件（遍历），EventBus核心：参数类型必须一致！！！
    private void postSingleEventForEventType(Object event, Class<?> eventClass) {
        // 从EventBean缓存中，获取所有订阅者和订阅方法
        CopyOnWriteArrayList<Subscription> subscriptions;
        synchronized (this) {
            // 同步锁，保证并发安全
            subscriptions = subscriptionsByEventType.get(eventClass);
        }
        // 判空，健壮性代码
        if (subscriptions != null && !subscriptions.isEmpty()) {
            for (Subscription subscription : subscriptions) {
                // 遍历，寻找发送方指定的EventBean，匹配的订阅方法的EventBean
                postToSubscription(subscription, event);
            }
        }
    }

    // 发送事件 到 订阅者的所有订阅方法（遍历中……），参考参考EventBus.java 427行
    private void postToSubscription(final Subscription subscription, final Object event) {
        // 匹配订阅方的线程模式
        switch (subscription.subscriberMethod.getThreadMode()) {
            case POSTING: // 订阅、发布在同一线程
                invokeSubscriber(subscription, event);
                break;
            case MAIN:
                // 订阅方是主线程，则主 - 主
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    invokeSubscriber(subscription, event);
                } else {
                    // 订阅方是子线程，则子 - 主
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            invokeSubscriber(subscription, event);
                        }
                    });
                }
                break;
            case ASYNC:
                // 订阅方是主线程，则主 - 子
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    // 主线程 - 子线程，创建一个子线程（缓存线程池）
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            invokeSubscriber(subscription, event);
                        }
                    });
                } else {
                    // 订阅方是子线程，则子 - 子
                    invokeSubscriber(subscription, event);
                }
                break;
            default:
                throw new IllegalStateException("未知线程模式！" + subscription.subscriberMethod.getThreadMode());
        }
    }

    // 执行订阅方法（被注解方法自动执行）参考EventBus.java 505行
    private void invokeSubscriber(Subscription subscription, Object event) {
        try {
            // 无论3.0之前还是之后。最后一步终究逃不过反射！
            subscription.subscriberMethod.getMethod().invoke(subscription.subscriber, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 清理静态缓存（视项目规模调用）
    public static void clearCaches() {
        METHOD_CACHE.clear();
    }
}
