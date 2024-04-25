package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.Rating;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.UserService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByRatedUserAndFromUser(User id, UUID uuid);
    List<Rating> getRatingsByRatedUser(User user);
//    boolean existsByFromUser(UUID id);


//    @Query(value = "SELECT * FROM Rating WHERE rated_user_id == ?1", nativeQuery = true)
//    List<Rating> getRatingsByUUID(UUID uuid);

//    List<Rating> getAllByRatedUserUUID(UUID uuid);



}
