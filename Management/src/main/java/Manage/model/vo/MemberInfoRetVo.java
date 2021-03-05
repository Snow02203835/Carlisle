package Manage.model.vo;

import Manage.model.bo.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MemberInfoRetVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "会员id")
    private Long memberId;

    @ApiModelProperty(value = "会员名")
    private String name;

    @ApiModelProperty(value = "身份证号")
    private String identityId;

    @ApiModelProperty(value = "创建者id")
    private Long creatorId;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "出生年月日")
    private String birthday;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "电话号码")
    private String mobile;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "住房历史")
    private String history;

    @ApiModelProperty(value = "备注")
    private String comment;

    public MemberInfoRetVo(Member member){
        this.id = member.getId();
        this.memberId = member.getMemberId();
        this.name = member.getRealName();
        this.creatorId = member.getCreatorId();
        if(member.getGender() == 0){
            this.gender = "女";
        }
        else if(member.getGender() == 1){
            this.gender = "男";
        }
        if(member.getBirthYear() != null && member.getBirthMonth() != null && member.getBirthday() != null) {
            this.birthday = member.getBirthYear().toString() + "年" +
                    (member.getBirthMonth() < 10 ? "0" : "") + member.getBirthMonth() + "月" +
                    (member.getBirthday() < 10 ? "0" : "") + member.getBirthday() + "日";
        }
        this.email = member.getDecryptEmail();
        this.mobile = member.getDecryptMobile();
        this.address = member.getDecryptAddress();
        this.history = member.getDecryptHistory();
        this.identityId = member.getDecryptIdentityId();
        this.comment = member.getComment();
    }
}
