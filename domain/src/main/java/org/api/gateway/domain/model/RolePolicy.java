package org.api.gateway.domain.model;

import java.util.List;

public final class RolePolicy {
    private static final int ADMIN_ROLE_ID = 2;

    private RolePolicy() {
    }

    public static List<String> authoritiesFor(List<Integer> roleIds) {
        return List.of(roleIds.contains(ADMIN_ROLE_ID) ? "ROLE_ADMIN" : "ROLE_USER");
    }
}
