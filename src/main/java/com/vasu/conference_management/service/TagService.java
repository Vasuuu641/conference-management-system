package com.vasu.conference_management.service;

import com.vasu.conference_management.entity.Tag;
import com.vasu.conference_management.repository.TagRepository;
import com.vasu.conference_management.util.ValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Tag createTag(String tagName, String description) {
        if (tagName == null || tagName.isBlank()) {
            throw new IllegalArgumentException("tagName is required");
        }

        String normalizedName = tagName.trim();
        if (tagRepository.existsByTagNameIgnoreCase(normalizedName)) {
            throw new IllegalStateException("Tag already exists: " + normalizedName);
        }

        Tag tag = new Tag();
        tag.setTagName(normalizedName);
        tag.setDescription(description);
        return tagRepository.save(tag);
    }

    @Transactional(readOnly = true)
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Tag findById(Long id) {
        ValidationUtil.requirePositiveId(id, "id");
        return tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + id));
    }

    @Transactional(readOnly = true)
    public Tag findByName(String tagName) {
        if (tagName == null || tagName.isBlank()) {
            throw new IllegalArgumentException("tagName is required");
        }
        return tagRepository.findByTagNameIgnoreCase(tagName.trim())
                .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + tagName));
    }

    @Transactional(readOnly = true)
    public List<Tag> search(String term) {
        if (term == null || term.isBlank()) {
            return findAll();
        }
        return tagRepository.findByTagNameContainingIgnoreCase(term.trim());
    }
}

