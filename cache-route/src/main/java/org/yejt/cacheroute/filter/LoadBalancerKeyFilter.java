package org.yejt.cacheroute.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author keys961
 */
@Component
public class LoadBalancerKeyFilter extends ZuulFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadBalancerKeyFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String key = request.getHeader(FilterConstants.LOAD_BALANCER_KEY);
        if (key != null) {
            LOGGER.info("Loader balancer key is {}, from request {}.",
                    key, request.getRemoteAddr());
            context.set(FilterConstants.LOAD_BALANCER_KEY, key);
        } else {
            LOGGER.warn("Loader balancer key is empty from request {}!",
                    request.getRemoteAddr());
        }

        return null;
    }
}
