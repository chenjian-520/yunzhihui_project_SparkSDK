package com.treasuremountain.datalake.dlapiservice.config.securityconfig.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.rest.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class ServerErrorResponseHandler implements AccessDeniedHandler/*ResponseEntityExceptionHandler*/ {

//    @Autowired
    private ObjectMapper mapper =new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException,
            ServletException {
        if (!response.isCommitted()) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            mapper.writeValue(response.getWriter(),
                    ServerErrorResponse.of("You don't have permission to perform this operation!",
                            EdgeServerErrorCode.PERMISSION_DENIED, HttpStatus.FORBIDDEN));
        }
    }

    public void handle(Exception exception, HttpServletResponse response) {
        if (!response.isCommitted()) {
            try {
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                if (exception instanceof ServerException) {
                    handleEdgeServerException((ServerException) exception, response);
                }else if (exception instanceof org.springframework.security.access.AccessDeniedException) {
                    handleAccessDeniedException((org.springframework.security.access.AccessDeniedException)exception,response);
                } else if (exception instanceof AuthenticationException) {
                    handleAuthenticationException((AuthenticationException) exception, response);
                } else {
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    mapper.writeValue(response.getWriter(), ServerErrorResponse.of(exception.getMessage(),
                            EdgeServerErrorCode.GENERAL, HttpStatus.INTERNAL_SERVER_ERROR));
                }
            } catch (IOException e) {
            }
        }
    }

    @ExceptionHandler(ServerException.class)
    private void handleEdgeServerException(ServerException polarisboardException, HttpServletResponse response) throws IOException {

        EdgeServerErrorCode errorCode = polarisboardException.getErrorCode();
        HttpStatus status;

        switch (errorCode) {
            case PERMISSION_DENIED:
                status = HttpStatus.FORBIDDEN;
                break;
            case INVALID_ARGUMENTS:
                status = HttpStatus.BAD_REQUEST;
                break;
            case ITEM_NOT_FOUND:
                status = HttpStatus.NOT_FOUND;
                break;
            case BAD_REQUEST_PARAMS:
                status = HttpStatus.BAD_REQUEST;
                break;
            case GENERAL:
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
            default:
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
        }
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(status.value());
        RestResult<ServerErrorResponse> responseRestResult=new RestResult<>();
        responseRestResult.setResult(false);
        responseRestResult.setMsg(polarisboardException.getMessage());
        responseRestResult.setData(ServerErrorResponse.of(polarisboardException.getMessage(), errorCode, status));
        mapper.writeValue(response.getWriter(), responseRestResult);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    private void handleAccessDeniedException(org.springframework.security.access.AccessDeniedException accessException, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        mapper.writeValue(response.getWriter(),
                ServerErrorResponse.of("You don't have permission to perform this operation!",
                        EdgeServerErrorCode.PERMISSION_DENIED, HttpStatus.FORBIDDEN));

    }

    @ExceptionHandler(AuthenticationException.class)
    private void handleAuthenticationException(AuthenticationException authenticationException, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        if (authenticationException instanceof BadCredentialsException) {
            mapper.writeValue(response.getWriter(), ServerErrorResponse.of("Invalid Token", EdgeServerErrorCode.JWT_TOKEN_INVALID, HttpStatus.UNAUTHORIZED));
        } else if (authenticationException instanceof JwtExpiredTokenException) {
            mapper.writeValue(response.getWriter(), ServerErrorResponse.of("Token has expired", EdgeServerErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED));
        } else if (authenticationException instanceof AuthMethodNotSupportedException) {
            mapper.writeValue(response.getWriter(), ServerErrorResponse.of(authenticationException.getMessage(), EdgeServerErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        }
        mapper.writeValue(response.getWriter(), ServerErrorResponse.of("Authentication failed", EdgeServerErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
    }
}
