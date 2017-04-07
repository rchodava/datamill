package foundation.stack.datamill.http.impl;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import foundation.stack.datamill.http.Body;
import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.http.ResponseBuilder;
import foundation.stack.datamill.http.Status;
import foundation.stack.datamill.json.Json;
import foundation.stack.datamill.values.StringValue;
import rx.Emitter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseBuilderImpl implements ResponseBuilder {
    private final Multimap<String, String> headers = LinkedListMultimap.create();
    private Body body;

    @Override
    public Response badRequest() {
        return new ResponseImpl(Status.BAD_REQUEST, headers, body);
    }

    @Override
    public Response badRequest(String content) {
        return new ResponseImpl(Status.BAD_REQUEST, headers, new ValueBody(new StringValue(content)));
    }

    @Override
    public <T> ResponseBuilder header(String name, T value) {
        headers.put(name, value.toString());
        return this;
    }

    @Override
    public Response internalServerError() {
        return new ResponseImpl(Status.INTERNAL_SERVER_ERROR, headers, body);
    }

    @Override
    public Response internalServerError(String content) {
        return new ResponseImpl(Status.INTERNAL_SERVER_ERROR, headers, new ValueBody(new StringValue(content)));
    }

    @Override
    public Response noContent() {
        return new ResponseImpl(Status.NO_CONTENT, headers);
    }

    @Override
    public Response notFound() {
        return new ResponseImpl(Status.NOT_FOUND, headers, body);
    }

    @Override
    public Response notFound(String content) {
        return new ResponseImpl(Status.NOT_FOUND, headers, new ValueBody(new StringValue(content)));
    }

    @Override
    public Response ok() {
        return new ResponseImpl(Status.OK, headers, body);
    }

    @Override
    public Response ok(String content) {
        return new ResponseImpl(Status.OK, headers, new ValueBody(new StringValue(content)));
    }

    @Override
    public Response ok(byte[] content) {
        return new ResponseImpl(Status.OK, headers, new BytesBody(content));
    }

    @Override
    public Response status(Status status) {
        return new ResponseImpl(status, headers, body);
    }

    @Override
    public Response status(Status status, String content) {
        return new ResponseImpl(status, headers, new ValueBody(new StringValue(content)));
    }

    @Override
    public Response status(Status status, byte[] content) {
        return new ResponseImpl(status, headers, new BytesBody(content));
    }

    @Override
    public ResponseBuilder streamingBodyAsBufferChunks(Func1<Observer<ByteBuffer>, Observable<ByteBuffer>> bodyStreamer) {
        Observable<ByteBuffer> chunkStream = Observable.fromEmitter(emitter -> {
            Subscription subscription = bodyStreamer.call(new PassthroughObserver<>(emitter))
                        .doOnNext(buffer -> emitter.onNext(buffer))
                        .doOnCompleted(() -> emitter.onCompleted())
                        .doOnError(e -> emitter.onError(e))
                        .subscribeOn(Schedulers.io())
                        .subscribe();

            emitter.setCancellation(subscription::unsubscribe);
        }, Emitter.BackpressureMode.BUFFER);

        this.body = new StreamedChunksBody(chunkStream, Charset.defaultCharset());
        return this;
    }

    @Override
    public ResponseBuilder streamingBody(Func1<Observer<byte[]>, Observable<byte[]>> bodyStreamer) {
        return streamingBodyAsBufferChunks(body -> bodyStreamer.call(
                new DelegatingObserver<byte[], ByteBuffer>(body) {
                    @Override
                    protected ByteBuffer map(byte[] bytes) {
                        return ByteBuffer.wrap(bytes);
                    }
                }).map(bytes -> ByteBuffer.wrap(bytes)));
    }

    @Override
    public ResponseBuilder streamingJson(Func1<Observer<Json>, Observable<Json>> jsonStreamer) {
        return streamingBody(body ->
                Observable.fromEmitter(emitter -> {
                    JsonStreamer streamer = new JsonStreamer(emitter);
                    Subscription subscription = jsonStreamer.call(streamer)
                            .doOnNext(json -> streamer.onNext(json))
                            .doOnCompleted(() -> {
                                emitter.onNext("]".getBytes());
                                emitter.onCompleted();
                            })
                            .doOnError(e -> streamer.onError(e))
                            .subscribeOn(Schedulers.io())
                            .subscribe();

                    emitter.setCancellation(subscription::unsubscribe);
                }, Emitter.BackpressureMode.BUFFER));
    }

    @Override
    public Response unauthorized() {
        return new ResponseImpl(Status.UNAUTHORIZED, headers, body);
    }

    @Override
    public Response unauthorized(String content) {
        return new ResponseImpl(Status.UNAUTHORIZED, headers, new ValueBody(new StringValue(content)));
    }

    @Override
    public Response forbidden() {
        return new ResponseImpl(Status.FORBIDDEN, headers, body);
    }

    @Override
    public Response forbidden(String content) {
        return new ResponseImpl(Status.FORBIDDEN, headers, new ValueBody(new StringValue(content)));
    }

    @Override
    public Response conflict(String content) {
        return new ResponseImpl(Status.CONFLICT, headers, new ValueBody(new StringValue(content)));
    }

    private static class PassthroughObserver<T> implements Observer<T> {
        private final Observer<T> target;

        PassthroughObserver(Observer<T> target) {
            this.target = target;
        }

        @Override
        public void onNext(T t) {
            target.onNext(t);
        }

        @Override
        public void onError(Throwable e) {
            target.onError(e);
        }

        @Override
        public void onCompleted() {
        }
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

    private static class JsonStreamer implements Observer<Json> {
        private final Observer<byte[]> body;

        private boolean first = true;

        JsonStreamer(Observer<byte[]> body) {
            this.body = body;
        }

        @Override
        public void onNext(Json json) {
            String out = first ? "[" : ",";
            first = false;

            body.onNext((out + json.toString()).getBytes());
        }

        @Override
        public void onError(Throwable e) {
            body.onError(e);
        }

        @Override
        public void onCompleted() {
        }
    }
}
