package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Feed {
    private int eventId;
    private long timestamp;
    private int userId;
    private String eventType;
    private String operation;
    private int entityId;
}
