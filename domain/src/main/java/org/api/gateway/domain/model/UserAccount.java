package org.api.gateway.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record UserAccount(Integer id, String username, String password, List<Integer> roleIds) {
    public UserAccount {
        Objects.requireNonNull(username, "username");
        roleIds = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(roleIds, "roleIds")));
    }

    public List<String> authorities() {
        return RolePolicy.authoritiesFor(roleIds);
    }
}
