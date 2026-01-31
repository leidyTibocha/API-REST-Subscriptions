package com.musicPlay.music_play.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

import com.musicPlay.music_play.api.dto.CreateSubscriptionRequest;
import com.musicPlay.music_play.api.dto.SubscriptionResponse;
import com.musicPlay.music_play.domain.model.Subscription;
import com.musicPlay.music_play.domain.model.SubscriptionPlan;
import com.musicPlay.music_play.domain.model.SubscriptionStatus;
import com.musicPlay.music_play.infrastructure.entity.SubscriptionEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MapperSubscription {
    // De Entidad a Dominio (Usa el constructor con todos los argumentos)
    Subscription toDomain(SubscriptionEntity subscriptionEntity);

    // PARA CREAR: De DTO a Dominio
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "autoRenew", ignore = true)
    @Mapping(target = "plan", source = "plan", qualifiedByName = "stringToPlan")
    Subscription toDomainFromRequest(CreateSubscriptionRequest request);

    // De Dominio a Entidad (Para guardar en DB)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "plan", source = "plan", qualifiedByName = "planToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    SubscriptionEntity toEntity(Subscription subscription);

    // De Dominio a DTO de Respuesta
    @Mapping(target = "subscriptionId", source = "id")
    @Mapping(target = "plan", source = "plan", qualifiedByName = "planToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    SubscriptionResponse toResponse(Subscription subscription);


    List<Subscription> toDomainList(List<SubscriptionEntity> entities);
    List<SubscriptionResponse> toResponseList(List<Subscription> domains);

    // --- DE STRING A ENUM (Entrada/Request -> Dominio) ---

    @Named("stringToPlan")
    default SubscriptionPlan mapToPlanEnum(String planName) {
        if (planName == null) return null;
        try {
            return SubscriptionPlan.valueOf(planName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Plan no válido: " + planName);
        }
    }

    @Named("stringToStatus")
    default SubscriptionStatus mapToStatusEnum(String statusName) {
        if (statusName == null) return null;
        return SubscriptionStatus.valueOf(statusName.toUpperCase());
    }

    // --- DE ENUM A STRING (Dominio -> Entidad/Response) ---

    @Named("planToString")
    default String mapPlanToString(SubscriptionPlan plan) {
        return (plan != null) ? plan.name() : null;
    }

    @Named("statusToString")
    default String mapStatusToString(SubscriptionStatus status) {
        return (status != null) ? status.name() : null;
    }

    @ObjectFactory
    default Subscription createFromEntity(SubscriptionEntity entity) {
        if (entity == null) return null;
        return new Subscription(
                entity.getId(),
                entity.getUserId(),
                entity.getStartDate(),
                entity.getEndDate(),
                mapToPlanEnum(entity.getPlan()),
                mapToStatusEnum(entity.getStatus()),
                entity.isAutoRenew()
        );
    }

}
