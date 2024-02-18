package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feed {
    private int eventId;
    private long timestamp;
    private int userId;
    private EventType eventType;
    private Operation operation;
    private int entityId;
}