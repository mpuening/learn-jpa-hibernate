package io.github.learnjpahibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.github.learnjpahibernate.model.HailingFrequency;

@RepositoryRestResource(collectionResourceRel = "hailing-frequencies", path = "hailing-frequencies")
public interface HailingFrequencyRepository extends JpaRepository<HailingFrequency, Long> {

}
