package com.erhu1999.shopordertest.version016;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存工具类
 *
 * @author HuKaiXuan
 */
class GoodsCache {
    /** 商品map */
    private static final Map<Long, Map<String, Object>> goodsCacheMap = new ConcurrentHashMap<>();

    /**
     * 根据商品ID获取商品对象
     *
     * @param goodsId 商品ID
     * @return 商品对象
     */
    public static Map<String, Object> getGoodsById(long goodsId) {
        return goodsCacheMap.get(goodsId);
    }

    /**
     * 初始化缓存
     *
     * @param goodsId 商品ID
     */
    public static void initCache(long goodsId) {
        Map<String, Object> goods = JdbcUtil016.queryOneRow("select `id`,`name`,`price`,`isOn`,`firstImg` from `Goods` as t where t.id=" + goodsId);
        goodsCacheMap.put(goodsId, goods);
    }
}
