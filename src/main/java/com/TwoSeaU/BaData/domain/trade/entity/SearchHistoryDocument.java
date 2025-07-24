package com.TwoSeaU.BaData.domain.trade.entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.time.Instant;

@Getter
@Document(indexName = "keyword-index-v2")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHistoryDocument {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(name = "@timestamp", type = FieldType.Date)
    private Instant timestamp;

    @Field(name = "thread_name", type = FieldType.Keyword)
    private String threadName;

    @Field(name = "@version", type = FieldType.Keyword)
    private String version;

    @Field(name = "logger_name", type = FieldType.Keyword)
    private String loggerName;

    @Field(type = FieldType.Keyword)
    private String level;

    @Field(name = "level_value", type = FieldType.Integer)
    private int levelValue;

    @Field(type = FieldType.Text)
    private String message;
}
