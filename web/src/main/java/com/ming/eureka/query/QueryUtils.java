package com.ming.eureka.query;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求查询工具
 *
 * @author Administrator
 */
@SuppressWarnings("Duplicates")
public class QueryUtils {

    /**
     * 保存在session中的查询map对象名称
     */
    private static final String MANAGE_SESSION_QUERY_MAP = QueryUtils.class + ".Manage.SessionQueryMap";

    /**
     * 从session中获取QueryInfo对象
     *
     * @param request   请求
     * @param className 类名
     * @return {@link QueryInfo}
     */
    public static QueryInfo getQueryInfoFromSession(HttpServletRequest request, String className) {
        Map<String, QueryInfo> queryMap = getManageQueryInfoMapFromSession(request);
        QueryInfo queryInfo = queryMap.get(className);
        if (queryInfo == null) {
            queryInfo = new QueryInfo();
            queryMap.put(className, queryInfo);
        }
        queryInfo.setUri(request.getRequestURI());
        return queryInfo;
    }

    /**
     * 从当前会话中获取查询map
     *
     * @param request 请求
     * @return {@link Map<String, QueryInfo>}
     */
    private static Map<String, QueryInfo> getManageQueryInfoMapFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object map = session.getAttribute(MANAGE_SESSION_QUERY_MAP);
        if (map == null) {
            map = new HashMap<String, QueryInfo>();
            session.setAttribute(MANAGE_SESSION_QUERY_MAP, map);
        }
        return (HashMap<String, QueryInfo>) map;
    }

    /**
     * 更新查询会话
     *
     * @param request 请求
     */
    public static void updateQuerySession(HttpServletRequest request) {
        request.getSession().setAttribute(MANAGE_SESSION_QUERY_MAP, request.getSession().getAttribute(MANAGE_SESSION_QUERY_MAP));
    }
}
