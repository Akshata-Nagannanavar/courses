//package com.example.course_backend;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.context.annotation.Bean;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonTypeInfo;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.*;
//import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
//import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
//import org.hibernate.collection.spi.PersistentCollection;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//
//@SpringBootApplication
//@EnableCaching
//@RestController
//public class DemoApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(DemoApplication.class, args);
//    }
//
//    @Autowired
//    private ObjectMapper springBootMapper; // reuse Spring Boot’s mapper (honors @JsonBackReference)
//
//    @Bean
//    public RedisCacheConfiguration redisCacheConfiguration() {
//        ObjectMapper mapper = springBootMapper.copy();
//
//        // ✅ Register Hibernate module (stops recursion, ignores lazy proxies)
//        Hibernate5JakartaModule hibernateModule = new Hibernate5JakartaModule();
//        hibernateModule.disable(Hibernate5JakartaModule.Feature.USE_TRANSIENT_ANNOTATION);
//        hibernateModule.enable(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING);
//        mapper.registerModule(hibernateModule);
//
//        // ✅ Custom module to convert PersistentCollection -> ArrayList during serialization
//        SimpleModule forceCollectionModule = new SimpleModule();
//        forceCollectionModule.setSerializerModifier(new BeanSerializerModifier() {
//            @Override
//            public JsonSerializer<?> modifySerializer(
//                    SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
//                if (PersistentCollection.class.isAssignableFrom(beanDesc.getBeanClass())) {
//                    return new JsonSerializer<Collection<?>>() {
//                        @Override
//                        public void serialize(Collection<?> value, JsonGenerator gen, SerializerProvider serializers)
//                                throws IOException {
//                            gen.writeObject(new ArrayList<>(value)); // Convert proxy to plain list
//                        }
//                    };
//                }
//                return serializer;
//            }
//        });
//        mapper.registerModule(forceCollectionModule);
//
//        // ✅ Polymorphic typing (keep as-is)
//        mapper.activateDefaultTyping(
//                LaissezFaireSubTypeValidator.instance,
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                JsonTypeInfo.As.PROPERTY
//        );
//
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//        // ✅ Custom PageImpl deserializer (unchanged)
//        SimpleModule pageModule = new SimpleModule();
//        pageModule.addDeserializer(PageImpl.class, new JsonDeserializer<PageImpl>() {
//            @Override
//            public PageImpl deserialize(JsonParser p, DeserializationContext ctxt)
//                    throws IOException {
//                JsonNode node = p.getCodec().readTree(p);
//                JsonNode content = node.get("content");
//                JsonNode number = node.get("number");
//                JsonNode size = node.get("size");
//                JsonNode total = node.get("totalElements");
//
//                List<?> list = mapper.convertValue(content, new TypeReference<List<?>>() {});
//                int page = number == null ? 0 : number.asInt();
//                int pageSize = size == null ? list.size() : size.asInt();
//                long totalElements = total == null ? list.size() : total.asLong();
//
//                return new PageImpl<>(list, PageRequest.of(page, pageSize), totalElements);
//            }
//        });
//        mapper.registerModule(pageModule);
//
//        // ✅ Redis serializer with updated mapper
//        GenericJackson2JsonRedisSerializer serializer =
//                new GenericJackson2JsonRedisSerializer(mapper);
//
//        return RedisCacheConfiguration.defaultCacheConfig()
//                .serializeValuesWith(
//                        RedisSerializationContext.SerializationPair.fromSerializer(serializer)
//                )
//                .disableCachingNullValues();
//    }
//
//    @GetMapping("/hello")
//    public String hello() {
//        return "Spring Boot is running alright!";
//    }
//}
package com.example.course_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableCaching
@RestController
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Spring Boot is running alright!";
    }
}
