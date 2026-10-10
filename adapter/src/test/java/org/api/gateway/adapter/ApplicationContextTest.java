package org.api.gateway.adapter;

import org.api.gateway.application.port.in.AuthenticationUseCase;
import org.api.gateway.application.port.in.BookingsUseCase;
import org.api.gateway.application.port.in.EventsUseCase;
import org.api.gateway.application.port.in.LoadUserUseCase;
import org.api.gateway.application.port.in.RolesUseCase;
import org.api.gateway.application.port.in.UserRolesUseCase;
import org.api.gateway.application.port.in.UsersUseCase;
import org.api.gateway.application.port.in.WorkFeedbackUseCase;
import org.api.gateway.application.port.in.WorksUseCase;
import org.api.gateway.application.port.in.EventFeedbackUseCase;
import org.api.gateway.application.port.in.EventWorksUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApplicationContextTest {
    @Autowired
    private AuthenticationUseCase authenticationUseCase;

    @Autowired
    private LoadUserUseCase loadUserUseCase;

    @Autowired
    private EventsUseCase eventsUseCase;

    @Autowired
    private WorksUseCase worksUseCase;

    @Autowired
    private EventWorksUseCase eventWorksUseCase;

    @Autowired
    private EventFeedbackUseCase eventFeedbackUseCase;

    @Autowired
    private WorkFeedbackUseCase workFeedbackUseCase;

    @Autowired
    private BookingsUseCase bookingsUseCase;

    @Autowired
    private UsersUseCase usersUseCase;

    @Autowired
    private RolesUseCase rolesUseCase;

    @Autowired
    private UserRolesUseCase userRolesUseCase;

    @Test
    void wiresAllInboundPortsToApplicationAndOutboundAdapters() {
        assertNotNull(authenticationUseCase);
        assertNotNull(loadUserUseCase);
        assertNotNull(eventsUseCase);
        assertNotNull(worksUseCase);
        assertNotNull(eventWorksUseCase);
        assertNotNull(eventFeedbackUseCase);
        assertNotNull(workFeedbackUseCase);
        assertNotNull(bookingsUseCase);
        assertNotNull(usersUseCase);
        assertNotNull(rolesUseCase);
        assertNotNull(userRolesUseCase);
    }
}
