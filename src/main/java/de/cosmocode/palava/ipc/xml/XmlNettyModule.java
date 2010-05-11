package de.cosmocode.palava.ipc.xml;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

/**
 * Binds xml channel handlers/decoders/encoders.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class XmlNettyModule implements Module {

    @Override
    public void configure(Binder binder) {
        // stateful frame decoders
        binder.bind(HttpRequestDecoder.class).in(Scopes.NO_SCOPE);
        binder.bind(HttpContentCompressor.class).in(Scopes.NO_SCOPE);
        
        // stateless decoders/encoders
        binder.bind(HttpResponseEncoder.class).in(Singleton.class);
        
        // custom decoders/encoders
        binder.bind(HttpContentDecoder.class).in(Singleton.class);
        binder.bind(HttpContentEncoder.class).in(Singleton.class);
        binder.bind(XmlDocumentDecoder.class).in(Singleton.class);
        binder.bind(XmlDocumentEncoder.class).in(Singleton.class);
    }

    /**
     * Provides a stateful http chunk aggregator.
     * 
     * @since 1.0
     * @return a new {@link HttpChunkAggregator}
     */
    @Provides
    HttpChunkAggregator provideHttpChunkAggregator() {
        return new HttpChunkAggregator(1048576);
    }
    
    /**
     * Provides an xml-http channel pipeline which can be used as a base pipeline
     * for xml over http protocols.
     * 
     * @since 1.0
     * @param requestDecoder the http request decoder
     * @param chunkAggregator the http chunk aggregator
     * @param responseEncoder the http response encoder
     * @param compressor the http compressor
     * @param httpContentDecoder the http content decoder
     * @param httpContentEncoder the http content encoder
     * @param documentDecoder the xml document decoder
     * @param documentEncoder the xml document encoder
     * @return a new {@link ChannelPipeline}
     */
    @Provides
    @Xml
    ChannelPipeline provideChannelPipeline(HttpRequestDecoder requestDecoder, HttpChunkAggregator chunkAggregator,
        HttpResponseEncoder responseEncoder, HttpContentCompressor compressor,
        HttpContentDecoder httpContentDecoder, HttpContentEncoder httpContentEncoder,
        XmlDocumentDecoder documentDecoder, XmlDocumentEncoder documentEncoder) {
        return Channels.pipeline(
            requestDecoder, chunkAggregator,
            responseEncoder, compressor,
            httpContentDecoder, httpContentEncoder,
            documentDecoder, documentEncoder
        );
    }
    
}
