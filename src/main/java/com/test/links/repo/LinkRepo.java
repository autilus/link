package com.test.links.repo;

import com.test.links.domain.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LinkRepo extends JpaRepository<Link, Long> {
    List<Link> findByUserId(String id);
}
