package com.example.springsecurityjwt.Repository;

import com.example.springsecurityjwt.models.Mountain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface MountainRepository extends JpaRepository<Mountain, Integer> {

    Mountain findByMountainId(int mountainId);

    @Query(
            value = "SELECT * FROM mountain WHERE area = ?1 ORDER BY popularity DESC",
            nativeQuery = true
    )
    List<Mountain> findByArea(int area);

    @Query(
            value = "SELECT * FROM mountain WHERE city = ?1 ORDER BY popularity DESC",
            nativeQuery = true
    )
    List<Mountain> findByCity(int city);

    @Query(
            value = "SELECT * FROM mountain WHERE altitude BETWEEN 3000 AND 3300 ORDER BY popularity DESC",
            nativeQuery = true
    )
    List<Mountain> findByAltitude3000To3300();

    @Query(
            value = "SELECT * FROM mountain WHERE altitude BETWEEN 3301 AND 3600 ORDER BY popularity DESC",
            nativeQuery = true
    )
    List<Mountain> findByAltitude3301To3600();

    @Query(
            value = "SELECT * FROM mountain WHERE altitude > 3600 ORDER BY popularity DESC",
            nativeQuery = true
    )
    List<Mountain> findByAltitudeFrom3600();

    @Query(
            value = "SELECT * FROM mountain WHERE difficulty = ?1 ORDER BY popularity DESC",
            nativeQuery = true
    )
    List<Mountain> findByDifficulty(int difficulty);

    @Query(
            value = "SELECT * FROM mountain ORDER BY popularity DESC",
            nativeQuery = true
    )
    List<Mountain> findByPopularity();

    @Query(
            value = "SELECT mountain_name FROM mountain WHERE mountain_id = ?1",
            nativeQuery = true
    )
    String findMountainNameById(int mountainId);

    @Query(
            value = "SELECT photo_path FROM mountain WHERE mountain_id = ?1",
            nativeQuery = true
    )
    String findMountainPhotoPath(int mountainId);

    @Query(
            value = "SELECT * FROM mountain WHERE mountain_name = ?1%",
            nativeQuery = true
    )
    List<Mountain> keywordSearch(String keyword);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE mountain SET popularity = popularity + 1 WHERE mountain_id = ?1",
            nativeQuery = true
    )
    void addPopularityByOne(int mountainId);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE mountain SET popularity = popularity -1 WHERE mountain_id = ?1",
            nativeQuery = true
    )
    void lessPopularityByOne(int mountainId);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE mountain SET summit_reached = summit_reached + 1 WHERE mountain_id = ?1",
            nativeQuery = true
    )
    void addSummitReachedByOne(int mountainId);

    @Query(
            value = "SELECT * FROM mountain ORDER BY summit_reached DESC",
            nativeQuery = true
    )
    List<Mountain> findMostReached();

    @Query(
            value = "SELECT mountain_name FROM mountain WHERE mountain_id = ?1",
            nativeQuery = true
    )
    String getMountain(int mountainId);
}
