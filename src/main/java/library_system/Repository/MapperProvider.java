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
     * <p>
     * Automatically configured to handle Java 8 date/time types and ignore unknown properties.
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
     * <p>
     * Configuration includes:
     * <ul>
     *     <li>JavaTimeModule for LocalDate/LocalDateTime support</li>
     *     <li>Disabling WRITE_DATES_AS_TIMESTAMPS to serialize dates as ISO strings</li>
     *     <li>Ignoring unknown JSON fields to allow forward compatibility</li>
     * </ul>
     *
     * @return configured mapper
     */
    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Support Java 8 date/time types
        mapper.registerModule(new JavaTimeModule());

        // Write LocalDate/LocalDateTime as ISO-8601 strings, not timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Ignore unknown fields to prevent exceptions when JSON has extra fields
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }
}
