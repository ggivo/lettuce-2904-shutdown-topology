/*
 * Copyright 2011-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lettuce.core.output;

import java.nio.ByteBuffer;

import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.codec.RedisCodec;

/**
 * {@link io.lettuce.core.KeyScanCursor} for scan cursor output.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 * @author Mark Paluch
 */
public class KeyScanOutput<K, V> extends ScanOutput<K, V, KeyScanCursor<K>> {

    public KeyScanOutput(RedisCodec<K, V> codec) {
        super(codec, new KeyScanCursor<K>());
    }

    @Override
    protected void setOutput(ByteBuffer bytes) {
        output.getKeys().add(bytes == null ? null : codec.decodeKey(bytes));
    }
}
