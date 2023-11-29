package com.example.neo4jlearn.Repository;

import com.example.neo4jlearn.entity.MovieEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

/**
 * @ClassName: MovieRepository
 * @Description: todo
 * @Author: wanghuaiyu
 * @Date: 2023/11/21
 */
public interface MovieRepository extends Neo4jRepository<MovieEntity, Long> {
    //    @Query("MATCH (n:Movie) WHERE id(n) = $0 RETURN n") 这种方法是自己写Query语句进行查询
    List<MovieEntity> findMovieEntitiesById(Long id);
    MovieEntity findMovieEntityByTitle(String title);
}