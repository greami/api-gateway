package org.api.gateway.application.port.out;

import org.api.gateway.application.port.contract.CoreOperations;

public interface CoreServicePort extends CoreOperations.Events, CoreOperations.Works, CoreOperations.EventWorks,
        CoreOperations.EventFeedback, CoreOperations.WorkFeedback, CoreOperations.Bookings {
}
