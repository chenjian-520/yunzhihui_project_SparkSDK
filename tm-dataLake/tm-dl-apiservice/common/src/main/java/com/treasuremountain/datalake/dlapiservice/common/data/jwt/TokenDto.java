package com.treasuremountain.datalake.dlapiservice.common.data.jwt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Description:
 * <p>
 * Created by xun-yu.she on 2019/4/1.
 * Company: Foxconn
 * Project: MaxIoT
 */
@Data
@ApiModel(description = "token信息")
@NoArgsConstructor
@ToString
public class TokenDto {
    @ApiModelProperty(value = "token", required = true)
    private String token;
    @ApiModelProperty(value = "refreshToken", required = true)
    private String refreshToken;

}
