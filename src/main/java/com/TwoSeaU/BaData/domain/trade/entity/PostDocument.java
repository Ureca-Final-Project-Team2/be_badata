package com.TwoSeaU.BaData.domain.trade.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Document(indexName = "post")
public class PostDocument {
    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(name = "title", type = FieldType.Text)
    private String title;

    @Field(name = "comment", type = FieldType.Text)
    private String comment;

    @Field(name = "deadLine", type = FieldType.Date)
    private LocalDate deadLine;

    @Field(name = "createdAt", type = FieldType.Date, format = {DateFormat.date_hour_minute_second_millis, DateFormat.epoch_millis})
    private LocalDateTime createdAt;

    @Field(name = "postType", type = FieldType.Keyword)
    private String postType;

    public static PostDocument from(final Post post) {
        return PostDocument.builder()
                .id(post.getId())
                .title(post.getTitle())
                .comment(post.getComment())
                .deadLine(post.getDeadLine())
                .createdAt(post.getCreatedAt())
                .postType(post.getClass().getSimpleName())
                .build();
    }
}