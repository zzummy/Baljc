package com.baljc.api.service;

import com.baljc.api.dto.BoardDto;
import com.baljc.db.entity.*;
import com.baljc.db.repository.*;
import com.baljc.exception.NotExistedAccountBookException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final MemberService memberService;
    private final FileService fileService;
    private final BoardCategoryRepository boardCategoryRepository;
    private final BoardRepository boardRepository;
    private final BoardImgRepository boardImgRepository;
    private final CommentRepository commentRepository;
    private final HeartRepository heartRepository;
    private final ScrapRepository scrapRepository;
    private final String boardImagePath;

    public BoardServiceImpl(MemberService memberService,
                            FileService fileService,
                            BoardCategoryRepository boardCategoryRepository,
                            BoardRepository boardRepository,
                            BoardImgRepository boardImgRepository,
                            CommentRepository commentRepository,
                            HeartRepository heartRepository,
                            ScrapRepository scrapRepository,
                            @Value("${cloud.aws.s3.folder.boardImage}") String boardImagePath
    ) {
        this.memberService = memberService;
        this.fileService = fileService;
        this.boardCategoryRepository = boardCategoryRepository;
        this.boardRepository = boardRepository;
        this.boardImgRepository = boardImgRepository;
        this.commentRepository = commentRepository;
        this.heartRepository = heartRepository;
        this.scrapRepository = scrapRepository;
        this.boardImagePath = boardImagePath;
    }

    @Override
    public List<BoardDto.BoardCategoryResponse> getBoardCategory() {
        List<BoardCategory> list = boardCategoryRepository.findAll();
        List<BoardDto.BoardCategoryResponse> boardCategoryResponse = list.stream()
                .map(category -> new BoardDto.BoardCategoryResponse(category.getBoardCategoryId(), category.getImgUrl(), category.getName()))
                .collect(Collectors.toList());

        return boardCategoryResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBoard(BoardDto.BoardRequest boardRequest, List<MultipartFile> files) {
        Member member = memberService.getMemberByAuthentication();
        BoardCategory category = boardCategoryRepository.getById(boardRequest.getCategoryId());

        Board board = Board.builder()
                    .content(boardRequest.getContent())
                    .member(member)
                    .place(boardRequest.getPlace())
                    .boardCategory(category)
                    .latitude(member.getLatitude())
                    .longitude(member.getLongitude())
                    .dong(member.getDong())
                    .deletedYn('N')
                    .build();
        boardRepository.save(board);

        if (files != null) {
            for (MultipartFile multipartFile : files) {
                String imgUrl = fileService.uploadImage(multipartFile, boardImagePath);

                boardImgRepository.save(BoardImg.builder()
                        .board(board)
                        .imgUrl(imgUrl)
                        .deletedYn('N')
                        .build());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertComment(UUID boardId, BoardDto.CommentRequest commentRequest) {
        Member member = memberService.getMemberByAuthentication();
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("해당 게시글이 존재하지 않습니다."));
        Comment comment = null;
        if (commentRequest.getParentId() != null) {
            comment = commentRepository.findById(commentRequest.getParentId()).orElseThrow(() -> new NullPointerException("해당 부모 댓글이 존재하지 않습니다."));
        }

        commentRepository.save(Comment.builder()
                .board(board)
                .member(member)
                .comment(comment)
                .content(commentRequest.getContent())
                .deletedYn('N')
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NullPointerException("해당 댓글이 존재하지 않습니다."));
        comment.deleteComment();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHeart(UUID boardId, BoardDto.HeartRequest heartRequest) {
        Member member = memberService.getMemberByAuthentication();
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("해당 게시글이 존재하지 않습니다."));

        if (heartRequest.getHeartYn().charAt(0) == 'Y') {
            heartRepository.save(Heart.builder()
                    .member(member)
                    .board(board)
                    .build());
        } else {
            Heart heart = heartRepository.findByMemberAndBoard(member, board).orElseThrow(() -> new NullPointerException("해당 좋아요가 존재하지 않습니다."));
            heartRepository.deleteById(heart.getHeartId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateScrap(UUID boardId, BoardDto.ScrapRequest scrapRequest) {
        Member member = memberService.getMemberByAuthentication();
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("해당 게시글이 존재하지 않습니다."));

        if (scrapRequest.getScrapYn().charAt(0) == 'Y') {
            scrapRepository.save(Scrap.builder()
                    .member(member)
                    .board(board)
                    .build());
        } else {
            Scrap scrap = scrapRepository.findByMemberAndBoard(member, board).orElseThrow(() -> new NullPointerException("해당 스크랩이 존재하지 않습니다."));
            scrapRepository.deleteById(scrap.getScrapId());
        }
    }

}
