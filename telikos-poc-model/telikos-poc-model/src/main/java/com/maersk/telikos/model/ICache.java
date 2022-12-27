package com.maersk.telikos.model;

import reactor.core.publisher.Mono;


public interface ICache<K,V> {
    Mono<V> get(K key);
    Mono<Boolean> put(K key,V value);
    Mono<Boolean> put(K key, V value, int entryTtl);
}
