package com.example.springsecurityjwt.Repository;

import com.example.springsecurityjwt.models.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Integer> {

    @Query(
            value = "SELECT * FROM relationship WHERE user_id1 = ?1 AND user_id2 = ?2 LIMIT 1",
            nativeQuery = true
    )
    Relationship findByUserId1AndUserId2(int userId1, int userId2);

    @Query(
            value = "SELECT * FROM relationship WHERE user_id1 = ?1 OR user_id2 = ?1",
            nativeQuery = true
    )
    List<Relationship> findAllInvitation(int receiverId);

    Relationship findByRelationshipId(int relationshipId);

    @Query(
            value = "SELECT * FROM relationship WHERE user_id2 = ?1 AND status =1",
            nativeQuery = true
    )
    List<Relationship> findUserId1Friends(int userId2);

    @Query(
            value = "SELECT * FROM relationship WHERE user_id1 = ?1 AND status = 1",
            nativeQuery = true
    )
    List<Relationship> findUserId2Friends(int userId1);

    @Query(
            value = "SELECT user_id1 FROM relationship WHERE user_id2 = ?1 AND action_user_id = ?1 AND status = 0",
            nativeQuery = true
    )
    List<Integer> findUserId1FriendsId(int userId2);

    @Query(
            value = "SELECT user_id2 FROM relationship WHERE user_id1 = ?1 AND action_user_id = ?1 AND status = 0",
            nativeQuery = true
    )
    List<Integer> findUserId2FriendsId(int userId1);
}
