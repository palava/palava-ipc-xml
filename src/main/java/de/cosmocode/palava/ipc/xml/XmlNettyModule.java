/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
     * @return a new {@link ChannelPipeline}
     */
    @Provides
    @Xml
    ChannelPipeline provideChannelPipeline(HttpRequestDecoder requestDecoder, HttpChunkAggregator chunkAggregator,
        HttpResponseEncoder responseEncoder, HttpContentCompressor compressor,
        HttpContentDecoder httpContentDecoder, HttpContentEncoder httpContentEncoder) {
        return Channels.pipeline(
            requestDecoder, chunkAggregator,
            responseEncoder, compressor,
            httpContentDecoder, httpContentEncoder
        );
    }
    
}
