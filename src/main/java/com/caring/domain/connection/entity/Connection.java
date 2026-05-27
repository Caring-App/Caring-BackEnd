package com.caring.domain.connection.entity;

import com.caring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "connection")
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long connectionId;

    @ManyToOne(fetch = FetchType.LAZY) // 보호자 한명이 여러 connection 가질 수 있음, 필요할 때만 DB에서 가져오도록
    @JoinColumn(name = "protector_id")
    private Member protector;

    @OneToOne(fetch = FetchType.LAZY) // 돌봄대상자는 한명의 보호자와만 연결되어있기에
    @JoinColumn(name = "ward_id")
    private Member ward;

    @Column(nullable = false)
    private LocalDateTime linkedAt;

    @PrePersist
    public void prePersist(){
        this.linkedAt=LocalDateTime.now();
    }
}
