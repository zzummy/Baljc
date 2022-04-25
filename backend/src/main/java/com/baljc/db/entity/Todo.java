package com.baljc.db.entity;

import com.baljc.common.util.BooleanToYNConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Todo {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID todoId;
    private LocalDate date;
    private String content;
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean completedYn;
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean deletedYn;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Todo(LocalDate date, String content, Boolean completedYn, Boolean deletedYn) {
        this.date = date;
        this.content = content;
        this.completedYn = completedYn;
        this.deletedYn = deletedYn;
    }
}
