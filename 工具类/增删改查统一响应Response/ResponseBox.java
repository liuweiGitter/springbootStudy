package com.telecom.js.noc.hxtnms.operationplan.utils;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: liuwei
 * Date: 2019-05-27 14:16
 * Desc: ��ɾ�Ĳ���Ӧǰ̨����ۺ���
 */
@Data
public class ResponseBox<T>{
    //��Ӧ���
    private boolean success;
    //��Ӧ��Ϣ
    private String msg;
    //������
    private Integer responseCode;
    //��Ӧʵ���ࣺ������
    private T detail;
    //��Ӧʵ�����б����б�
    private List<T> list;
    //��ҳ����
    private Map<String,Integer> page;

    /**
     * �����ѯĬ�Ϲ���
     * @param detail
     * @return
     */
    public ResponseBox (T detail){
        this.setDetail(detail);
        this.setAll(true,"��ѯ�ɹ���",CommonConstants.SUCCESS_CODE);
    }

    /**
     * �б��ѯĬ�Ϲ���
     * @param list ��ѯ�б���
     * @param queryHomeBox ��ѯbox(��ҳ��ز����ѱ���ֵ)
     */
    public ResponseBox (List<T> list,QueryHomeBox<T> queryHomeBox){
        this.list = list;
        page = new HashMap<>();
        //ÿҳ����
        page.put("pageDataCount",queryHomeBox.getPageDataCount());
        //��ѯ��ҳ��
        page.put("queryPageNum",queryHomeBox.getQueryPageNum());
        //������
        page.put("totalCount",queryHomeBox.getTotalCount());
        //��ҳ��
        page.put("totalPageNum",queryHomeBox.getTotalPageNum());
        this.setAll(true,"��ѯ�ɹ���",CommonConstants.SUCCESS_CODE);
    }

    private ResponseBox(){
        //�����޲ι���
    }

    /**
     * ��ɾ��Ĭ�Ϲ���
     * @param count ��ɾ�Ľ��count
     */
    public ResponseBox(int count){
        String successMsg = "�����ɹ���";
        String failMsg = "����ʧ�ܣ�";
        setResponseBox(count,successMsg,failMsg);
    }

    /**
     * �Զ��巵����Ϣ����ɾ�Ĺ���
     * @param count ��ɾ�Ľ��count
     * @param successMsg �ɹ�ʱ���ص���Ϣ
     * @param failMsg ʧ��ʱ���ص���Ϣ
     */
    public ResponseBox(int count,String successMsg,String failMsg){
        setResponseBox(count,successMsg,failMsg);
    }

    /**
     * ��������Ĺ���
     * @param failParamMsg ���������Ϣ
     */
    public ResponseBox(String failParamMsg){
        setAll(false,failParamMsg,CommonConstants.PARAM_ERROR_CODE);
    }

    private void setAll(boolean success, String msg, Integer responseCode) {
        this.success = success;
        this.msg = msg;
        this.responseCode = responseCode;
    }

    private void setResponseBox(int count,String successMsg,String failMsg){
        //��ɾ����������񣬲���ʧ��ʱ����0
        if ( 0 == count){
            setAll(false,failMsg,CommonConstants.FAILE_CODE);
        }else{
            setAll(true,successMsg,CommonConstants.SUCCESS_CODE);
        }
    }
}
