package com.lambdaschool.piggybank.repositories;

import com.lambdaschool.piggybank.models.Piggybank;
import org.springframework.data.repository.CrudRepository;

public interface PiggybankRepository extends CrudRepository<Piggybank,Long> {
}
