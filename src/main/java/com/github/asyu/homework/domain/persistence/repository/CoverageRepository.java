package com.github.asyu.homework.domain.persistence.repository;

import com.github.asyu.homework.domain.persistence.entity.Coverage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoverageRepository extends JpaRepository<Coverage, Long> {

  List<Coverage> findByIdIn(List<Long> ids);
}
