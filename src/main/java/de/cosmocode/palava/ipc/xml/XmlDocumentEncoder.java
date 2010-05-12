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

import java.io.OutputStream;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.w3c.dom.Document;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;

import de.cosmocode.palava.ipc.netty.ChannelBuffering;

/**
 * Encodes {@link Document}s to {@link ChannelBuffer}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
@Sharable
@ThreadSafe
public final class XmlDocumentEncoder extends OneToOneEncoder {

    private final Provider<Transformer> provider;
    
    @Inject
    public XmlDocumentEncoder(Provider<Transformer> provider) {
        this.provider = Preconditions.checkNotNull(provider, "Provider");
    }
    
    @Override
    protected Object encode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (message instanceof Document) {
            final Document document = Document.class.cast(message);
            final Transformer transformer = provider.get();
            final DOMSource source = new DOMSource(document);
            
            final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(1024);
            final OutputStream stream = ChannelBuffering.asOutputStream(buffer);
            final StreamResult result = new StreamResult(stream);
            transformer.transform(source, result);
            
            return buffer;
        } else {
            return message;
        }
    }

}
