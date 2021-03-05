package Manage.model.bo;

import Core.model.VoObject;
import Core.util.AES;
import Core.util.Common;
import Core.util.SHA256;
import Manage.model.po.MemberPo;
import Manage.model.vo.MemberInfoRetVo;
import Manage.model.vo.MemberVo;
import lombok.Data;

import java.io.Serializable;

/**
 * author snow create 2021/02/14 21:00
 */
@Data
public class Member extends User implements VoObject, Serializable {
    private Long memberId;
    private Long creatorId;
    private String identityId;
    private Integer birthYear;
    private Byte birthMonth;
    private Byte birthday;
    private String address;
    private String history;
    private String comment;
    private String signature;

    public Member(MemberPo memberPo){
        this.id = memberPo.getId();
        this.memberId = memberPo.getMemberId();
        this.creatorId = memberPo.getCreatorId();
        this.identityId = memberPo.getIdentityId();
        this.realName = memberPo.getName();
        this.gender = memberPo.getGender();
        this.birthYear = memberPo.getBirthYear();
        this.birthMonth = memberPo.getBirthMonth();
        this.birthday = memberPo.getBirthday();
        this.email = memberPo.getEmail();
        this.mobile = memberPo.getMobile();
        this.address = memberPo.getAddress();
        this.history = memberPo.getHistory();
        this.comment = memberPo.getComment();
        this.signature = memberPo.getSignature();
    }

    public Member(MemberVo memberVo){
        this.realName = memberVo.getName();
        this.memberId = memberVo.getMemberId();
        this.gender = memberVo.getGender();
        if(memberVo.getDate() != null){
            String[] dates = memberVo.getDate().split("-");
            if(dates.length == 3) {
                this.birthYear = Integer.parseInt(dates[0]);
                this.birthMonth = Byte.parseByte(dates[1]);
                this.birthday = Byte.parseByte(dates[2]);
            }
        }
        this.email = AES.encrypt(memberVo.getEmail(), AES_PASS);
        this.mobile = AES.encrypt(memberVo.getMobile(), AES_PASS);
        this.address = AES.encrypt(memberVo.getAddress(), AES_PASS);
        this.history = AES.encrypt(memberVo.getHistory(), AES_PASS);
        this.identityId = AES.encrypt(memberVo.getIdentityId(), AES_PASS);
        this.comment = memberVo.getComment();
        this.signature = createSignature();
    }

    public MemberPo createMemberPo(){
        MemberPo memberPo = new MemberPo();
        memberPo.setId(this.id);
        memberPo.setMemberId(this.memberId);
        memberPo.setIdentityId(this.identityId);
        memberPo.setCreatorId(this.creatorId);
        memberPo.setName(this.realName);
        memberPo.setGender(this.gender);
        memberPo.setBirthYear(this.birthYear);
        memberPo.setBirthMonth(this.birthMonth);
        memberPo.setBirthday(this.birthday);
        memberPo.setEmail(this.email);
        memberPo.setMobile(this.mobile);
        memberPo.setAddress(this.address);
        memberPo.setHistory(this.history);
        memberPo.setComment(this.comment);
        memberPo.setSignature(this.signature);
        return memberPo;
    }

    /**
     * 生成签名
     * @author snow create 2021/02/14 21:26
     * @return
     */
    public String createSignature(){
        StringBuilder signature = Common.concatString("-", this.realName, this.identityId,
                this.gender.toString(), this.email, this.mobile, this.address, this.history);
        return SHA256.getSHA256(signature.toString());
    }

    /**
     * 判断签名是否被篡改
     * @author snow create 2021/01/19 00:26
     * @return
     */
    public Boolean isSignatureBeenModify(){
        if(this.signature.equals(createSignature())){
            return false;
        }
        return true;
    }

    public Boolean authentic(){
        return true;
    }

    public String getDecryptAddress(){
        if(this.address != null) {
            return AES.decrypt(this.address, AES_PASS);
        }
        return null;
    }

    public String getDecryptHistory(){
        if(this.history != null) {
            return AES.decrypt(this.history, AES_PASS);
        }
        return null;
    }

    public String getDecryptIdentityId(){
        if(this.identityId != null) {
            return AES.decrypt(this.identityId, AES_PASS);
        }
        return null;
    }

    @Override
    public Object createVo() {
        return new MemberInfoRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

}
