package kattycandy.ds.repository;

import kattycandy.ds.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;


public interface UsersRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

	Optional<User> findByUsernameAndPassword(String username, String password);
}
