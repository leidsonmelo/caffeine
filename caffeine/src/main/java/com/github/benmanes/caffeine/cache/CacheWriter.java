/*
 * Copyright 2015 Ben Manes. All Rights Reserved.
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
package com.github.benmanes.caffeine.cache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Communicates the write or deletion of a value, based on a key, to an external resource.
 * <p>
 * The operations may be performed in either a <tt>write-through</tt> or <tt>write-behind</tt>
 * style, where the difference is whether the operation completes synchronously or asynchronously
 * with the cache.
 * <p>
 * When combined with a {@link CacheLoader}, the writer simplifies the implementation of a tiered
 * cache. The loader queries the secondary cache and computes the value if not found. The layered
 * cache may be inclusive or modeled as a victim cache by writing entries when the
 * {@link RemovalCause} indicates an eviction.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 * @param <K> the most general type of keys this writer can write; for example {@code Object} if any
 *        key is acceptable
 * @param <V> the most general type of values this write can write; for example {@code Object} if
 *        any value is acceptable
 */
@ThreadSafe
public interface CacheWriter<K, V> {

  /***
   * Writes the value corresponding to the {@code key} to the external resource. The cache will
   * communicate a write when an entry in the cache is inserted or updated explicitly. The implicit
   * creation of an entry due to being loaded when absent is not communicated.
   *
   * @param key the non-null key whose value should be written
   * @param value the value associated with {@code key} that should be written
   * @throws RuntimeException or Error, in which case the mapping is unchanged
   */
  void write(@Nonnull K key, @Nonnull V value);

  /**
   * Deletes the value corresponding to the {@code key} from the external resource. The cache will
   * communicate a delete when the entry is explicitly removed or evicted.
   *
   * @param key the non-null key whose value was removed
   * @param value the value associated with {@code key}, or {@code null} if collected
   * @param cause the reason for which the entry was removed
   * @throws RuntimeException or Error, in which case the mapping is unchanged
   */
  void delete(@Nonnull K key, @Nullable V value, @Nonnull RemovalCause cause);

  /**
   * Returns a writer that does nothing.
   *
   * @param <K> the type of keys
   * @param <V> the type of values
   * @return a writer that performs no operations
   */
  static @Nonnull <K, V> CacheWriter<K, V> disabledWriter() {
    @SuppressWarnings("unchecked")
    CacheWriter<K, V> writer = (CacheWriter<K, V>) DisabledWriter.INSTANCE;
    return writer;
  }
}

enum DisabledWriter implements CacheWriter<Object, Object> {
  INSTANCE;

  @Override
  public void write(Object key, Object value) {}

  @Override
  public void delete(Object key, Object value, RemovalCause cause) {}
}
