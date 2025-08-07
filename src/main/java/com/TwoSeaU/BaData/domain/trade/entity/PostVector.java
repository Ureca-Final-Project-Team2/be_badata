package com.TwoSeaU.BaData.domain.trade.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "post_vector")
public class PostVector {
    @Id
    private Long id;

    @Column(name = "vector_pg", columnDefinition = "vector(57)")
    private float[] vectorPg;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Post post;
}
