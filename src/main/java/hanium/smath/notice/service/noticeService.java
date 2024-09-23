package hanium.smath.notice.service;

import hanium.smath.notice.entity.notice; // 수정됨
import hanium.smath.notice.dto.noticeRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class noticeService {

    private final hanium.smath.notice.repository.noticeRepository noticeRepository;

    public noticeService(hanium.smath.notice.repository.noticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Transactional(readOnly = true)
    public List<notice> findAll() {
        return noticeRepository.findAll();
    }

    @Transactional
    public notice create(noticeRequest noticeRequest, String author) {
        notice notice = new notice();
        notice.setTitle(noticeRequest.getTitle());
        notice.setContent(noticeRequest.getContent());
        notice.setAuthor(author); // author 설정

        return noticeRepository.save(notice);
    }


    @Transactional
    public notice update(Long id, noticeRequest noticeRequest) {
        Optional<notice> optionalNotice = noticeRepository.findById(id);
        if (optionalNotice.isPresent()) {
            notice notice = optionalNotice.get();
            notice.setTitle(noticeRequest.getTitle());
            notice.setContent(noticeRequest.getContent());
            return noticeRepository.save(notice);
        } else {
            throw new RuntimeException("Notice not found with id: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        Optional<notice> optionalNotice = noticeRepository.findById(id);
        if (optionalNotice.isPresent()) {
            noticeRepository.delete(optionalNotice.get());
        } else {
            throw new RuntimeException("Notice not found with id: " + id);
        }
    }
}