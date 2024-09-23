package hanium.smath.Notice.repository;

import hanium.smath.Notice.entity.Notice; // 수정됨
import org.springframework.data.jpa.repository.JpaRepository;

// 나머지 코드는 동일
public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
