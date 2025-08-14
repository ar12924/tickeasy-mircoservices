package microservices.member.vo;

import java.sql.Timestamp;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "VERIFICATION_TOKEN")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TOKEN_ID")
    private Integer tokenId;

    @Column(name = "TOKEN_NAME", unique = true)
    private String tokenName;

    @Column(name = "TOKEN_TYPE")
    private String tokenType;

    @Column(name = "EXPIRED_TIME")
    private Timestamp expiredTime;

    @CreationTimestamp
    @Column(name = "CREATED_TIME")
    private Timestamp createdTime;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", referencedColumnName = "MEMBER_ID")
    private Member member;
}


