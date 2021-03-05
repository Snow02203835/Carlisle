package Manage.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserVo {

    @ApiModelProperty(value = "用户名")
    @NotNull
    private String userName;

    @ApiModelProperty(value = "密码")
    @NotNull
    private String password;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "性别")
    private Byte gender;

    @ApiModelProperty(value = "邮箱")
    @NotNull
    private String email;

    @ApiModelProperty(value = "电话号码")
    @NotNull
    private String mobile;

}
