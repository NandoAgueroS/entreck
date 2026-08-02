package com.entreck.shared.domain;

import java.util.List;

/**
 * Domain-level paginated result.
 *
 * <p>Framework-agnostic alternative to Spring Data's {@code Page<T>}. The
 * infrastructure adapter converts from Spring Data types to this type before
 * returning to the application layer, keeping the domain dependency-free.
 *
 * @param <T> the element type
 * @param content the list of elements in the current page
 * @param meta the pagination metadata
 */
public record DomainPage<T>(List<T> content, PageMeta meta) {

  /**
   * Creates a paginated result.
   *
   * @param content the list of elements
   * @param meta the pagination metadata
   * @param <T> the element type
   * @return the paginated result
   */
  public static <T> DomainPage<T> of(List<T> content, PageMeta meta) {
    if (content == null) {
      throw new IllegalArgumentException("Content is required");
    }
    if (meta == null) {
      throw new IllegalArgumentException("Page meta is required");
    }
    return new DomainPage<>(List.copyOf(content), meta);
  }
}
