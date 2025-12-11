package library_system.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Provides a globally shared and preconfigured Jackson ObjectMapper.
 * <p>
 * Features:
 * <ul>
 *     <li>Supports Java Time (LocalDate, LocalDateTime)</li>
 *     <li>Pretty printing disabled timestamps</li>
 *     <li>Ignores unknown JSON fields</li>
 *     <li>Can be reset during unit tests</li>
 * </ul>
 */
public class MapperProvider {

    /**
     * Shared ObjectMapper instance used by all repository classes.
     */
    public static ObjectMapper MAPPER = createMapper();

    /**
     * Reinitializes the mapper.
     * <p>
     * Useful in unit tests to ensure a clean JSON environment.
     */
    public static void resetForTesting() {
        MAPPER = createMapper();
    }

    /**
     * Creates and configures a new ObjectMapper instance.
     *
     * @return configured mapper
     */
    private static ObjectMapper createMapper() {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JavaTimeModule());
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return m;
    }
}
