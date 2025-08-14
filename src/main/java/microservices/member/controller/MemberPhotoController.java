package microservices.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import microservices.member.service.S3PhotoService;
import microservices.member.dao.MemberDao;
import microservices.member.vo.Member;

@RestController
@RequestMapping("/api/member-photos")
public class MemberPhotoController {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private S3PhotoService s3PhotoService;

    @GetMapping("/{memberId}")
    public ResponseEntity<String> getPhoto(@PathVariable("memberId") Integer memberId) {
        Member member = memberDao.findById(memberId);
        if (member == null) {
            return ResponseEntity.notFound().build();
        }
        if (member.getPhotoKey() == null || member.getPhotoKey().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        var url = s3PhotoService.generateReadUrl(member.getPhotoKey(), java.time.Duration.ofMinutes(10));
        return ResponseEntity.ok(url.toString());
    }
}


