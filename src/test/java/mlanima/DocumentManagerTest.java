package mlanima;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static mlanima.DocumentManager.*;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private DocumentManager manager;

    @BeforeEach
    void setUp() {
        manager = new DocumentManager();

        Author author = new Author("1", "Michel");
        Document a = new Document("1", "My Document a", "Content a", author, Instant.MAX);
        Document b = new Document("2", "My Document b", "Content b", author, Instant.MIN);
        Document c = new Document("3", "My Document c", "Content c", author, Instant.now());

        manager.save(a);
        manager.save(b);
        manager.save(c);

    }

    @Test
    void save() {
        Document b2 = new Document("1", "Changed document", "Content", null, Instant.now());

        assertEquals(b2, manager.save(b2));
    }

    @Test
    void saveNull() {
        assertThrows(IllegalArgumentException.class, () -> manager.save(null));
    }

    @Test
    void saveDocumentWithNullId() {
        Document b2 = new Document(null, "Document b2", "Content b2", null, Instant.now());
        Document b3 = new Document(null, "Document b3", "Content b3", null, Instant.now());
        manager.save(b2);
        assertEquals(b3, manager.save(b3));
    }

    @Test
    void searchByNullInstants() {
        SearchRequest request = new SearchRequest(List.of("My"), List.of("a"), List.of("1", "2"), null, null);
        assertFalse(manager.search(request).isEmpty());
    }

    @Test
    void searchByNulls() {
        SearchRequest request = new SearchRequest(null, null, null, null, null);
        assertFalse(manager.search(request).isEmpty());
    }

    @Test
    void searchBeforeInstant() {
        SearchRequest request = new SearchRequest(null, null, null, null, Instant.now());
        assertEquals(manager.search(request).size(), 2);
        assertNotEquals(manager.search(request).getFirst().getId(), "1");
    }

    @Test
    void searchAfterInstant() {
        SearchRequest request = new SearchRequest(null, null, null, Instant.now(), null);
        assertEquals(manager.search(request).size(), 1);
    }

    @Test
    void findById() {
        assertTrue(manager.findById("1").isPresent());
        assertEquals(manager.findById("1").get().getId(), "1");
    }

    @Test
    void findByNullId() {
        assertThrows(IllegalArgumentException.class, () -> manager.findById(null));
    }
}