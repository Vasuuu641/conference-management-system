package com.vasu.conference_management.service;

import com.vasu.conference_management.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class TagServiceTests {

    @Autowired
    private TagService tagService;

    @Test
    void createTagStoresNewTag() {
        String name = "ServiceTag-" + System.nanoTime();

        Tag tag = tagService.createTag(name, "Testing service layer");

        assertNotNull(tag.getId());
        assertEquals(name, tag.getTagName());
    }

    @Test
    void createTagRejectsDuplicateIgnoringCase() {
        String name = "DuplicateTag-" + System.nanoTime();
        tagService.createTag(name, "first");

        assertThrows(IllegalStateException.class, () -> tagService.createTag(name.toLowerCase(), "second"));
    }

    @Test
    void findByNameReturnsStoredTag() {
        String name = "LookupTag-" + System.nanoTime();
        Tag created = tagService.createTag(name, "lookup test");

        Tag found = tagService.findByName(name.toUpperCase());

        assertEquals(created.getId(), found.getId());
    }
}

