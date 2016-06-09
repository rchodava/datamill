package foundation.stack.datamill.http.impl;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import foundation.stack.datamill.http.Entity;
import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.http.ResponseBuilder;
import foundation.stack.datamill.http.Status;
import foundation.stack.datamill.json.Json;
import foundation.stack.datamill.values.StringValue;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.ReplaySubject;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseBuilderImpl implements ResponseBuilder {
    private final ExecutorService streamingEntityThreadPool;
    private final Multimap<String, String> headers = LinkedListMultimap.create();
    private Entity entity;

    public ResponseBuilderImpl(ExecutorService threadPool) {
        this.streamingEntityThreadPool = threadPool;
    }

    // Test hook
    ResponseBuilderImpl() {
        this.streamingEntityThreadPool = null;
    }

    @Override
    public Response badRequest() {
        return new ResponseImpl(Status.BAD_REQUEST, headers, entity);
    }

    @Override
    public Response badRequest(String content) {
        return new ResponseImpl(Status.BAD_REQUEST, headers, new ValueEntity(new StringValue(content)));
    }

    @Override
    public <T> ResponseBuilder header(String name, T value) {
        headers.put(name, value.toString());
        return this;
    }

    @Override
    public Response internalServerError() {
        return new ResponseImpl(Status.INTERNAL_SERVER_ERROR, headers, entity);
    }

    @Override
    public Response internalServerError(String content) {
        return new ResponseImpl(Status.INTERNAL_SERVER_ERROR, headers, new ValueEntity(new StringValue(content)));
    }

    @Override
    public Response noContent() {
        return new ResponseImpl(Status.NO_CONTENT, headers);
    }

    @Override
    public Response notFound() {
        return new ResponseImpl(Status.NOT_FOUND, headers, entity);
    }

    @Override
    public Response ok() {
        return new ResponseImpl(Status.OK, headers, entity);
    }

    @Override
    public Response ok(String content) {
        return new ResponseImpl(Status.OK, headers, new ValueEntity(new StringValue(content)));
    }

    @Override
    public Response ok(byte[] content) {
        return new ResponseImpl(Status.OK, headers, new BytesEntity(content));
    }

    @Override
    public ResponseBuilder streamingEntity(Func1<Observer<byte[]>, Observable<byte[]>> entityStreamer) {
        ReplaySubject<byte[]> entitySubject = ReplaySubject.create();

        Subscription[] entityStreamerSubscription = new Subscription[1];

        Observable<byte[]> disposingSubject = Observable.using(() -> null,
                __ -> entitySubject,
                __ -> {
                    if (entityStreamerSubscription[0] != null && !entityStreamerSubscription[0].isUnsubscribed()) {
                        entityStreamerSubscription[0].unsubscribe();
                    }
                });

        streamingEntityThreadPool.execute(() -> {
            entityStreamerSubscription[0] = entityStreamer.call(entitySubject)
                    .doOnNext(bytes -> entitySubject.onNext(bytes))
                    .doOnCompleted(() -> entitySubject.onCompleted())
                    .subscribe();
        });

        this.entity = new StreamedChunksEntity(disposingSubject, Charset.defaultCharset());
        return this;
    }

    @Override
    public ResponseBuilder streamingJson(Func1<Observer<Json>, Observable<Json>> jsonStreamer) {
        return streamingEntity(entity -> Observable.concat(
                Observable.just("[".getBytes()),
                Observable.defer(() ->
                        jsonStreamer.call(new DelegatingObserver<Json, byte[]>(entity) {
                            @Override
                            protected byte[] map(Json source) {
                                return (source.toString() + ",").getBytes();
                            }
                        }).map(json -> (json.toString() + ",").getBytes())),
                Observable.just("]".getBytes())));
    }

    @Override
    public Response unauthorized() {
        return new ResponseImpl(Status.UNAUTHORIZED, headers, entity);
    }

    @Override
    public Response unauthorized(String content) {
        return new ResponseImpl(Status.UNAUTHORIZED, headers, new ValueEntity(new StringValue(content)));
    }

    @Override
    public Response forbidden() {
        return new ResponseImpl(Status.FORBIDDEN, headers, entity);
    }

    @Override
    public Response forbidden(String content) {
        return new ResponseImpl(Status.FORBIDDEN, headers, new ValueEntity(new StringValue(content)));
    }

    @Override
    public Response conflict(String content) {
        return new ResponseImpl(Status.CONFLICT, headers, new ValueEntity(new StringValue(content)));
    }

    private static abstract class DelegatingObserver<S, T> implements Observer<S> {
        private final Observer<T> target;

        DelegatingObserver(Observer<T> target) {
            this.target = target;
        }

        @Override
        public void onNext(S s) {
            target.onNext(map(s));
        }

        protected abstract T map(S source);

        @Override
        public void onError(Throwable e) {
            target.onError(e);
        }

        @Override
        public void onCompleted() {
            target.onCompleted();
        }
    }
}
