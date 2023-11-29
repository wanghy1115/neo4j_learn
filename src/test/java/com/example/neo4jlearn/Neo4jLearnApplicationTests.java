package com.example.neo4jlearn;

import com.example.neo4jlearn.Repository.MovieRepository;
import com.example.neo4jlearn.Repository.PersonRepository;
import com.example.neo4jlearn.entity.MovieEntity;
import com.example.neo4jlearn.entity.PersonEntity;
import com.example.neo4jlearn.entity.relationship.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.data.neo4j.repository.query.QueryFragmentsAndParameters;

import java.util.*;

@SpringBootTest
class Neo4jLearnApplicationTests {
    @Autowired
    Neo4jTemplate neo4jTemplate;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    PersonRepository personRepository;

    /**
     * 初始化添加数据
     */
    @Test
    void createDataTestByNeo4jTemplate(){
        // 创建节点实体

        MovieEntity movie = new MovieEntity("你的名字","影片讲述了男女高中生在梦中相遇，并寻找彼此的故事。");// 电影实体节点

// 定义（参演）关系
// new Roles 参数1：Person实体，演员的出生年和姓名；参数2：演员名字列表（考虑到一个演员可能参演多个角色）
// 参数1是目标关系实体节点 参数2是关系属性
        Roles roles1 = new Roles(new PersonEntity(1998,"上白石萌音"), Collections.singletonList("宫水三叶"));
        Roles roles2 = new Roles(new PersonEntity(1993,"神木隆之介"), Collections.singletonList("立花泷"));
        PersonEntity director = new PersonEntity(1973,"新海诚");

// 添加movie的演员实体，加入（参演）关系
        movie.getActorsAndRoles().add(roles1);
        movie.getActorsAndRoles().add(roles2);
        movie.getDirectors().add(director);

// 存入图数据库持久化
        neo4jTemplate.save(movie);
    }

    /**
     * 查询数据
     */
    @Test
    void selectDataTestByNeo4jTemplate(){
        // 查询（不太推荐用Neo4jTemplate进行过滤查询，因为需要手动写cypherQuery,需要开发人员了解一下cypherQuery的写法）
        Optional<PersonEntity> person;
        // 1. 通过id查询
        person = neo4jTemplate.findById(12, PersonEntity.class);
        System.out.println("id为12号的Person节点：\n"+person);

        // 2. 通过属性查询节点，如name 需要手写cypherQuery语句
        Map<String,Object> map = new HashMap<>();
        map.put("name","新海诚");
        // 两种写法都对，看个人喜好 n是一个变量随意取，{}或者where填写query的filter过滤条件
        person = neo4jTemplate.findOne("MATCH (n:PersonDemo {name: $name}) RETURN n",map, PersonEntity.class);
//        person = neo4jTemplate.findOne("MATCH (n:Person) WHERE n.name = $name RETURN n",map, PersonEntity.class);
        System.out.println("\n查询名字为新海诚的Person节点:\n"+person);

        // 3. 通过属性关系查询节点
        map = new HashMap<>();
        map.put("roles",Collections.singletonList("宫水三叶"));
        // 方法1.使用toExecutableQuery查询
        QueryFragmentsAndParameters parameters = new QueryFragmentsAndParameters(
                "MATCH (person:PersonDemo) -[ relation:ACTED_IN]-> (movie:MovieDemo) \n" +
                        "WHERE relation.roles = $roles\n" +
                        "RETURN person",map);
        List<PersonEntity> roles = neo4jTemplate.toExecutableQuery(PersonEntity.class, parameters).getResults();
        // 方法2.使用findOne查询
//        Optional<PersonEntity> roles = neo4jTemplate.findOne(
//                "MATCH (person:Person) -[ relation:ACTED_IN]-> (movie:Movie) \n" +
//                "WHERE relation.roles = $roles\n" +
//                "RETURN person",map,PersonEntity.class);

        System.out.println("\n查询角色为“宫水三叶”的演员：\n"+roles);

    }
    /**
     * 修改数据
     */
    @Test
    void updateTestByNeo4jTemplate(){
        //查询新海诚的节点
        Map<String,Object> map = new HashMap<>();
        map.put("name","新海诚");
        PersonEntity person = neo4jTemplate.findOne("MATCH (n:PersonDemo {name: $name}) RETURN n",map, PersonEntity.class).get();

        Long userId = person.getId();// 记录当前查询的"新海诚"的节点id
        // 更新①---------更新“新海诚”的name为曾用名“新津诚”（这是他的曾用名）
        map.put("name","新海诚");
        map.put("usedName","新津诚");
        QueryFragmentsAndParameters queryFragmentsAndParameters =
                new QueryFragmentsAndParameters(
                        "MATCH (n:PersonDemo{name: $name}) SET n.name = $usedName",
                        map);
        neo4jTemplate.toExecutableQuery(
                PersonEntity.class,
                queryFragmentsAndParameters).getResults();
        Optional<PersonEntity> person1 = neo4jTemplate.findById(userId, PersonEntity.class);
        System.out.println("\n更新“新海诚”的name为曾用名“新津诚”（这是他的曾用名）:\n"+person1);
        // 更新②---------更新“新津诚”的name为“新海诚”
        person.setName("新海诚");
        neo4jTemplate.save(person);
        Optional<PersonEntity> person2 = neo4jTemplate.findById(userId, PersonEntity.class);
        System.out.println("\n更新“新津诚”的name为“新海诚”:\n"+person2);
    }

    /**
     * 删除数据,根据id删除
     */
    @Test
    void deleteDataTestByNeo4jTemplate(){
        Map<String,Object> map = new HashMap<>();
        map.put("name","新海诚");
        PersonEntity person = neo4jTemplate.findOne("MATCH (n:PersonDemo {name: $name}) RETURN n",map, PersonEntity.class).get();

        neo4jTemplate.deleteById(person.getId(), PersonEntity.class);
    }
    /**
     * 全部删除
     */
    @Test
    void deleteAllDataTestByNeo4jTemplate(){
        neo4jTemplate.deleteAll(PersonEntity.class);
        neo4jTemplate.deleteAll(MovieEntity.class);
    }

    /**
     * 创建数据
     */
    @Test
    void createDataTestByRepository(){
        // 创建节点
        MovieEntity movie = new MovieEntity("你的名字","影片讲述了男女高中生在梦中相遇，并寻找彼此的故事。");
        Roles roles1 = new Roles(new PersonEntity(1998,"上白石萌音"), Collections.singletonList("宫水三叶"));
        Roles roles2 = new Roles(new PersonEntity(1993,"神木隆之介"), Collections.singletonList("立花泷"));
        PersonEntity director = new PersonEntity(1973,"新海诚");
        // 添加关系
        movie.getActorsAndRoles().add(roles1);
        movie.getActorsAndRoles().add(roles2);
        movie.getDirectors().add(director);
        // 存入图数据库持久化
        movieRepository.save(movie);
    }
    /**
     * 查询数据
     */
    @Test
    void selectDataTestByRepository(){
        // 查询
        PersonEntity person = personRepository.findPersonEntityByName("上白石萌音");
        System.out.println("查询名字为“上白石萌音”的PersonEntity："+person);
        MovieEntity movieQueried = movieRepository.findMovieEntityByTitle("你的名字");
        System.out.println("查询名字为“你的名字”的MovieEntity："+movieQueried);
    }
    /**
     * 更新数据
     */
    @Test
    void updateDataTestByRepository(){
        //先把实体查询出来
        PersonEntity person = personRepository.findPersonEntityByName("上白石萌音");
        // 更新(更新主要是三步：1.获取实体id；2.修改实体属性；3.更新实体）
        // 注意：repository的save方法【对应的实体若id一致】则为修改，否则为新建。
        Long personId = person.getId();
        person.setBorn(1997);
        personRepository.save(person);
        person = personRepository.findPersonEntityByName("上白石萌音");
        System.out.println(personId == person.getId()?"\n更新“上白石萌音”出生日期为1997信息成功！：\n"+person:"更新信息失败！");
    }
    /**
     * 删除数据
     */
    @Test
    void deleteDataTestByRepository(){
        personRepository.deleteById(10L);
        personRepository.deleteAll();
        movieRepository.deleteAll();
    }

}
