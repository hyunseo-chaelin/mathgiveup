package hanium.smath.notice.controller;

import hanium.smath.notice.dto.noticeRequest;
import hanium.smath.notice.dto.noticeResponse; // NoticeResponse 추가
import hanium.smath.notice.entity.notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notices")
public class noticeController {

    private final hanium.smath.notice.service.noticeService noticeService;

    @Autowired
    public noticeController(hanium.smath.notice.service.noticeService noticeService) {
        this.noticeService = noticeService;
    }

    // 공지사항 조회 (NoticeResponse로 변환하여 반환)
    @GetMapping
    public ResponseEntity<List<noticeResponse>> getAllNotices() {
        List<noticeResponse> noticeRespons = noticeService.findAll()
                .stream()
                .map(notice -> new noticeResponse(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getContent(),
                        "관리자", // 닉네임을 "관리자"로 고정
                        notice.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(noticeRespons);
    }

    // 공지사항 작성 (관리자만 가능)
    @PostMapping("/new")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<noticeResponse> createNotice(@RequestBody noticeRequest noticeRequest, org.springframework.security.core.Authentication authentication) {
        // 인증된 사용자 정보 가져오기
        if (authentication.getPrincipal() instanceof UserDetails) {
            // UserDetails 객체에서 로그인 ID를 가져옴
            String author = ((UserDetails) authentication.getPrincipal()).getUsername();

            // Notice 생성 시 author 설정
            notice notice = noticeService.create(noticeRequest, author);

            // NoticeResponse로 변환하여 반환
            noticeResponse response = new noticeResponse(
                    notice.getId(),
                    notice.getTitle(),
                    notice.getContent(),
                    "관리자", // 닉네임을 "관리자"로 고정
                    notice.getCreatedAt()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 공지사항 수정 (관리자만 가능)
    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<noticeResponse> updateNotice(@PathVariable Long id, @RequestBody noticeRequest noticeRequest) {
        notice updatedNotice = noticeService.update(id, noticeRequest);

        // NoticeResponse로 변환하여 반환
        noticeResponse response = new noticeResponse(
                updatedNotice.getId(),
                updatedNotice.getTitle(),
                updatedNotice.getContent(),
                "관리자", // 닉네임을 "관리자"로 고정
                updatedNotice.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    // 공지사항 삭제 (관리자만 가능)
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
