package Manage.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MemberVo {

    @ApiModelProperty(value = "会员名")
    private String name;

    @ApiModelProperty(value = "会员卡号")
    private Long memberId;

    @ApiModelProperty(value = "身份证号")
    private String identityId;

    @ApiModelProperty(value = "性别")
    private Byte gender;

    @ApiModelProperty(value = "生日")
    private String date;

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
}
