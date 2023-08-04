package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.Star;

import java.util.List;
import java.util.Optional;

@Repository
public interface StarRepository extends JpaRepository<Star, Long> {
    Optional<Star> findFirstByName(String name);
    Optional<Star> findFirstById(Long id);

    @Query("SELECT s FROM Star s " +
            "LEFT JOIN Astronomer a on a.observingStar.id = s.id " +
            "where s.starType = 'RED_GIANT' AND a.observingStar IS NULL " +
            "order by s.lightYears")
    List<Star> findAllStarsRedGiantsAndNeverBeenObservedOrderByLightYears();

}
