package Manage.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author snow create 2021/02/28 15:08
 */
@Data
public class AdminVerifyEmailVo {

    @ApiModelProperty(value = "用户名")
    @NotNull
    private String userName;

    @ApiModelProperty(value = "验证码")
    @NotNull
    private String verifyCode;
}
