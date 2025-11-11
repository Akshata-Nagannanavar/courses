package com.example.course_backend;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
public class RedisConfig {

    @Autowired
    private ObjectMapper springBootMapper;

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        // copy Spring Boot mapper so we keep app-wide settings
        ObjectMapper mapper = springBootMapper.copy();

        // 1) Register Hibernate module (avoid proxy recursion)
        Hibernate5JakartaModule hibernateModule = new Hibernate5JakartaModule();
        // don't force lazy loading here (we'll serialize collections safely)
        hibernateModule.disable(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING);
        mapper.registerModule(hibernateModule);

        // 2) Create cleanup module to unwrap/flatten Hibernate internals
        SimpleModule cleanupModule = new SimpleModule();

        // serializer for the base PersistentCollection
        cleanupModule.addSerializer(PersistentCollection.class, new JsonSerializer<PersistentCollection>() {
            @Override
            public void serialize(PersistentCollection value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                if (value == null) {
                    gen.writeNull();
                    return;
                }
                if (value.wasInitialized()) {
                    Object val = value.getValue();
                    if (val instanceof Collection<?>) {
                        gen.writeObject(new ArrayList<>((Collection<?>) val));
                    } else {
                        gen.writeObject(val);
                    }
                } else {
                    // uninitialized collection -> empty list in JSON
                    gen.writeStartArray();
                    gen.writeEndArray();
                }
            }
        });

        // serializer for HibernateProxy -> unwrap the proxied instance
        cleanupModule.addSerializer(HibernateProxy.class, new JsonSerializer<HibernateProxy>() {
            @Override
            public void serialize(HibernateProxy value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                if (value == null) {
                    gen.writeNull();
                    return;
                }
                Object unproxied = value.getHibernateLazyInitializer().getImplementation();
                gen.writeObject(unproxied);
            }
        });

        // 2.a) Also register serializers for concrete Hibernate collection classes by name,
        // because Jackson may pick the concrete class serializer (PersistentBag, PersistentSet, etc.)
        String[] concreteNames = new String[] {
                "org.hibernate.collection.internal.PersistentBag",
                "org.hibernate.collection.internal.PersistentSet",
                "org.hibernate.collection.internal.PersistentList",
                "org.hibernate.collection.internal.PersistentMap"
        };
        for (String cn : concreteNames) {
            try {
                Class<?> c = Class.forName(cn);
                // add same serializer implementation for the concrete class
                cleanupModule.addSerializer((Class) c, new JsonSerializer<Object>() {
                    @Override
                    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers)
                            throws IOException {
                        if (value == null) {
                            gen.writeNull();
                            return;
                        }
                        // try to treat it as PersistentCollection gracefully
                        if (value instanceof PersistentCollection pc) {
                            if (pc.wasInitialized()) {
                                Object val = pc.getValue();
                                if (val instanceof Collection<?> col) {
                                    gen.writeObject(new ArrayList<>(col));
                                } else {
                                    gen.writeObject(val);
                                }
                            } else {
                                gen.writeStartArray();
                                gen.writeEndArray();
                            }
                        } else {
                            // fallback: write as normal object
                            gen.writeObject(value);
                        }
                    }
                });
            } catch (ClassNotFoundException e) {
                // ignore if that concrete class does not exist in used hibernate version
            }
        }

        // 3) PageImpl deserializer (for cached Page<> responses)
        cleanupModule.addDeserializer(PageImpl.class, new JsonDeserializer<PageImpl<?>>() {
            @Override
            public PageImpl<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                ObjectMapper plainMapper = new ObjectMapper();
                plainMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JsonNode node = p.getCodec().readTree(p);
                List<?> list = mapper.convertValue(node.get("content"), new TypeReference<List<?>>() {});
                int number = node.has("number") ? node.get("number").asInt() : 0;
                int size = node.has("size") ? node.get("size").asInt() : list.size();
                long total = node.has(
                        "totalElements") ? node.get("totalElements").asLong() : list.size();
                return new PageImpl<>(list, PageRequest.of(number, size), total);
            }
        });

        // 4) Also handle Page interface references
        cleanupModule.addDeserializer(Page.class, new JsonDeserializer<PageImpl<?>>() {
            @Override
            public PageImpl<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                JsonNode node = p.getCodec().readTree(p);
                List<?> list = mapper.convertValue(node.get("content"), new TypeReference<List<?>>() {});
                int number = node.has("number") ? node.get("number").asInt() : 0;
                int size = node.has("size") ? node.get("size").asInt() : list.size();
                long total = node.has("totalElements") ? node.get("totalElements").asLong() : list.size();
                return new PageImpl<>(list, PageRequest.of(number, size), total);
            }
        });

        // Register cleanup module
        mapper.registerModule(cleanupModule);

        // 5) Enable polymorphic typing AFTER modules registered
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
        );

        // ✅ 6) Critical fix for "missing type id property '@class'" in JsonNode
        mapper.addMixIn(JsonNode.class, Object.class);

        // 7) Safe defaults
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 8) Build Redis serializer with configured mapper
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues();
    }
}
