package com.baljc.api.service;

import com.baljc.api.dto.ChatDto;

import java.util.List;

public interface ChatService {
    List<ChatDto.RoomResponse> getRoomList();
}
