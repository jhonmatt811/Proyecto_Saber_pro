package com.icfes_group.repository.util;


import com.icfes_group.model.User;
import com.icfes_group.model.util.PasswordChanges;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordChangesRepository extends JpaRepository<PasswordChanges, UUID> {
    Optional<PasswordChanges> findFirstByUserOrderByDateDesc(User user);
}
