package com.simple.Bookstore.init;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SetupTrigramAndGinIndexing {
    private final JdbcTemplate jdbc;

    @Value("${spring.profiles.active:dev}")
    private String activeProfileRaw;

    private String activeProfile;

    @PostConstruct
    public void run() {
        activeProfile = (activeProfileRaw == null || activeProfileRaw.isBlank()) ? "dev" : activeProfileRaw;
        if (!activeProfile.equals("dev")) {
            log.info("[SetupTrigramAndGinIndexing] Skipped setup (profile: {})", activeProfile);
            return;
        }

        jdbc.execute("CREATE EXTENSION IF NOT EXISTS pg_trgm");
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_book_title_trgm ON book USING gin (title gin_trgm_ops)");
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_book_author_trgm ON book USING gin (author gin_trgm_ops)");
        log.info("[SetupTrigramAndGinIndexing] pg_trgm extension and GIN indexes applied.");
    }

    @PreDestroy
    public void cleanup() {
        if (!activeProfile.equals("dev")) {
            log.info("[SetupTrigramAndGinIndexing] Skipped setup (profile: {})", activeProfile);
            return;
        }

        // jdbc.execute("DROP EXTENSION IF EXISTS pg_trgm");
        jdbc.execute("DROP INDEX IF EXISTS idx_book_title_trgm");
        jdbc.execute("DROP INDEX IF EXISTS idx_book_author_trgm");
        log.info("[SetupTrigramAndGinIndexing] GIN indexes dropped.");
    }
}
