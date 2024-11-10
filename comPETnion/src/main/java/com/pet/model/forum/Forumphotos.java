package com.pet.model.forum;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "forumphotos")
public class Forumphotos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer photoId; // 重命名字段，以符合驼峰命名法


    @JsonIgnore
    @Lob
    private byte[] photoFile; // 使用 @Lob 注解标记大对象（如二进制数据）

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post; // 使用 @ManyToOne 和 @JoinColumn 注解定义多对一关系


}
