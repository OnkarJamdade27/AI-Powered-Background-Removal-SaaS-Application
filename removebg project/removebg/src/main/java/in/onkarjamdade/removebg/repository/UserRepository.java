package in.onkarjamdade.removebg.repository;

import in.onkarjamdade.removebg.entity.UserEntity;
import in.onkarjamdade.removebg.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByClerkId(String clerkId);

    boolean existsByClerkId(String clerkId);
}
