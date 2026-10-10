package org.api.gateway.application.port.out;

import org.api.gateway.application.port.contract.UserOperations;

public interface UserServicePort extends UserOperations.Users, UserOperations.Roles, UserOperations.UserRoles {
}
