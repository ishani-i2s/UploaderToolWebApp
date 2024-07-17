package com.example.demo.entity;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class TaskDetails {
    @Id
    String TaskId;
    String Description;
    String Site;
    String Status;
    String PlannedStartDate;
    String WorkType;
    String ObjectId;
    String log;
    String objectIdRowKey;
}
