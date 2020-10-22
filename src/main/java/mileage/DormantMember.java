package mileage;

import javax.persistence.*;

import org.springframework.beans.BeanUtils;


@Entity
@Table(name = "DormantMember_table")
public class DormantMember {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long memberId;
    private String phoneNo;
    private String memberStatus;
    private String nickname;

    @PostPersist
    public void onPostPersist() {
        DormantMemberInserted dormantMemberInserted = new DormantMemberInserted();
        BeanUtils.copyProperties(this, dormantMemberInserted);

        dormantMemberInserted.setMemberStatus("Pre-Dormant");
        dormantMemberInserted.publishAfterCommit();

    }

    @PostUpdate
    public void onPostUpdate() {
        if (this.getMemberStatus().equals("DORMANT")) {
            DormantMemberChanged dormantMemberChanged = new DormantMemberChanged();
            BeanUtils.copyProperties(this, dormantMemberChanged);

            System.out.println("### 1 ##");

            dormantMemberChanged.publishAfterCommit();

        } else if (this.getMemberStatus().equals("DESTRUCTION")) {
            DormantStatusUpdated dormantStatusUpdated = new DormantStatusUpdated();
            BeanUtils.copyProperties(this, dormantStatusUpdated);

            System.out.println("### 2 ##");

            dormantStatusUpdated.publishAfterCommit();

        }

    }

    @PreUpdate
    public void onPreUpdate() {

        if (this.getMemberStatus().equals("CLEAR")) {
            DormantMemberCleared dormantMemberCleared = new DormantMemberCleared();
            BeanUtils.copyProperties(this, dormantMemberCleared);
            dormantMemberCleared.setMemberStatus("CLEAR-DONE");
            dormantMemberCleared.publishAfterCommit();

            //Following code causes dependency to external APIs
            // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

            mileage.external.Member member = new mileage.external.Member();
            // mappings goes here

            member.setMemberId(this.getMemberId());
            member.setNickname(this.getNickname());
            member.setPhoneNo(this.getPhoneNo());
            member.setMemberStatus("READY");

            DormantmemberApplication.applicationContext.getBean(mileage.external.MemberService.class)
                    .join(member);
        }else {

            System.out.println("## 3 ###");

        }

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


}
