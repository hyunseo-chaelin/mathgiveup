package hanium.smath.notice.repository;

import hanium.smath.notice.entity.notice; // 수정됨
import org.springframework.data.jpa.repository.JpaRepository;

// 나머지 코드는 동일
public interface noticeRepository extends JpaRepository<notice, Long> {
}
