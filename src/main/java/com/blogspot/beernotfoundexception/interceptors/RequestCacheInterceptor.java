package com.blogspot.beernotfoundexception.interceptors;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;

@Interceptor
@RequestCache
public class RequestCacheInterceptor implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final String MAP_NAME = RequestCacheInterceptor.class.getName() + "_map_cache";

	@AroundInvoke
	public Object execute(InvocationContext invocationContext) throws Exception {
	    Map<Key, Result> mapCache = getMapCache();
	    Key key = new Key(invocationContext);
	    Result result = mapCache.get(key);
	    if (result == null) {
	        long ini = System.currentTimeMillis();
	        result = new Result(invocationContext.proceed());
	        result.time = System.currentTimeMillis() - ini;
	        mapCache.put(key, result);
	    }
	    result.count++;
	    return result.object;
	}   

	@SuppressWarnings("unchecked")
	private Map<Key, Result> getMapCache() {
	    Map<Key, Result> map = (Map<Key, Result>) getRequest().getAttribute(MAP_NAME);
	    if (map == null) {
	        map = Collections.synchronizedMap(new HashMap<RequestCacheInterceptor.Key, Result>());
	        getRequest().setAttribute(MAP_NAME, map);
	    }
	    return map;
	}

	private HttpServletRequest getRequest() {
	    return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();        
	}

	public class Result {
	    private long count = 0;
	    private long time;
	    private Object object;

	    public Result(Object object) {
	        super();
	        this.object = object;
	    }
	    
	    public long getTime() {
			return time;
		}
	    
	    public long getCount() {
			return count;
		}

	    @Override
	    public int hashCode() {
	        final int prime = 31;
	        int result = 1;
	        result = prime * result + getOuterType().hashCode();
	        result = prime * result
	                + ((this.object == null) ? 0 : this.object.hashCode());
	        return result;
	    }
	    @Override
	    public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        Result other = (Result) obj;
	        if (!getOuterType().equals(other.getOuterType()))
	            return false;
	        if (object == null) {
	            if (other.object != null)
	                return false;
	        } else if (!object.equals(other.object))
	            return false;
	        return true;
	    }
	    private RequestCacheInterceptor getOuterType() {
	        return RequestCacheInterceptor.this;
	    }

	}

	private class Key {
	    Method method;
	    Object[] params;

	    public Key(InvocationContext invocationContext) {
	        this(invocationContext.getMethod(), invocationContext.getParameters());
	    }

	    public Key(Method method, Object[] params) {
	        super();
	        this.method = method;
	        this.params = params;
	    }

	    @Override
	    public int hashCode() {
	        final int prime = 31;
	        int result = 1;
	        result = prime * result + getOuterType().hashCode();
	        result = prime * result
	                + ((method == null) ? 0 : method.hashCode());
	        result = prime * result + Arrays.hashCode(params);
	        return result;
	    }

	    @Override
	    public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null)
	            return false;
	        if (getClass() != obj.getClass())
	            return false;
	        Key other = (Key) obj;
	        if (!getOuterType().equals(other.getOuterType()))
	            return false;
	        if (method == null) {
	            if (other.method != null)
	                return false;
	        } else if (!method.equals(other.method))
	            return false;
	        if (!Arrays.equals(params, other.params))
	            return false;
	        return true;
	    }

	    private RequestCacheInterceptor getOuterType() {
	        return RequestCacheInterceptor.this;
	    }

	}	

}
