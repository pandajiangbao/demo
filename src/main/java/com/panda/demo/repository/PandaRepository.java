package com.panda.demo.repository;

import com.panda.demo.po.Panda;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Panda
 * @date 12/9/2019
 */
public interface PandaRepository extends MongoRepository<Panda, String> {
}
