package hjyun.sample.jwt.domain.group.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//@Entity
//@Table(name = "groups")
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Getter
public class GroupEntity {

//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

//  @Column(nullable = false, unique = true)
  private String name;




}
