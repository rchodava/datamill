package org.chodavarapu.datamill.http.impl;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.chodavarapu.datamill.http.Entity;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.ResponseBuilder;
import org.chodavarapu.datamill.http.Status;
import org.chodavarapu.datamill.json.Json;
import org.chodavarapu.datamill.values.StringValue;
import rx.Observer;
import rx.subjects.ReplaySubject;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

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
    public ResponseBuilder streamingEntity(Consumer<Observer<byte[]>> entityStreamer) {
        ReplaySubject<byte[]> entitySubject = ReplaySubject.create();

        streamingEntityThreadPool.execute(() -> entityStreamer.accept(entitySubject));

        this.entity = new StreamedChunksEntity(entitySubject, Charset.defaultCharset());
        return this;
    }

    @Override
    public ResponseBuilder streamingJson(Consumer<Observer<Json>> jsonStreamer) {
        boolean firstJsonObject[] = new boolean[] { true };
        return streamingEntity(byteObserver -> {
            jsonStreamer.accept(new Observer<Json>() {
                {
                    byteObserver.onNext("[".getBytes());
                }

                @Override
                public void onCompleted() {
                    byteObserver.onNext("]".getBytes());
                    byteObserver.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    byteObserver.onError(e);
                }

                @Override
                public void onNext(Json jsonObject) {
                    if (jsonObject != null) {
                        if (!firstJsonObject[0]) {
                            byteObserver.onNext(",".getBytes());
                        } else {
                            firstJsonObject[0] = false;
                        }

                        byteObserver.onNext(jsonObject.toString().getBytes());
                    }
                }
            });
        });
    }

    @Override
    public Response unauthorized() {
        return new ResponseImpl(Status.UNAUTHORIZED, headers, entity);
    }

    @Override
    public Response unauthorized(String content) {
        return new ResponseImpl(Status.UNAUTHORIZED, headers, new ValueEntity(new StringValue(content)));
    }
}
