package com.xxd.extendpopmenu.trans;

import com.xxd.extendpopmenu.entity.BackExtendHeader;
import com.xxd.extendpopmenu.entity.BackExtendLevel1;
import com.xxd.extendpopmenu.entity.BackExtendLevel2;
import com.xxd.extendpopmenu.entity.ExtendData;
import com.xxd.extendpopmenu.entity.ExtendItem;
import com.xxd.extendpopmenu.utils.AssertUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xxd on 2017/7/29.
 * <p>
 * 将接口返回的数据转化为ExtendView可以接收的数据
 */

public class ExtendDataTrans {

    /**
     * 转换无穷数级的格式（数组模式）
     *
     * @param back
     * @return
     */
    public static List<ExtendData> parse(List<BackExtendHeader> back) {
        List<ExtendData> list = new ArrayList<>();
        if (!AssertUtil.isEmpty(back)) {
            for (int i = 0; i < back.size(); i++) {
                BackExtendHeader header = back.get(i);
                ExtendData data = new ExtendData();
                list.add(data);
                data.setType(header.getType());
                addTitle(data, header.getList());

            }
        }
        return list;
    }

    /**
     * 转换服务器返回的每个数组只有1个子项的数据，但是有很多重复数组的格式。。。
     *
     * @param back
     * @return
     */
    public static List<ExtendData> parseSingle(List<BackExtendHeader> back) {
        List<ExtendData> list = new ArrayList<>();
        if (!AssertUtil.isEmpty(back)) {
            for (int i = 0; i < back.size(); i++) {
                BackExtendHeader header = back.get(i);
                ExtendData data = new ExtendData();
                list.add(data);
                data.setType(header.getType());
                addTitleSingle(data, header.getList());

            }
        }
        return list;
    }

    private static void addTitle(ExtendData data, List<BackExtendLevel1> levelList1) {
        if (AssertUtil.isEmpty(levelList1))
            return;
        // 第一级
        data.setTotalLevel(1);
        // 放入header信息
        List<ExtendItem> itemList = new ArrayList<>();
        data.setList(itemList);
        for (BackExtendLevel1 level1 : levelList1) {
            String title = level1.getTitle();
            if (AssertUtil.isEmpty(title)) { // 没有标题，直接进入下一级
                addContent(data, itemList, level1.getContentList(), 0);
            } else {  // 有标题，标题作为1级
                ExtendItem item = new ExtendItem();
                item.setCurrentLevel(1);
                item.setTitle(true);
                item.setName(title);
                item.setCurrentLevel(1);
                List<ExtendItem> childItemList = new ArrayList<>();
                item.setChild(childItemList);
                itemList.add(item);
                addContent(data, childItemList, level1.getContentList(), 1);
            }
        }
    }

    private static void addContent(ExtendData data, List<ExtendItem> itemList, List<BackExtendLevel2> levelList2, int lastLevel) {
        if (AssertUtil.isEmpty(levelList2))
            return;
        // 总层级+1
        if (data.getTotalLevel() < lastLevel + 1)
            data.setTotalLevel(lastLevel + 1);
        // 遍历level2集合赋值到新的集合中
        for (BackExtendLevel2 level2 : levelList2) {
            ExtendItem item = new ExtendItem();
            itemList.add(item);
            item.setCurrentLevel(lastLevel + 1);
            item.setCode(level2.getCode());
            item.setName(level2.getName());
            if (!AssertUtil.isEmpty(level2.getContentList())) { // 如果接口数据有下一级
                List<ExtendItem> childItemList = new ArrayList<>();
                item.setChild(childItemList);
                addContent(data, childItemList, level2.getContentList(), lastLevel + 1);
            }
        }
    }

    private static void addTitleSingle(ExtendData data, List<BackExtendLevel1> levelList1) {
        if (AssertUtil.isEmpty(levelList1))
            return;
        // 第一级
        data.setTotalLevel(1);
        // 放入header信息
        List<ExtendItem> itemList = new ArrayList<>();
        data.setList(itemList);
        for (BackExtendLevel1 level1 : levelList1) {
            String title = level1.getTitle();
            if (AssertUtil.isEmpty(title)) { // 没有标题，直接进入下一级
                addContentSingle(data, data.getCacheMap(), itemList, level1.getContentList(), 0);
            } else {  // 有标题，标题作为1级
                ExtendItem item = new ExtendItem();
                item.setCacheMap(new HashMap<String, ExtendItem>());
                item.setCurrentLevel(1);
                item.setTitle(true);
                item.setName(title);
                item.setCurrentLevel(1);
                List<ExtendItem> childItemList = new ArrayList<>();
                item.setChild(childItemList);
                itemList.add(item);
                addContentSingle(data, item.getCacheMap(), childItemList, level1.getContentList(), 1);
            }
        }
    }

    private static void addContentSingle(ExtendData data, Map<String, ExtendItem> cacheMap, List<ExtendItem> itemList, List<BackExtendLevel2> levelList2, int lastLevel) {
        if (AssertUtil.isEmpty(levelList2))
            return;
        // 总层级+1
        if (data.getTotalLevel() < lastLevel + 1)
            data.setTotalLevel(lastLevel + 1);

        // 遍历level2集合赋值到新的集合中
        for (BackExtendLevel2 level2 : levelList2) {

            String code = level2.getCode(); // 本层的code标识

            ExtendItem item = cacheMap.get(code);
            if(item == null){  // 没有被添加过
                item = new ExtendItem();
                itemList.add(item);
                item.setCurrentLevel(lastLevel + 1);
                item.setCode(code);
                item.setName(level2.getName());
                cacheMap.put(code,item);
            }else{  // 已经添加过了

            }

            if (!AssertUtil.isEmpty(level2.getContentList())) { // 如果接口数据有下一级
                List<ExtendItem> childItemList = item.getChild();
                if(childItemList == null){
                    childItemList = new ArrayList<>();
                    item.setChild(childItemList);
                    item.setCacheMap(new HashMap<String, ExtendItem>());

                }
                addContentSingle(data,item.getCacheMap(), childItemList, level2.getContentList(), lastLevel + 1);
            }
        }

    }


}
