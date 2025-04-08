package mlanima;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
class DocumentManager {


    private final Map<String, Document> documents = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {

        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        if (document.getId() == null) {
            document.setId(UUID.randomUUID().toString());
        }

        documents.put(document.getId(), document);
        return document;
    }

    private Predicate<Document> searchFilter(SearchRequest request) {
        List<Predicate<Document>> searchFilters = new ArrayList<>();

        // filter by prefixes
        if (request.getTitlePrefixes() != null) {
            searchFilters.add(
                    d -> request
                            .getTitlePrefixes().stream()
                            .anyMatch(prefix -> d.getTitle().startsWith(prefix))
            );
        }

        // filter by contents
        if ( request.getContainsContents() != null) {
            searchFilters.add(
                    d -> request.getContainsContents().stream()
                            .anyMatch( content -> d.getContent().contains(content) )
            );
        }

        // filter by authors' ids
        if (request.getAuthorIds() != null) {
            searchFilters.add(
                    d -> request.getAuthorIds().contains(d.getId())
            );
        }

        // filter by creationFrom
        if (request.getCreatedFrom() != null) {
            searchFilters.add(d -> d.getCreated().isAfter(request.getCreatedFrom()));
        }

        // filter by creationTo
        if (request.getCreatedTo() != null) {
            searchFilters.add(d -> d.getCreated().isBefore(request.getCreatedTo()));
        }


        return searchFilters.stream().reduce( f -> true, Predicate::and);
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */

    public List<Document> search(SearchRequest request) {
        return documents.values().stream().filter(searchFilter(request)).toList();
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        if ( id == null ) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return Optional.ofNullable(documents.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }

}