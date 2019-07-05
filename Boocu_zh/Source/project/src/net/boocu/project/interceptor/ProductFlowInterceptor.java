package net.boocu.project.interceptor;

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.EmailRecordEntity;
import net.boocu.project.entity.FlowEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.FlowService;
import net.boocu.web.Filter;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author zhang
 * @date 2019年03月11日 10:59
 */
public class ProductFlowInterceptor extends HandlerInterceptorAdapter {
    @Resource
    private FlowService flowService;
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView model) throws Exception {

        super.postHandle(request, response, handler, model);
    }

    @Override
    //邮箱推送的商品 用户点击之后在流量表添加记录
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        Long member_id = ReqUtil.getLong(request, "member_id", 0L);
        Long product_id = ReqUtil.getLong(request, "product_id", 0L);
        if(member_id!=0&&product_id!=0){//从邮箱点过来的
            String s1 = request.getHeader("user-agent");
            //流量表
            FlowEntity entity=new FlowEntity();
            if(s1.contains("Android")||s1.contains("iPhone")||s1.contains("iPad")) {
                entity.setOpenType(EmailRecordEntity.OpenType.MOBILE);
            }else {
                //设置打开方式为邮件打开的
                entity.setOpenType(EmailRecordEntity.OpenType.PC);
            }
            entity.setProduct_id(product_id);
            entity.setMember_id(member_id);
            FlowEntity save = flowService.save(entity);
        }

        return super.preHandle(request, response, handler);
    }
}
