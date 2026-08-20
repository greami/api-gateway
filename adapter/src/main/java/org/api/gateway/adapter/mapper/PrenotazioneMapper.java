package org.api.gateway.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openapitools.model.Prenotazione;
import org.api.gateway.adapter.repository.entity.PrenotazioneEntity;
import org.api.gateway.domain.model.userService.PrenotazioneRecord;

@Mapper(componentModel = "spring")
public interface PrenotazioneMapper {


    @Mapping(target = "idPrenotazione", source = "id")
    @Mapping(target = "idUtente", source = "idUtente")
    @Mapping(target = "idEvento", source = "idEvento")
    @Mapping(target = "numeroPartecipanti", source = "numeroPosti")
    @Mapping(target = "dataCreazione", source = "dataPrenotazione")
    @Mapping(target = "dataUltimaModifica", source = "dataModifica")
    Prenotazione toPrenotazione(PrenotazioneRecord prenotazioneRecord);

    @Mapping(target = "id", source = "idPrenotazione")
    @Mapping(target = "idUtente", source = "idUtente")
    @Mapping(target = "idEvento", source = "idEvento")
    @Mapping(target = "numeroPosti", source = "numeroPartecipanti")
    @Mapping(target = "dataPrenotazione", source = "dataCreazione")
    @Mapping(target = "dataModifica", source = "dataUltimaModifica")
    PrenotazioneRecord toPrenotazioneRecord(Prenotazione prenotazione);


    @Mapping(target = "idUtente", source = "record.idUtente")
    @Mapping(target = "idEvento", source = "record.idEvento")
    @Mapping(target = "numeroPosti", source = "record.numeroPosti")
    @Mapping(target = "dataPrenotazione", source = "record.dataPrenotazione")
    @Mapping(target = "dataModifica", source = "record.dataModifica")
    PrenotazioneEntity toPrenotazioneEntity(PrenotazioneRecord record);


}
