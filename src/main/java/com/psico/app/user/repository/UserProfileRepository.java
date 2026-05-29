package com.psico.app.user.repository;

import com.psico.app.user.model.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {
    Optional<UserProfileEntity> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE UserProfileEntity u SET u.preferences = :preferences WHERE u.user.id = :userId")
    int updatePreferencesByUserId(@Param("userId") Long userId, @Param("preferences") String preferences);
}
