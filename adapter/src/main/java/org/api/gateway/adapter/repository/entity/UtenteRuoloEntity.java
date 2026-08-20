package org.api.gateway.adapter.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "UTENTE_RUOLO",
    uniqueConstraints = @UniqueConstraint(name = "UK_UR", columnNames = {"IDUTENTE", "IDRUOLO"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtenteRuoloEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "IDUTENTE", nullable = false)
    private Long idUtente;

    @Column(name = "IDRUOLO", nullable = false)
    private Long idRuolo;
}
