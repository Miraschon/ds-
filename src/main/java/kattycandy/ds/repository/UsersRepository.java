package kattycandy.ds.repository;

import kattycandy.ds.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;


public interface UsersRepository extends JpaRepository<UserEntity, Integer>, JpaSpecificationExecutor<UserEntity> {

	Optional<UserEntity> findByUsernameAndPassword(String username, String password);

	Optional<UserEntity> findByUsername(String username);
}
