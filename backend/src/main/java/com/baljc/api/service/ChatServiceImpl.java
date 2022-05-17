package com.baljc.api.service;

import com.baljc.api.dto.ChatDto;
import com.baljc.api.dto.MemberDto;
import com.baljc.db.entity.Chat;
import com.baljc.db.entity.Member;
import com.baljc.db.entity.Room;
import com.baljc.db.repository.ChatRepository;
import com.baljc.db.repository.MemberRepository;
import com.baljc.db.repository.RoomRepository;
import com.baljc.exception.NotExistedMemberException;
import com.baljc.exception.NotExistedRoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService{

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final ChatRepository chatRepository;

    public ChatServiceImpl(MemberService memberService, MemberRepository memberRepository, RoomRepository roomRepository, ChatRepository chatRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.roomRepository = roomRepository;
        this.chatRepository = chatRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatDto.RoomResponse insertRoom(ChatDto.Request request) {
        if (!(memberRepository.findById(request.getMemberId1()).isPresent()
                && memberRepository.findById(request.getMemberId2()).isPresent()))
            throw new NotExistedMemberException("아이디로 조회되는 회원이 존재하지 않습니다.");

        Room room = roomRepository.findByMembers(request.getMemberId1(), request.getMemberId2()).orElseGet(
                () -> roomRepository.save(Room.builder()
                        .member1(memberRepository.getById(request.getMemberId1()))
                        .member2(memberRepository.getById(request.getMemberId2()))
                        .build())
        );

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateTime = room.getUpdatedAt();
        String updatedAt = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long diff = ChronoUnit.DAYS.between(dateTime, now);

        if (diff < 7) {
            if (diff >= 1) updatedAt = diff + "일전";
            else {
                diff = ChronoUnit.HOURS.between(dateTime, now);
                if (diff >= 1) updatedAt = diff + "시간전";
                else {
                    diff = ChronoUnit.MINUTES.between(dateTime, now);
                    if (diff >= 1) updatedAt = diff + "분전";
                    else updatedAt = "방금전";
                }
            }
        }

        Member other = room.getMember1();
        if (other.getMemberId().equals(memberService.getMemberByAuthentication().getMemberId()))
            other = room.getMember2();
        return new ChatDto.RoomResponse(room.getRoomId(), updatedAt, new MemberDto.Other(other.getNickname(),
                other.getProfileUrl(), other.getDepth1(), other.getDepth2(), other.getDepth3()));
    }

    @Override
    public List<ChatDto.RoomResponse> getRoomList() {
        Member member = memberService.getMemberByAuthentication();
        List<Room> roomList1 = member.getRoomList1();
        List<Room> roomList2 = member.getRoomList2();
        List<Room> roomList = new ArrayList<>();
        Collections.addAll(roomList, roomList1.toArray(new Room[0]));
        Collections.addAll(roomList, roomList2.toArray(new Room[0]));

        return roomList
                .stream()
                .sorted(Comparator.comparing(Room::getUpdatedAt).reversed())
                .map(room -> {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime dateTime = room.getUpdatedAt();
                    String updatedAt = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    long diff = ChronoUnit.DAYS.between(dateTime, now);

                    if (diff < 7) {
                        if (diff >= 1) updatedAt = diff + "일전";
                        else {
                            diff = ChronoUnit.HOURS.between(dateTime, now);
                            if (diff >= 1) updatedAt = diff + "시간전";
                            else {
                                diff = ChronoUnit.MINUTES.between(dateTime, now);
                                if (diff >= 1) updatedAt = diff + "분전";
                                else updatedAt = "방금전";
                            }
                        }
                    }

                    Member other = room.getMember1();
                    if (other.getMemberId().equals(member.getMemberId())) other = room.getMember2();

                    return new ChatDto.RoomResponse(room.getRoomId(), updatedAt,
                            new MemberDto.Other(other.getNickname(), other.getProfileUrl(),
                                    other.getDepth1(), other.getDepth2(), other.getDepth3()));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatDto.ChatResponse> getChatList(UUID roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new NotExistedRoomException("아이디로 조회되는 채팅 방이 존재하지 않습니다."))
                .getChatList()
                .stream()
                .sorted(Comparator.comparing(Chat::getCreatedAt))
                .map(chat -> new ChatDto.ChatResponse(chat.getChatId(),
                        chat.getContent(),
                        chat.getImgUrl(),
                        chat.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
