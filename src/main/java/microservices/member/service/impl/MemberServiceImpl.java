package microservices.member.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import microservices.member.dao.MemberDao;
import microservices.member.dao.VerificationDao;
import microservices.member.service.MemberService;
import microservices.member.service.MailService;
import microservices.member.vo.Member;
import microservices.member.vo.VerificationToken;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberDao memberDao;
    private final VerificationDao verificationDao;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    @Autowired
    public MemberServiceImpl(MemberDao memberDao,
                             VerificationDao verificationDao,
                             MailService mailService,
                             PasswordEncoder passwordEncoder,
                             RestTemplate restTemplate) {
        this.memberDao = memberDao;
        this.verificationDao = verificationDao;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
    }

    @Override
    public Member register(Member member) {
        Member exists = memberDao.findByUserName(member.getUserName());
        if (exists != null) {
            member.setSuccessful(false);
            member.setMessage("帳號已存在");
            return member;
        }
        member.setIsActive(0);
        if (member.getPassword() != null) {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
        }
        boolean ok = memberDao.insert(member);
        if (!ok) {
            member.setSuccessful(false);
            member.setMessage("註冊失敗");
            return member;
        }
        sendVerificationMail(member);
        member.setSuccessful(true);
        member.setMessage("註冊成功，驗證信已寄出");
        return member;
    }

    @Override
    public Member editMember(Member member) {
        boolean ok = memberDao.update(member);
        member.setSuccessful(ok);
        member.setMessage(ok ? "更新成功" : "更新失敗");
        return member;
    }

    @Override
    public Member login(Member member) {
        Member db = memberDao.findByUserName(member.getUserName());
        if (db == null || db.getPassword() == null || member.getPassword() == null ||
                !passwordEncoder.matches(member.getPassword(), db.getPassword())) {
            Member resp = new Member();
            resp.setSuccessful(false);
            resp.setMessage("帳號或密碼錯誤");
            return resp;
        }
        db.setSuccessful(true);
        db.setMessage("登入成功");
        return db;
    }

    @Override
    public Member getById(Integer memberId, Member loginMember) {
        return memberDao.findById(memberId);
    }

    @Override
    public Member getByUsername(String username) {
        return memberDao.findByUserName(username);
    }

    @Override
    public Member getByEmail(String email) {
        return memberDao.findByEmail(email);
    }

    @Override
    public String getRoleById(Integer memberId) {
        Member m = memberDao.findById(memberId);
        if (m == null || m.getRoleLevel() == null) return "USER";
        return m.getRoleLevel() >= 9 ? "ADMIN" : "USER";
    }

    @Override
    public boolean removeMemberById(Integer memberId) {
        return memberDao.delete(memberId);
    }

    @Override
    public boolean activateMemberByToken(String tokenStr) {
        VerificationToken t = verificationDao.findByToken(tokenStr);
        if (t == null) return false;
        if (t.getExpiredTime() != null && t.getExpiredTime().before(new Timestamp(System.currentTimeMillis()))) {
            verificationDao.deleteById(t.getTokenId());
            return false;
        }
        microservices.member.vo.Member em = t.getMember();
        if (em == null) return false;
        Member m = new Member();
        m.setMemberId(em.getMemberId());
        m.setUserName(em.getUserName());
        if (m == null) return false;
        m.setIsActive(1);
        memberDao.update(m);
        verificationDao.deleteById(t.getTokenId());
        return true;
    }

    @Override
    public Member requestPasswordResetByEmail(String email) {
        Member m = memberDao.findByEmail(email);
        if (m == null) {
            Member resp = new Member();
            resp.setSuccessful(false);
            resp.setMessage("查無此 Email");
            return resp;
        }
        String tokenName = createToken("RESET");
        VerificationToken token = buildToken(tokenName, "RESET_PASSWORD", m, 3600);
        verificationDao.insert(token);
        // 呼叫 notification-service 發送重置密碼信
        try {
            var url = System.getenv().getOrDefault("NOTIFY_ENDPOINT", "http://localhost:8080/api/notifications/send");
            var payload = new java.util.HashMap<String, Object>();
            payload.put("type", "EMAIL");
            payload.put("to", m.getEmail());
            payload.put("subject", "TickEasy - 密碼重設通知");
            payload.put("body", String.format("%s 您好，請以此連結重置密碼: %s", m.getUserName(), tokenName));
            restTemplate.postForEntity(url, payload, Void.class);
        } catch (Exception ignore) {}
        m.setSuccessful(true);
        m.setMessage("重設密碼信已寄出");
        return m;
    }

    @Override
    public Member sendPasswordUpdateMail(Member member, String newPassword) {
        String tokenName = createToken("UPDATE");
        VerificationToken token = buildToken(tokenName, "UPDATE_PASSWORD", member, 3600);
        verificationDao.insert(token);
        try {
            var url = System.getenv().getOrDefault("NOTIFY_ENDPOINT", "http://localhost:8080/api/notifications/send");
            var payload = new java.util.HashMap<String, Object>();
            payload.put("type", "EMAIL");
            payload.put("to", member.getEmail());
            payload.put("subject", "TickEasy - 密碼更新確認");
            payload.put("body", String.format("%s 您好，請以此連結確認密碼更新: %s", member.getUserName(), tokenName));
            restTemplate.postForEntity(url, payload, Void.class);
        } catch (Exception ignore) {}
        member.setSuccessful(true);
        member.setMessage("密碼更新確認信已寄出");
        return member;
    }

    @Override
    public Member resetPasswordByToken(String token, String newPassword) {
        VerificationToken t = verificationDao.findByToken(token);
        Member resp = new Member();
        if (t == null || t.getMember() == null) {
            resp.setSuccessful(false);
            resp.setMessage("無效的連結");
            return resp;
        }
        if (t.getExpiredTime() != null && t.getExpiredTime().before(Timestamp.from(Instant.now()))) {
            verificationDao.deleteById(t.getTokenId());
            resp.setSuccessful(false);
            resp.setMessage("連結已過期");
            return resp;
        }
        microservices.member.vo.Member em = t.getMember();
        Member m = new Member();
        if (em != null) {
            m.setMemberId(em.getMemberId());
            m.setUserName(em.getUserName());
        }
        m.setPassword(passwordEncoder.encode(newPassword));
        memberDao.update(m);
        verificationDao.deleteById(t.getTokenId());
        m.setSuccessful(true);
        m.setMessage("密碼已更新");
        return m;
    }

    @Override
    public Member resendVerificationMail(String email) {
        Member m = memberDao.findByEmail(email);
        if (m == null) {
            Member resp = new Member();
            resp.setSuccessful(false);
            resp.setMessage("查無此 Email");
            return resp;
        }
        return sendVerificationMail(m);
    }

    @Override
    public Member sendVerificationMail(Member member) {
        String tokenName = createToken("VERIFY");
        VerificationToken token = buildToken(tokenName, "EMAIL_VERIFY", member, 24 * 3600);
        verificationDao.insert(token);
        try {
            var url = System.getenv().getOrDefault("NOTIFY_ENDPOINT", "http://localhost:8080/api/notifications/send");
            var payload = new java.util.HashMap<String, Object>();
            payload.put("type", "EMAIL");
            payload.put("to", member.getEmail());
            payload.put("subject", "歡迎加入 TickEasy - 請驗證您的帳號");
            payload.put("body", String.format("%s 您好，請以此連結完成驗證: %s", member.getUserName(), tokenName));
            restTemplate.postForEntity(url, payload, Void.class);
        } catch (Exception ignore) {}
        member.setSuccessful(true);
        member.setMessage("驗證信已寄出");
        return member;
    }

    @Override
    public Member updatePasswordAndDeleteToken(Member member, String newPassword, Integer tokenId) {
        member.setPassword(newPassword);
        memberDao.update(member);
        if (tokenId != null) {
            verificationDao.deleteById(tokenId);
        }
        member.setSuccessful(true);
        member.setMessage("密碼已更新");
        return member;
    }

    private String createToken(String prefix) {
        return prefix + "|" + UUID.randomUUID();
    }

    private VerificationToken buildToken(String tokenName, String type, Member member, long ttlSeconds) {
        VerificationToken t = new VerificationToken();
        t.setTokenName(tokenName);
        t.setTokenType(type);
        t.setExpiredTime(Timestamp.from(Instant.now().plusSeconds(ttlSeconds)));
        microservices.member.vo.Member entityMember = new microservices.member.vo.Member();
        entityMember.setMemberId(member.getMemberId());
        entityMember.setUserName(member.getUserName());
        t.setMember(entityMember);
        return t;
    }
}


