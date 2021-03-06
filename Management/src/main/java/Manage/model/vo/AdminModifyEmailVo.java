package Manage.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author snow create 2021/01/23 16:47
 */
@Data
public class AdminModifyEmailVo {

    @ApiModelProperty(value = "验证码")
    @NotNull
    private String key;

    @ApiModelProperty(value = "新邮箱")
    @NotNull
    private String email;
}
