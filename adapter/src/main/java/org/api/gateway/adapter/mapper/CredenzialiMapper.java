package org.api.gateway.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openapitools.model.RuoloCreateRequest;
import org.openapitools.model.RuoloResponse;
import org.openapitools.model.RuoloUpdateRequest;
import org.openapitools.model.UtenteCreateRequest;
import org.openapitools.model.UtenteResponse;
import org.openapitools.model.UtenteRuoloCreateRequest;
import org.openapitools.model.UtenteRuoloResponse;
import org.openapitools.model.UtenteRuoloUpdateRequest;
import org.openapitools.model.UtenteUpdateRequest;
import org.api.gateway.domain.model.userService.RuoloRecord;
import org.api.gateway.domain.model.userService.UtenteRecord;
import org.api.gateway.domain.model.userService.UtenteRuoloRecord;

@Mapper(componentModel = "spring")
public interface CredenzialiMapper {

    @Mapping(target = "idUtente", source = "id")
    UtenteResponse toUtenteResponse(UtenteRecord utenteRecord);

    @Mapping(target = "id", ignore = true)
    UtenteRecord toUtenteRecord(UtenteCreateRequest utenteCreateRequest);

    @Mapping(target = "id", ignore = true)
    UtenteRecord toUtenteRecord(UtenteUpdateRequest utenteUpdateRequest);

    @Mapping(target = "idRuolo", source = "id")
    RuoloResponse toRuoloResponse(RuoloRecord ruoloRecord);

    @Mapping(target = "id", source = "idRuolo")
    RuoloRecord toRuoloRecord(RuoloCreateRequest ruoloCreateRequest);

    @Mapping(target = "id", source = "idRuolo")
    RuoloRecord toRuoloRecord(RuoloUpdateRequest ruoloUpdateRequest);

    @Mapping(target = "idUtenteRuolo", source = "id")
    UtenteRuoloResponse toUtenteRuoloResponse(UtenteRuoloRecord utenteRuoloRecord);

    @Mapping(target = "id", ignore = true)
    UtenteRuoloRecord toUtenteRuoloRecord(UtenteRuoloCreateRequest utenteRuoloCreateRequest);

    @Mapping(target = "id", ignore = true)
    UtenteRuoloRecord toUtenteRuoloRecord(UtenteRuoloUpdateRequest utenteRuoloUpdateRequest);
}
