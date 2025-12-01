package library_system.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Provides a shared, pre-configured Jackson ObjectMapper for the application.
 * <p>
 * The mapper registers the Java Time module and configures common
 * serialization/deserialization features suitable for the project.
 * </p>
 */
public class MapperProvider {

    public static final ObjectMapper MAPPER = createMapper();

    private static ObjectMapper createMapper() {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JavaTimeModule());
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        m.findAndRegisterModules();
        m.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return m;
    }
}
