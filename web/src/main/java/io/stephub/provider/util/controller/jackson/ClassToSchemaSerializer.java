package io.stephub.provider.util.controller.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

import java.io.IOException;

public class ClassToSchemaSerializer extends StdSerializer<Class<?>> {
    private static ObjectMapper mapper = new ObjectMapper();
    private static JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);

    public ClassToSchemaSerializer() {
        this(null);
    }

    public ClassToSchemaSerializer(Class<Class<?>> t) {
        super(t);
    }

    @Override
    public void serialize(Class<?> aClass, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        serializerProvider.defaultSerializeValue(schemaGen.generateSchema(aClass), jsonGenerator);
    }
}
