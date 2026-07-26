package com.entreck.shared.web;

import com.entreck.shared.domain.PageMeta;
import java.util.List;

/**
 * Generic pagination envelope for list responses.
 *
 * @param content the page content
 * @param meta the pagination metadata
 * @param first whether this is the first page
 * @param last whether this is the last page
 * @param <T> the element type
 */
public record PageResponse<T>(List<T> content, PageMeta meta, boolean first, boolean last) {

  /**
   * Builds a {@link PageResponse} from a Spring Data {@link org.springframework.data.domain.Page}
   * and a content mapper.
   *
   * @param page the Spring Data page
   * @param mapper the function mapping each domain element to its response DTO
   * @return the pagination envelope
   * @param <D> the domain element type
   * @param <R> the response DTO type
   */
  public static <D, R> PageResponse<R> from(org.springframework.data.domain.Page<D> page,
                                            java.util.function.Function<D, R> mapper) {
    List<R> mapped = page.getContent().stream().map(mapper).toList();
    PageMeta meta = PageMeta.of(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    return new PageResponse<>(mapped, meta, page.isFirst(), page.isLast());
  }
}
