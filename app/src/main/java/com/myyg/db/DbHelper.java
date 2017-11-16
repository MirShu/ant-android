package com.myyg.db;

import android.content.Context;
import android.database.Cursor;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.myyg.base.BaseApplication;
import com.myyg.model.AreaModel;
import com.myyg.model.GoodsDetailsModel;
import com.myyg.model.GoodsModel;
import com.myyg.model.JoinModel;
import com.myyg.model.ShopCartModel;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.DateHelper;
import com.myyg.utils.MyLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JOHN on 2016/6/26.
 */
public class DbHelper {
    private static final String TAG = DbHelper.class.getSimpleName();
    private static DbUtils dbUtils;
    private static Context mContext;
    private static final String MYYG_DB_NAME = "myyg.db";

    static {
        init();
    }

    /**
     * 初始化数据
     */
    private static void init() {
        mContext = BaseApplication.getInstance();
        dbUtils = DbUtils.create(BaseApplication.getInstance(), MYYG_DB_NAME);
        createDatabase();
    }

    /**
     * 创建数据库
     */
    public static void createDatabase() {
        try {
            if (dbUtils.tableIsExist(AreaModel.class)) {
                return;
            }
            String dbFile = mContext.getDatabasePath(MYYG_DB_NAME).getAbsolutePath();
            CommonHelper.copyFileFromAssets(mContext, MYYG_DB_NAME, dbFile);
        } catch (DbException e) {

        }
    }

    /**
     * @param model
     * @return
     */
    public static boolean addShopCart(ShopCartModel model) {
        try {
            ShopCartModel dbModel = getShopCart(model.getGoodsId());
            if (dbModel == null) {
                String time = DateHelper.getDefaultDate(System.currentTimeMillis());
                model.setShopId(CommonHelper.getUUID());
                model.setCreateTime(time);
                dbUtils.saveOrUpdate(model);
            } else {
                dbModel.setJoinNumber(dbModel.getJoinNumber() + model.getJoinNumber());
                dbModel.setTotalMoney(dbModel.getJoinNumber() * dbModel.getPrice());
                dbUtils.saveOrUpdate(dbModel);
            }
            return true;
        } catch (Exception ex) {
            MyLog.e(TAG, ex.getMessage());
            return false;
        }
    }

    /**
     * @param goodsId
     * @param joinNumber
     * @return
     */
    public static boolean editShopCart(String goodsId, int joinNumber) {
        try {
            ShopCartModel dbModel = getShopCart(goodsId);
            if (dbModel != null) {
                dbModel.setJoinNumber(joinNumber);
                dbModel.setTotalMoney(dbModel.getJoinNumber() * dbModel.getPrice());
                dbUtils.saveOrUpdate(dbModel);
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * @param goodsModel
     * @return
     */
    public static boolean addShopCart(GoodsModel goodsModel) {
        ShopCartModel model = new ShopCartModel();
        model.setGoodsId(goodsModel.getId() + "");
        model.setGoodsName(goodsModel.getTitle());
        model.setGoodsImage(goodsModel.getThumb());
        model.setTotalNumber(goodsModel.getZongrenshu());
        model.setSurplusNumber(goodsModel.getShenyurenshu());
        model.setJoinNumber(1);
        model.setPeriods(goodsModel.getQishu());
        model.setPrice(goodsModel.getYunjiage());
        model.setTotalMoney(model.getPrice() * model.getJoinNumber());
        boolean result = addShopCart(model);
        return result;
    }

    /**
     * @param goodsDetailsModel
     * @param joinNumber
     * @return
     */
    public static boolean addShopCart(GoodsDetailsModel goodsDetailsModel, int joinNumber) {
        ShopCartModel model = new ShopCartModel();
        model.setGoodsId(goodsDetailsModel.getId() + "");
        model.setGoodsName(goodsDetailsModel.getTitle());
        model.setGoodsImage(goodsDetailsModel.getThumb());
        model.setTotalNumber(goodsDetailsModel.getZongrenshu());
        model.setSurplusNumber(goodsDetailsModel.getShenyurenshu());
        model.setJoinNumber(joinNumber);
        model.setPeriods(goodsDetailsModel.getQishu());
        model.setPrice(goodsDetailsModel.getYunjiage());
        model.setTotalMoney(model.getPrice() * model.getJoinNumber());
        boolean result = addShopCart(model);
        return result;
    }

    /**
     * 添加到购物车
     *
     * @param joinModel
     * @param joinNumber
     * @return
     */
    public static boolean addShopCart(JoinModel joinModel, int joinNumber) {
        ShopCartModel model = new ShopCartModel();
        model.setGoodsId(joinModel.getId() + "");
        model.setGoodsName(joinModel.getTitle());
        model.setGoodsImage(joinModel.getThumb());
        model.setTotalNumber(joinModel.getZongrenshu());
        model.setSurplusNumber(joinModel.getShenyurenshu());
        model.setJoinNumber(joinNumber);
        model.setPeriods(joinModel.getQishu());
        model.setPrice(joinModel.getYunjiage());
        model.setTotalMoney(model.getPrice() * model.getJoinNumber());
        boolean result = addShopCart(model);
        return result;
    }

    /**
     * @return
     */
    public static List<ShopCartModel> getShopCart() {
        List<ShopCartModel> listShopCart = new ArrayList<>();
        try {
            listShopCart = dbUtils.findAll(Selector.from(ShopCartModel.class).orderBy("CreateTime", true));
        } catch (Exception ex) {
            MyLog.e(TAG, ex.getMessage());
        }
        return listShopCart;
    }

    /**
     * @param goodsId
     * @return
     */
    public static ShopCartModel getShopCart(String goodsId) {
        ShopCartModel model = new ShopCartModel();
        try {
            model = dbUtils.findFirst(Selector.from(ShopCartModel.class).where("GoodsId", "=", goodsId));
        } catch (Exception ex) {

        }
        return model;
    }

    /**
     * @param shopId
     * @return
     */
    public static boolean deleteShopCart(String shopId) {
        try {
            dbUtils.deleteById(ShopCartModel.class, shopId);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 修改购物车信息
     *
     * @param model
     * @return
     */
    public static boolean updateShopCart(ShopCartModel model) {
        try {
            dbUtils.saveOrUpdate(model);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 获取购物车总金额
     *
     * @return
     */
    public static float getTotalMoney() {
        float totalMoney = 0f;
        Cursor cursor = null;
        try {
            String strSQL = "SELECT SUM(TotalMoney) FROM T_ShopCart";
            cursor = dbUtils.execQuery(strSQL);
            if (null != cursor && cursor.moveToNext()) {
                totalMoney = cursor.getFloat(0);
            }
        } catch (Exception ex) {
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return totalMoney;
    }

    /**
     * 获取购物车总商品数量
     *
     * @return
     */
    public static int getTotalShopCart() {
        int count = 0;
        Cursor cursor = null;
        try {
            String strSQL = "SELECT COUNT(1) FROM T_ShopCart";
            cursor = dbUtils.execQuery(strSQL);
            if (null != cursor && cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception ex) {
            MyLog.i(TAG, ex.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return count;
    }

    /**
     * 清空购物车
     *
     * @return
     */
    public static boolean clearShopCart() {
        try {
            dbUtils.deleteAll(ShopCartModel.class);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 通过父级行政区域获取子级行政区域列表
     *
     * @param parentId
     * @return
     */
    public static List<AreaModel> getAreaByParentId(int parentId) {
        List<AreaModel> listArea = new ArrayList<>();
        try {
            listArea = dbUtils.findAll(Selector.from(AreaModel.class).where("ParentId", "=", parentId).orderBy("AreaId", false));
        } catch (Exception ex) {
            MyLog.e(TAG, ex.getMessage());
        }
        return listArea;
    }

    /**
     * @param name
     * @return
     */
    public static AreaModel getAreaByName(String name) {
        AreaModel areaModel = null;
        try {
            areaModel = dbUtils.findFirst(Selector.from(AreaModel.class).where("AreaName", "=", name));
        } catch (DbException e) {

        }
        return areaModel;
    }
}
