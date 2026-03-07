package project.booteco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.booteco.domain.User;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User,UUID> {

    Optional<User> findByPhoneWhatsapp(String phoneWhatsapp);
    Optional<User> findById(UUID id);
}
