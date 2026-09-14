package com.bagas.pinjam100.repository.userrolepermission;

import com.bagas.pinjam100.entity.userrolepermission.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByIdAndDeletedDateIsNull(UUID id);
    List<User> findAllByDeletedDateIsNull();
    Optional<User> findByIdentityNumberAndDeletedDateIsNull(String username);
    @Query("""
        SELECT DISTINCT u
        FROM User u
        LEFT JOIN FETCH u.role r
        LEFT JOIN FETCH r.rolePermissions rp
        LEFT JOIN FETCH rp.permission
        WHERE u.identityNumber = :identityNumber
          AND u.deletedDate IS NULL
    """)
    Optional<User> findByIdentityNumberWithRoleAndPermissions(
            @Param("identityNumber") String identityNumber
    );

    boolean existsByIdentityNumber(String identityNumber);
}
