package com.movie.Movie_BE.Repository;

import com.movie.Movie_BE.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);

    List<User> findAllByUserNameIn(List<String> userNames);

    @Query("SELECT u FROM User u WHERE u.userName IN :usernames")
    List<User> findByUserNameIn(@Param("usernames") List<String> usernames);

}
