package org.api.gateway.adapter.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PRENOTAZIONI")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrenotazioneEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "IDUTENTE", nullable = false)
    private Integer idUtente;

    @Column(name = "IDEVENTO", nullable = false)
    private Integer idEvento;

    @Column(name = "NUMEROPOSTI", nullable = false)
    private Integer numeroPosti;

    @Column(name = "DATAPRENOTAZIONE", nullable = false)
    private LocalDateTime dataPrenotazione;

    @Column(name = "DATAMODIFICA")
    private LocalDateTime dataModifica;

}
