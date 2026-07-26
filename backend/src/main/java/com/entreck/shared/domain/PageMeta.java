package com.entreck.shared.domain;

/**
 * Pagination metadata for paginated responses.
 *
 * <p>Plain Java record with no framework imports so it can be used in the
 * pure domain layer. Conversion from a Spring Data
 * {@link org.springframework.data.domain.Page} is performed in the application
 * or interface layer to keep the domain dependency-free.
 *
 * @param page the current page number (0-based)
 * @param size the requested page size
 * @param totalElements the total number of elements across all pages
 * @param totalPages the total number of pages
 */
public record PageMeta(int page, int size, long totalElements, int totalPages) {

  /**
   * Creates a {@link PageMeta} from explicit pagination values.
   *
   * @param page the current page number
   * @param size the requested page size
   * @param totalElements the total number of elements
   * @param totalPages the total number of pages
   * @return the pagination metadata
   */
  public static PageMeta of(int page, int size, long totalElements, int totalPages) {
    return new PageMeta(page, size, totalElements, totalPages);
  }
}
