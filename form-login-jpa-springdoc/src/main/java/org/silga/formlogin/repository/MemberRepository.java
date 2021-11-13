package org.silga.formlogin.repository;

import org.silga.formlogin.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m JOIN FETCH m.authorities WHERE m.username = (:username)")
    Member findByUsername(String username);
}
