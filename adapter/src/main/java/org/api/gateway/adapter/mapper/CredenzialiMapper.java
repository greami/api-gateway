package org.api.gateway.adapter.mapper;

import org.api.gateway.domain.model.userService.RuoloRecord;
import org.api.gateway.domain.model.userService.UtenteRecord;
import org.api.gateway.domain.model.userService.UtenteRuoloRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openapitools.model.*;

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

}
