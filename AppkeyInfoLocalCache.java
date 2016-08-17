package com.cn21.calendar.function;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.cn21.calendar.constant.APIConstants;
import com.cn21.calendar.dao.mapper.AppkeyInfoMapper;
import com.cn21.calendar.exception.DaoException;
import com.cn21.calendar.springcontext.SpringContextHolder;
import com.cn21.calendar.vo.basic.AppkeyInfoBasic;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 将AppkeyInfo信息保存到本地缓存
 * <p>
 * AppkeyInfoLocalCache
 * <p>
 * 
 * @author <a href="mailto:yangkj@corp.21cn.com">yangkj</a>
 * @version
 * @since 2016年5月16日
 */
@Component
public class AppkeyInfoLocalCache {

	private static Logger log = Logger.getLogger(AppkeyInfoLocalCache.class);

	static LoadingCache<String, AppkeyInfoBasic> cache = CacheBuilder.newBuilder().refreshAfterWrite(3, TimeUnit.HOURS)// 给定时间内没有被读/写访问，则回收。
			.expireAfterAccess(APIConstants.TOKEN_VALID_TIME, TimeUnit.HOURS)// 缓存过期时间和redis缓存时长一样
			.maximumSize(1000).// 设置缓存个数
			build(new CacheLoader<String, AppkeyInfoBasic>() {
				@Override
				/** 当本地缓存命没有中时，调用load方法获取结果并将结果缓存 **/
				public AppkeyInfoBasic load(String appKey) throws DaoException {
					return getAppkeyInfo(appKey);
				}

				/** 数据库进行查询 **/
				private AppkeyInfoBasic getAppkeyInfo(String appKey) throws DaoException {
					log.info("method<getAppkeyInfo> get AppkeyInfo form DB appkey<" + appKey + ">");
					return ((AppkeyInfoMapper) SpringContextHolder.getBean("appkeyInfoMapper"))
							.selectAppkeyInfoByAppKey(appKey);
				}
			});

	public static AppkeyInfoBasic getAppkeyInfoByAppkey(String appKey) throws DaoException, ExecutionException {
		log.info("method<getAppkeyInfoByAppkey> appkey<" + appKey + ">");
		return cache.get(appKey);
	}

}
