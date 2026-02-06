package com.thejoa703.service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.thejoa703.dto.request.PostRequestDto;
import com.thejoa703.dto.response.PostResponseDto;
import com.thejoa703.entity.AppUser;
import com.thejoa703.entity.Hashtag;
import com.thejoa703.entity.Image;
import com.thejoa703.entity.Post;
import com.thejoa703.repository.AppUserRepository;
import com.thejoa703.repository.HashtagRepository;
import com.thejoa703.repository.PostRepository;
import com.thejoa703.repository.RetweetRepository; // ✅ 리트윗 레포지토리 추가
import com.thejoa703.util.FileStorageService;

import lombok.RequiredArgsConstructor;


/**
* 게시글 서비스
* - 게시글 작성, 조회, 수정, 삭제
* - 페이징 조회 및 해시태그 검색
* - 리트윗 수 포함
*/ 
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
	
    private final PostRepository postRepository; // 글게시글
    private final AppUserRepository userRepository;  // 유저 레파지토리
    private final HashtagRepository hashtagRepository; // 해쉬태그 레파지토리
    private final FileStorageService fileStorageService; // 파일스토리지 - 업로드
    private final RetweetRepository retweetRepository;  // 리트윗
 
    ///// 게시글작성 - 이미지업로드 , 해쉬태그작성, 글작성
    public PostResponseDto createPost(Long userId, PostRequestDto dto, List<MultipartFile> files) {
        AppUser user = userRepository.findById(userId)
                       .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
		Post post = new Post();
		post.setContent(  dto.getContent()  );
		post.setUser(user);
        
    		// 이미지 업로드
        if (files != null && !files.isEmpty()) {
            files.forEach(file -> {
                String url = fileStorageService.upload(file);
                Image image = new Image();
                image.setSrc(url);
                image.setPost(post);
                post.getImages().add(image);
            });
        } 
		// 해쉬태그작성
        if (dto.getHashtags() != null && !dto.getHashtags().isEmpty()) {
            Set<String> distinctTags = Arrays.stream(dto.getHashtags().split(","))
                    .map(String::trim)   // 양쪽공백빼기
                    .filter(s -> !s.isEmpty())  // 비게아니라면
                    .collect(Collectors.toSet());

            distinctTags.forEach(tagStr -> {
                String normalized = tagStr.startsWith("#") ? tagStr.substring(1) : tagStr;
                Hashtag tag = hashtagRepository.findByName(normalized)
                        .orElseGet(() -> {
                            Hashtag newTag = new Hashtag();
                            newTag.setName(normalized);
                            return hashtagRepository.save(newTag);
                        });
                post.getHashtags().add(tag);
            });
        }    	
		 
    		// 글작성
        Post saved = postRepository.save( post );
        PostResponseDto dtoResponse = PostResponseDto.from(saved);
        dtoResponse.setRetweetCount(retweetRepository.countByOriginalPostId(saved.getId()));  // 리트윗처리
    		return dtoResponse;
    }
    
    
    // 단일 게시글 조회
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId) {  // 해당하는 글번호 받아서 
        Post post = postRepository.findById(postId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        
        PostResponseDto dto = PostResponseDto.from(post);  // 이미지, 해쉬태그, 좋아요.... 묶음
        dto.setRetweetCount(retweetRepository.countByOriginalPostId(post.getId()));  // 리트윗처리
        return dto;
    }
    // 전체게시글 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts() { // 삭제가 안된글들
        return postRepository.findByDeletedFalse().stream()
                .map(post -> {
                    PostResponseDto dto = PostResponseDto.from(post);
                    dto.setRetweetCount(retweetRepository.countByOriginalPostId(post.getId())); 
                    return dto;
                })
                .collect(Collectors.toList());
    }
 
    // 전체게시글 조회 페이징들어감.
    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPostsPaged(int page, int size) {  // 현재페이지 1, 몇개씩 10
        int start = (page - 1) * size + 1;  // 1(START)~10(END)
        int end = page * size;  //10
        List<Post> posts = postRepository.findPostsWithPaging(start, end);

        return posts.stream()
                .map(post -> {
                    PostResponseDto dto = PostResponseDto.from(post);
                    dto.setRetweetCount(retweetRepository.countByOriginalPostId(post.getId()));  
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    // 특정유저가 좋아요한 게시글 페이징 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getLikedPostsPaged(Long userId, int page, int size) {  // 현재페이지 1, 몇개씩 10
        int start = (page - 1) * size + 1;  // start
        int end = page * size; // end
        List<Post> posts = postRepository.findLikedPostsWithPaging(userId, start, end);

        return posts.stream()
                .map(post -> {
                    PostResponseDto dto = PostResponseDto.from(post);
                    dto.setRetweetCount(retweetRepository.countByOriginalPostId(post.getId())); // ✅ 리트윗 수 포함
                    return dto;
                })
                .collect(Collectors.toList());  // 리스트
    }
    // 내가쓴글 + 리트윗
    @Transactional(readOnly = true)
    public List<PostResponseDto> getMyPostsAndRetweetsPaged(Long userId, int page, int size) {
        int start = (page - 1) * size + 1;
        int end = page * size;
        // ✅ 변경: PostRepository에 추가한 UNION ALL 쿼리 호출
        List<Post> posts = postRepository.findMyPostsAndRetweetsWithPaging(userId, start, end);

        return posts.stream()
                .map(post -> {
                    PostResponseDto dto = PostResponseDto.from(post);
                    dto.setRetweetCount(retweetRepository.countByOriginalPostId(post.getId())); // ✅ 리트윗 수 포함
                    return dto;
                })
                .collect(Collectors.toList());
    }
 
    // 해쉬태그검색
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsByHashtag(String hashtag) {
        String normalized = hashtag.startsWith("#") ? hashtag.substring(1) : hashtag;
        List<Post> posts = postRepository.findByHashtags_NameAndDeletedFalse(normalized);

        return posts.stream()
                .map(post -> {
                    PostResponseDto dto = PostResponseDto.from(post);
                    dto.setRetweetCount(retweetRepository.countByOriginalPostId(post.getId())); // ✅ 리트윗 수 포함
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    // 게시글수정
    public PostResponseDto updatePost(Long userId, Long postId, PostRequestDto dto, List<MultipartFile> files) {
        Post post = postRepository.findById(postId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("본인 글만 수정할 수 있습니다.");
        }
        // 내용수정
        post.setContent(dto.getContent());
 
        // 이미지 갱신 로직
        if (files != null && !files.isEmpty()) {
            post.getImages().clear();  //초기화
            files.forEach(file -> {
                String url = fileStorageService.upload(file);
                Image image = new Image();
                image.setSrc(url);
                image.setPost(post);
                post.getImages().add(image);
            });
        } 
        // 해쉬태그  갱신
        post.getHashtags().clear();
        if (dto.getHashtags() != null && !dto.getHashtags().isEmpty()) {
            Set<String> distinctTags = Arrays.stream(dto.getHashtags().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());

            distinctTags.forEach(tagStr -> {
                String normalized = tagStr.startsWith("#") ? tagStr.substring(1) : tagStr;
                Hashtag tag = hashtagRepository.findByName(normalized)
                        .orElseGet(() -> {
                            Hashtag newTag = new Hashtag();
                            newTag.setName(normalized);
                            return hashtagRepository.save(newTag);
                        });
                post.getHashtags().add(tag);
            });
        }
        // 글 수정
        Post updated = postRepository.save(post);
        PostResponseDto dtoResponse = PostResponseDto.from(updated);
        dtoResponse.setRetweetCount(retweetRepository.countByOriginalPostId(updated.getId())); 
        return dtoResponse;
    }
    // 게시글삭제
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("본인 글만 삭제할 수 있습니다.");
        }
        post.setDeleted(true);
        postRepository.save(post);
    }
    // 전체게시글 수
    @Transactional(readOnly = true)
    public long countPosts() {
        return postRepository.count();
    }
}

