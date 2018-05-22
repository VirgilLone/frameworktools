package com.haoyunhu.tools.rest;

import com.haoyunhu.tools.constant.OperatorConstant;
import com.haoyunhu.tools.rest.constant.RestBaseConstant;
import com.haoyunhu.tools.rest.dto.ResponseBaseDto;
import com.haoyunhu.tools.utils.DateUtils;
import com.haoyunhu.tools.utils.JacksonUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by lulu.han on 2016/2/24.
 */
@org.springframework.stereotype.Controller
public class RestHeartControllerBean implements Controller {
    //日志
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RestControllerBean.class);

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        ResponseBaseDto dto = new ResponseBaseDto();
        dto.setSuccess( OperatorConstant.RETURN_SUCCESS);
        dto.setMessage(RestBaseConstant.OPERATOR_SUCCESS);
        dto.setResponseTime(DateUtils.dateToString(new Date()));
        // 返回字符串
        String result;
        try {
            result = JacksonUtils.getInstance().writeValueAsString(dto);
        } catch (Exception e) {
            result = "";
            logger.error("convert json error. " + e.getMessage(), e);
        }

        httpServletResponse.setContentType("application/json; charset=utf-8");
        PrintWriter writer = httpServletResponse.getWriter();
        writer.print(result);
        writer.flush();
        writer.close();
        return null;
    }
}
