package com.haoyunhu.tools.rest2.advice;

import com.haoyunhu.tools.constant.OperatorConstant;
import com.haoyunhu.tools.exception.BizErrorBusinessException;
import com.haoyunhu.tools.exception.BizTokenException;
import com.haoyunhu.tools.exception.BizWarnBusinessException;
import com.haoyunhu.tools.rest.constant.RestBaseConstant;
import com.haoyunhu.tools.rest.dto.ResponseBaseDto;
import com.haoyunhu.tools.rest2.constant.ErrorCodeConstant;
import com.haoyunhu.tools.rest2.model.RestHeadInfo;
import com.haoyunhu.tools.utils.DateUtils;
import com.haoyunhu.tools.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by weijun.hu on 2016/3/18.
 */
@ControllerAdvice(annotations = RestController.class)
public class RestControllerAdvice {

    private Logger logger = LoggerFactory.getLogger(RestControllerAdvice.class);
    private static String FROM_SOURCE = "from_source";

    @ModelAttribute
    public RestHeadInfo newRestHeadInfo(HttpServletRequest request) {
        RestHeadInfo restHeadInfo = new RestHeadInfo();
        restHeadInfo.setFromSource(request.getHeader(FROM_SOURCE));
        return restHeadInfo;
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseBaseDto errorResponse(Exception ex) {
        String success = OperatorConstant.RETURN_FAILURE;
        String errorMessage;
        String errorCode;
        if (ex instanceof BizErrorBusinessException) {
            errorMessage = ex.getMessage();
            errorCode = ((BizErrorBusinessException) ex).getErrorCode();
        } else if (ex instanceof BizWarnBusinessException) {
            errorMessage = ex.getMessage();
            success = OperatorConstant.RETURN_EMPTY;
            errorCode = ((BizWarnBusinessException) ex).getErrorCode();
        } else if (ex instanceof BizTokenException) {
            errorMessage = ex.getMessage();
            success = OperatorConstant.RETURN_FAILURE_TOKEN;
            errorCode = ((BizTokenException) ex).getErrorCode();
        } else {
            errorMessage = RestBaseConstant.OPERATOR_FAILURE;
            errorCode = ErrorCodeConstant.ERROR_CODE_10002;
            logger.error(ex.getMessage(), ex);
        }

        ResponseBaseDto dto = new ResponseBaseDto();
        dto.setSuccess(success);
        dto.setMessage(errorMessage);
        dto.setResponseTime(DateUtils.dateToString(new Date()));
        if (StringUtils.isNotBlank(errorCode)) {
            dto.setErrorCode(errorCode);
        }
        return dto;
    }
}
