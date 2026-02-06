package com.insightx.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public abstract class BaseRepositoryTest {
    
    @BeforeEach
    public void setUp() {
        // Common setup for all repository tests
    }
}
