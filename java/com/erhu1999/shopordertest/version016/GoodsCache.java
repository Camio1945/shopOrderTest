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
    public static synchronized Map<String, Object> getGoodsById(long goodsId) {
        return goodsCacheMap.get(goodsId);
    }

    /**
     * 减少库存
     *
     * @param goodsId  商品ID
     * @param subStock 减少库存数量
     */
    public static synchronized void subStock(long goodsId, int subStock) {
        Map<String, Object> goods = getGoodsById(goodsId);
        int stock = (Integer) goods.get("stock");
        goods.put("stock", stock - subStock);
    }

    /**
     * 初始化缓存
     *
     * @param goodsId 商品ID
     */
    public static void initCache(long goodsId) {
        Map<String, Object> goods = JdbcUtil016.queryOneRow("select `id`,`name`,`price`,`stock`,`isOn`,`firstImg` from `Goods` as t where t.id=" + goodsId);
        goodsCacheMap.put(goodsId, goods);
    }
}
