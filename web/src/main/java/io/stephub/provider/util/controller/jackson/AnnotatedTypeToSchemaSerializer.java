package io.stephub.provider.util.controller.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

import java.io.IOException;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;

public class AnnotatedTypeToSchemaSerializer extends StdSerializer<AnnotatedType> {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);

    public AnnotatedTypeToSchemaSerializer() {
        this(null);
    }

    public AnnotatedTypeToSchemaSerializer(final Class<AnnotatedType> t) {
        super(t);
    }

    @Override
    public void serialize(final AnnotatedType type, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
        if (type.getType() instanceof ParameterizedType) {
            serializerProvider.defaultSerializeValue(schemaGen.generateSchema((Class<?>) ((ParameterizedType) type.getType()).getRawType()), jsonGenerator);
        } else {
            serializerProvider.defaultSerializeValue(schemaGen.generateSchema((Class<?>) type.getType()), jsonGenerator);
        }
    }
}
