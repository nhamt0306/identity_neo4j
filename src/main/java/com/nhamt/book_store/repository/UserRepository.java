package com.nhamt.book_store.repository;

import com.nhamt.book_store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository <User, String> {
    boolean existsByUsername(String username);
}
