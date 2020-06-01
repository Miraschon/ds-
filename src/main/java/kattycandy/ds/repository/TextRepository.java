package kattycandy.ds.repository;

import kattycandy.ds.entity.TextEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author Oleg Z. (cornknight@gmail.com)
 */
public interface TextRepository extends JpaRepository<TextEntity, Integer>, JpaSpecificationExecutor<TextEntity> {
	List<TextEntity> findAllByUserId(Integer userId);
}
