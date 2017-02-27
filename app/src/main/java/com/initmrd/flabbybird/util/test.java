package com.initmrd.flabbybird.util;

import java.util.List;

/**
 * Created by initMrd@gmail.com on 17/2/24.
 */

public class test {

    private List<FileBean> mFileBeen;

    //网络请求的回调方法
    //为了方便 String转换成list我就不写了 这里直接饮用list
    void getDataFromNet(List<mClass> netList){
        //这里将网络获取的数组和第一层id:0传进方法
        putList(netList,0);

    }

    void putList(List<mClass> list, int superid){
        //遍历数组
        for (int i=0;i<list.size();i++){
            //将网络获取的list转换成我们需要的List
            mFileBeen.add(new FileBean(superid,list.get(i).id,list.get(i).name));
            //处理一条后,如果他的子集数量不为0,那么,我们就再次调用这个方法,把它子集里的内容添加进我们需要的List里
            if(list.get(i).count!=0){
                //这里传入的时你的子集和子集对应的父id
                putList(list.get(i).list,list.get(i).id);
            }
        }
    }

    //这是你网络请求回来的数据类型
    class mClass{
        int id;
        String name;
        int count;
        List<mClass> list;
    }


    //list需要的数据类型
    class FileBean{
        int superid;
        int id;
        String name;

        public FileBean(int superid, int id, String name) {
            this.superid = superid;
            this.id = id;
            this.name = name;
        }
    }
}
