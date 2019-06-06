package cn.edu.neu.shop.pin.service;

import cn.edu.neu.shop.pin.mapper.PinProductMapper;
import cn.edu.neu.shop.pin.mapper.PinUserProductCollectionMapper;
import cn.edu.neu.shop.pin.model.PinProduct;
import cn.edu.neu.shop.pin.model.PinUserProductCollection;
import cn.edu.neu.shop.pin.util.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author flyhero
 * 给定用户的userId，查询他的商品收藏记录
 */
@Service
public class UserProductCollectionService extends AbstractService<PinUserProductCollection> {

    public static final int STATUS_ADD_PRODUCT_SUCCESS = 0;
    public static final int STATUS_ADD_PRODUCT_INVALID_ID = -1;
    public static final int STATUS_DELETE_PRODUCT_SUCCESS = 0;
    public static final int STATUS_DELETE_PRODUCT_INVALID_ID = -1;
    public static final int STATUS_DELETE_PRODUCT_PERMISSION_DENIED = -2;

    @Autowired
    private PinUserProductCollectionMapper pinUserProductCollectionMapper;

    @Autowired
    private PinProductMapper pinProductMapper;

    /**
     * 根据用户id获取其收藏的商品列表及商品信息、店铺信息
     * @param userId
     * @return
     */
    public List<PinUserProductCollection> getUserProductCollection(Integer userId) {
        return pinUserProductCollectionMapper.getUserProductCollection(userId);
    }

    /**
     * 插入一条新的商品收藏
     * @param userId
     * @param productId
     * @return
     */
    @Transactional
    public Integer addProductToCollection(Integer userId, Integer productId) {
        PinProduct pinProduct = pinProductMapper.selectByPrimaryKey(productId);
        if(pinProduct == null) return STATUS_ADD_PRODUCT_INVALID_ID;
        PinUserProductCollection pinUserProductCollection = new PinUserProductCollection();
        pinUserProductCollection.setProductId(productId);
        pinUserProductCollection.setCreateTime(new Date());
        pinUserProductCollection.setUserId(userId);
        this.save(pinUserProductCollection);
        return STATUS_ADD_PRODUCT_SUCCESS;
    }

    @Transactional
    public Integer deleteStoreCollection(Integer userId, Integer productId) {
        PinProduct pinProduct = pinProductMapper.selectByPrimaryKey(productId);
        if(pinProduct == null) return STATUS_DELETE_PRODUCT_INVALID_ID;
        PinUserProductCollection p = new PinUserProductCollection();
        p.setUserId(userId);
        p.setProductId(productId);
        List<PinUserProductCollection> list = pinUserProductCollectionMapper.select(p);
        if(list.size() == 0) return STATUS_DELETE_PRODUCT_INVALID_ID;
        for(PinUserProductCollection pp : list) {
            if(pp.getUserId() != userId)
                return STATUS_DELETE_PRODUCT_PERMISSION_DENIED;
            if(pp.getProductId() != productId)
                return STATUS_DELETE_PRODUCT_INVALID_ID;
            pinUserProductCollectionMapper.delete(pp);
        }
        return STATUS_DELETE_PRODUCT_SUCCESS;
    }
}
