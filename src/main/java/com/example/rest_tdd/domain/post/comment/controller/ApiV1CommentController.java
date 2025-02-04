package com.example.rest_tdd.domain.post.comment.controller;

import com.example.rest_tdd.domain.member.member.entity.Member;
import com.example.rest_tdd.domain.post.comment.dto.CommentDto;
import com.example.rest_tdd.domain.post.comment.entity.Comment;
import com.example.rest_tdd.domain.post.post.entity.Post;
import com.example.rest_tdd.domain.post.post.service.PostService;
import com.example.rest_tdd.global.Rq;
import com.example.rest_tdd.global.dto.RsData;
import com.example.rest_tdd.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class ApiV1CommentController {

    private final PostService postService;
    private final Rq rq;

    @GetMapping
    public List<CommentDto> getItems(@PathVariable long postId) {
        Post post = postService.getItem(postId).orElseThrow(() ->
                new ServiceException("404-1", "존재하지 않는 게시글입니다."
                )
        );
        return post.getComments()
                .stream()
                .map(CommentDto::new)
                .toList();
    }

    @GetMapping("{commentId}")
    public CommentDto getItem(
            @PathVariable long postId,
            @PathVariable long commentId
    ) {
        Post post = postService.getItem(postId).orElseThrow(() ->
                new ServiceException("404-1", "존재하지 않는 게시글입니다."
                )
        );
        Comment comment = post.getCommentById(commentId);
        return new CommentDto(comment);

    }

    record WriteReqBody(String content) {}

    @PostMapping
    public RsData<Void> write(
            @PathVariable
            long postId,
            @RequestBody
            WriteReqBody reqBody
            ) {
        Member actor = rq.getAuthenticatedActor();
        Comment comment = _write(postId, actor, reqBody.content);

        //커멘트가 등록되지 않고 프록시로 존재해 아이디가 없음
        //그래서 바로 db에 반영하도록 함
        postService.flush();

        return new RsData<>(
                "200-1",
                "%d번 댓글 작성이 완료되었습니다.".formatted(comment.getId())
        );
    }

    public Comment _write(long postId, Member actor, String content) {
        Post post = postService.getItem(postId).orElseThrow(() ->
                new ServiceException("404-1", "존재하지 않는 게시글입니다."
                )
        );
        return post.addComment(actor, content);
    }

    record ModifyReqBody(String content) {}

    @PutMapping("{id}")
    @Transactional
    public RsData<Void> modify(
            @PathVariable
            long postId,
            @PathVariable
            long id,
            @RequestBody
            ModifyReqBody reqBody
    ) {
        Member actor = rq.getAuthenticatedActor();

        Post post = postService.getItem(postId).orElseThrow(() ->
                new ServiceException("404-1", "존재하지 않는 게시글입니다."
                )
        );

        Comment comment = post.getCommentById(id);

        comment.canModify(actor);
        comment.modify(reqBody.content());

        return new RsData<>(
                "200-1",
                "%d번 댓글이 수정되었습니다.".formatted(comment.getId())
        );
    }

    @DeleteMapping("{id}")
    @Transactional
    public RsData<Void> delete(
            @PathVariable
            long postId,
            @PathVariable
            long id
    ) {
        Member actor = rq.getAuthenticatedActor();

        Post post = postService.getItem(postId).orElseThrow(() ->
                new ServiceException("404-1", "존재하지 않는 게시글입니다."
                )
        );

        Comment comment = post.getCommentById(id);
        comment.canDelete(actor);

        post.deleteComment(comment);
        return new RsData<>(
                "200-1",
                "%d번 댓글 삭제가 완료되었습니다.".formatted(id)
        );
    }

}


