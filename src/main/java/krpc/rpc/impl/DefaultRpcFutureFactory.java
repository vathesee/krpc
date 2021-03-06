package krpc.rpc.impl;

import com.google.protobuf.Message;
import krpc.common.InitClose;
import krpc.common.NamedThreadFactory;
import krpc.rpc.core.RpcFutureFactory;
import krpc.rpc.core.RpcRawMessage;
import krpc.rpc.core.ServiceMetas;
import krpc.trace.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultRpcFutureFactory implements RpcFutureFactory, InitClose {

    static Logger log = LoggerFactory.getLogger(DefaultRpcFutureFactory.class);

    ServiceMetas serviceMetas;

    int notifyThreads = 1;
    int notifyMaxThreads = 0;
    int notifyQueueSize = 1000;
    ThreadPoolExecutor notifyPool;
    NamedThreadFactory threadFactory1 = new NamedThreadFactory("krpc_async_notifier");

    public void init() {

        if (notifyThreads >= 0) {
            if (notifyMaxThreads > notifyThreads)
                notifyPool = new ThreadPoolExecutor(notifyThreads, notifyMaxThreads, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(notifyQueueSize), threadFactory1);
            else
                notifyPool = new ThreadPoolExecutor(notifyThreads, notifyThreads, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(notifyQueueSize), threadFactory1);
            notifyPool.prestartAllCoreThreads();
        }

        log.info("notifyPool inited");
    }

    public void close() {
        if (notifyPool != null)
            notifyPool.shutdown();
    }

    public CompletableFuture<Message> newFuture(int serviceId, int msgId, boolean isAsync, TraceContext traceContext) {
        return new DefaultRpcFuture(this, serviceId, msgId, isAsync, traceContext);
    }

    public CompletableFuture<RpcRawMessage> newRawFuture(int serviceId, int msgId, boolean isAsync, TraceContext traceContext) {
        return new DefaultRawRpcFuture(this, serviceId, msgId, isAsync, traceContext);
    }

    public int getNotifyThreads() {
        return notifyThreads;
    }

    public void setNotifyThreads(int notifyThreads) {
        this.notifyThreads = notifyThreads;
    }

    public int getNotifyQueueSize() {
        return notifyQueueSize;
    }

    public void setNotifyQueueSize(int notifyQueueSize) {
        this.notifyQueueSize = notifyQueueSize;
    }

    public ServiceMetas getServiceMetas() {
        return serviceMetas;
    }

    public void setServiceMetas(ServiceMetas serviceMetas) {
        this.serviceMetas = serviceMetas;
    }

    public int getNotifyMaxThreads() {
        return notifyMaxThreads;
    }

    public void setNotifyMaxThreads(int notifyMaxThreads) {
        this.notifyMaxThreads = notifyMaxThreads;
    }


}
