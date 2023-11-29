package com.example.neo4jlearn.entity.relationship;
import com.example.neo4jlearn.entity.PersonEntity;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import java.util.List;
/**
 * @ClassName: Roles
 * @Description: 定义一个关系属性
 * @Author: wanghuaiyu
 * @Date: 2023/11/20
 */
@RelationshipProperties
public class Roles {

    @RelationshipId
    private Long id;

    private final List<String> roles;

    @TargetNode // 相当于@StartNode
    private final PersonEntity person;

    // 参数1是目标关系实体节点 参数2是关系属性
    //    Roles 参数1：Person实体，演员的出生年和姓名；参数2：演员名字列表（考虑到一个演员可能参演多个角色）
    public Roles(PersonEntity person, List<String> roles) {
        this.person = person;
        this.roles = roles;
    }

    public List<String> getRoles() {
        return roles;
    }
}


