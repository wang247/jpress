package io.jpress.module.product.model;

import com.jfinal.core.JFinal;
import io.jboot.db.annotation.Table;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.commons.utils.JsoupUtils;
import io.jpress.model.UserCart;
import io.jpress.model.UserFavorite;
import io.jpress.module.product.model.base.BaseProduct;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Generated by JPress.
 */
@Table(tableName = "product", primaryKey = "id")
public class Product extends BaseProduct<Product> {

    private static final long serialVersionUID = 1L;

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_DRAFT = 2;
    public static final int STATUS_TRASH = 3;


    public boolean isNormal() {
        return getStatus() != null && getStatus() == STATUS_NORMAL;
    }

    public boolean isDraft() {
        return getStatus() != null && getStatus() == STATUS_DRAFT;
    }

    public boolean isTrash() {
        return getStatus() != null && getStatus() == STATUS_TRASH;
    }


    public String getUrl() {
        if (StrUtil.isBlank(getSlug())) {
            return JFinal.me().getContextPath() + "/product/" + getId() + JPressOptions.getAppUrlSuffix();
        } else {
            return JFinal.me().getContextPath() + "/product/" + getSlug() + JPressOptions.getAppUrlSuffix();
        }
    }

    public String getHtmlView() {
        return StrUtil.isBlank(getStyle()) ? "product.html" : "product_" + getStyle().trim() + ".html";
    }

    public String getText() {
        return JsoupUtils.getText(getContent());
    }

    /**
     * 获取文章的所有图片
     *
     * @return
     */
    public List<String> getImages() {
        return JsoupUtils.getImageSrcs(getContent());
    }

    /**
     * 获取前面几张图片
     *
     * @param count
     * @return
     */
    public List<String> getImages(int count) {
        List<String> list = getImages();
        if (list == null || list.size() <= count) {
            return list;
        }

        List<String> newList = new ArrayList<>();
        for (int i = 0; 0 < count; i++) newList.add(list.get(i));
        return newList;
    }

    public boolean hasImage() {
        return getFirstImage() != null;
    }

    public boolean hasVideo() {
        return getFirstVideo() != null;
    }

    public boolean hasAudio() {
        return getFirstAudio() != null;
    }

    public String getFirstImage() {
        return JsoupUtils.getFirstImageSrc(getContent());
    }

    public String getFirstVideo() {
        return JsoupUtils.getFirstVideoSrc(getContent());
    }

    public String getFirstAudio() {
        return JsoupUtils.getFirstAudioSrc(getContent());
    }

    public String getShowImage() {
        String thumbnail = getThumbnail();
        return StrUtil.isNotBlank(thumbnail) ? thumbnail : getFirstImage();
    }

    public boolean isCommentEnable() {
        Boolean cs = getCommentStatus();
        return cs != null && cs == true;
    }


    public UserCart toUserCartItem(Long userId, Long distUserId, String spec) {
        UserCart userCart = new UserCart();

        userCart.setUserId(userId);
        userCart.setSellerId(this.getUserId());
        userCart.setProductId(getId());
        userCart.setProductType(UserFavorite.FAV_TYPE_PRODUCT);
        userCart.setProductTypeText(UserFavorite.FAV_TYPE_PRODUCT_TEXT);
        userCart.setProductPrice(this.getPrice());
        userCart.setProductCount(1);
        userCart.setProductTitle(getTitle());
        userCart.setProductSummary(CommonsUtils.maxLength(getText(), 200));
        userCart.setSelected(false);
        userCart.setProductLink(getUrl());
        userCart.setWithVirtual(false);//非虚拟产品
        userCart.setWithRefund(true);//可以退货
        userCart.setCommentPath(getUrl());
        userCart.setCreated(new Date());
        userCart.setProductSpec(spec);

        Boolean disEnable = getDistEnable();
        if (disEnable != null && disEnable) {
            userCart.setDistUserId(distUserId);
        }


        String showImage = getShowImage();
        if (StrUtil.isNotBlank(showImage)) {
            userCart.setProductThumbnail(showImage);
        }

        return userCart;
    }

    public UserFavorite toFavorite(Long userId) {
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setType(UserFavorite.FAV_TYPE_PRODUCT);
        favorite.setTypeText(UserFavorite.FAV_TYPE_PRODUCT_TEXT);
        favorite.setTypeId(String.valueOf(getId()));
        favorite.setTitle(getTitle());
        favorite.setSummary(getSummary());
        favorite.setThumbnail(getShowImage());
        favorite.setLink(getUrl());
        favorite.setCreated(new Date());
        return favorite;
    }


}
