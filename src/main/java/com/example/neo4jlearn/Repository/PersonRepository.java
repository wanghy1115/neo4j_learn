package com.example.neo4jlearn.Repository;

import com.example.neo4jlearn.entity.PersonEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * @ClassName: PersonRepository
 * @Description: todo
 * @Author: wanghuaiyu
 * @Date: 2023/11/21
 */
public interface PersonRepository extends Neo4jRepository<PersonEntity, Long> {
    PersonEntity findPersonEntityByName(String name);
}
