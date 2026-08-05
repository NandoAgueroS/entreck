package com.entreck.pos.config;

import com.entreck.pos.application.mapper.PointOfSaleMapper;
import com.entreck.pos.application.usecase.GetPointOfSaleDetailUseCase;
import com.entreck.pos.application.usecase.RegisterPointOfSaleUseCase;
import com.entreck.pos.application.usecase.UpdatePointOfSaleUseCase;
import com.entreck.pos.application.usecase.impl.GetPointOfSaleDetailUseCaseImpl;
import com.entreck.pos.application.usecase.impl.RegisterPointOfSaleUseCaseImpl;
import com.entreck.pos.application.usecase.impl.UpdatePointOfSaleUseCaseImpl;
import com.entreck.pos.domain.repository.PointOfSaleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration wiring point-of-sale use cases.
 *
 * <p>Use case implementations are plain Java classes (no Spring annotations).
 * This configuration creates Spring beans for each use case, injecting the
 * domain port and the application-layer MapStruct mapper.
 */
@Configuration
public class PosConfig {

  /**
   * Wires the register-point-of-sale use case (S01).
   *
   * @param pointOfSaleRepository the POS repository port
   * @param pointOfSaleMapper the application-layer DTO mapper
   * @return the use case bean
   */
  @Bean
  public RegisterPointOfSaleUseCase registerPointOfSaleUseCase(
      PointOfSaleRepository pointOfSaleRepository, PointOfSaleMapper pointOfSaleMapper) {
    return new RegisterPointOfSaleUseCaseImpl(pointOfSaleRepository, pointOfSaleMapper);
  }

  /**
   * Wires the update-point-of-sale use case (S02).
   *
   * @param pointOfSaleRepository the POS repository port
   * @param pointOfSaleMapper the application-layer DTO mapper
   * @return the use case bean
   */
  @Bean
  public UpdatePointOfSaleUseCase updatePointOfSaleUseCase(
      PointOfSaleRepository pointOfSaleRepository, PointOfSaleMapper pointOfSaleMapper) {
    return new UpdatePointOfSaleUseCaseImpl(pointOfSaleRepository, pointOfSaleMapper);
  }

  /**
   * Wires the get-point-of-sale-detail use case (B06).
   *
   * @param pointOfSaleRepository the POS repository port
   * @param pointOfSaleMapper the application-layer DTO mapper
   * @return the use case bean
   */
  @Bean
  public GetPointOfSaleDetailUseCase getPointOfSaleDetailUseCase(
      PointOfSaleRepository pointOfSaleRepository, PointOfSaleMapper pointOfSaleMapper) {
    return new GetPointOfSaleDetailUseCaseImpl(pointOfSaleRepository, pointOfSaleMapper);
  }
}
