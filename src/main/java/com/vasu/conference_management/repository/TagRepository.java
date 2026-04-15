package com.vasu.conference_management.repository;

import com.vasu.conference_management.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByTagName(String tagName);
    List<Tag> findByTagNameContainingIgnoreCase(String tagName);
}

