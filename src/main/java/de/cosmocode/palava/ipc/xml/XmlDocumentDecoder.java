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

import java.io.InputStream;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.parsers.DocumentBuilder;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.w3c.dom.Document;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.ipc.netty.ChannelBuffering;

/**
 * Decodes {@link ChannelBuffer}s into {@link Document}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
@Sharable
@ThreadSafe
public final class XmlDocumentDecoder extends OneToOneDecoder {

    private final DocumentBuilder builder;
    
    @Inject
    public XmlDocumentDecoder(DocumentBuilder builder) {
        this.builder = Preconditions.checkNotNull(builder, "Builder");
    }
    
    @Override
    protected Object decode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (message instanceof ChannelBuffer) {
            final ChannelBuffer buffer = ChannelBuffer.class.cast(message);
            final InputStream stream = ChannelBuffering.asInputStream(buffer);
            return builder.parse(stream);
        } else {
            return message;
        }
    }

}
