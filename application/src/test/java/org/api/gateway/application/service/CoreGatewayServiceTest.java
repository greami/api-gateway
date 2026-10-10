package org.api.gateway.application.service;

import org.api.gateway.application.model.GatewayResponse;
import org.api.gateway.application.port.out.CoreServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.PrenotazioniDisponibiliEvento;
import org.openapitools.model.Prenotazione;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreGatewayServiceTest {
    @Mock
    private CoreServicePort coreServicePort;

    @Test
    void bookingFallbacksRemainInTheApplicationUseCase() {
        when(coreServicePort.cercaPrenotazioneByIdUtente(1000))
                .thenThrow(new IllegalStateException("service unavailable"));
        when(coreServicePort.cercaPrenotazioniDisponibiliPerEvento(2000))
                .thenThrow(new IllegalStateException("service unavailable"));
        CoreGatewayService service = new CoreGatewayService(coreServicePort);

        GatewayResponse<List<Prenotazione>> bookings = service.cercaPrenotazioneByIdUtente(1000);
        GatewayResponse<PrenotazioniDisponibiliEvento> availability =
                service.cercaPrenotazioniDisponibiliPerEvento(2000);

        assertEquals(200, bookings.statusCode());
        assertEquals(List.of(), bookings.body());
        assertEquals(200, availability.statusCode());
        assertEquals(2000, availability.body().getIdEvento());
        assertEquals(0, availability.body().getNumeroPostiDispobili());
        assertEquals(0, availability.body().getNumeroPostiTotali());
    }
}
