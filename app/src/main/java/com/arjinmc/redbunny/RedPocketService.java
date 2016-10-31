package com.arjinmc.redbunny;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

import static com.arjinmc.redbunny.WechatUtils.WECHAT_PACKAGE;

/**
 * 监听红包信息
 * Created by Eminem on 2016/10/25.
 */

public class RedPocketService extends AccessibilityService {

    private final String TAG = "RedPocketService";
    private final int DELAY_TIME = 2000;
    /**标注微信消息通知过来的红包是否被领取*/
    private boolean wechatHasFetch = true;
    private Handler handler = new Handler();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event){
//        //监听行为分析
//        Log.e(TAG,"《=======================================================");
//        Log.e(TAG,"【packagename】: "+event.getPackageName());
//        Log.e(TAG,"【classname】: "+event.getClassName());
//        Log.e(TAG,"【source】: "+event.getSource());
//        Log.e(TAG,"【evenType】: "+AccessibilityEvent.eventTypeToString(event.getEventType()));
//        Log.e(TAG,"========================================================》");

        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence text : texts) {
                        String content = text.toString();
                        if (content.contains(WechatUtils.WECHAT_NOTICE_LUCKY_MONEY)
                                && isWechatEnabled(event)) {
                            wechatHasFetch = false;
                            clickNotification(event);

                        }else if(content.contains(QQUtils.QQ_NOTICE_LUCKY_MONEY)
                                && isQQEnabled(event)){
                            clickNotification(event);
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                if (isWechatEnabled(event)
                        && event.getClassName().toString().equals("android.widget.ListView")) {
                    clickWechatLuckyMoney();
                }else if(isQQEnabled(event) && event.getClassName().toString().equals(QQUtils.QQ_LIST_VIEW)){
                    openQQLuckyMoney();
                }

                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if(isWechatEnabled(event)){
                    String className = event.getClassName().toString();
                    if(className.equals(WechatUtils.WECHAT_LUCKY_MONEY_DETAIL)){
                        actionBack();
                    }
                    if(!wechatHasFetch) {
                        if (className.equals(WechatUtils.WECHAT_LAUNCHER)) {
                            clickWechatLuckyMoney();
                        }
                    }
                    if (className
                            .equals(WechatUtils.WECHAT_LUCKY_MONEY_PLUGIN)) {
                        openWechatLuckyMoney();
                    }
                }else if(isQQEnabled(event)){
                    String className = event.getClassName().toString();
                    if(className.equals(QQUtils.QQ_DETAIL)){
                        handler.postDelayed(new GobackRunnable(), DELAY_TIME);
                    }else{
                        openQQLuckyMoney();
                    }

                }
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                if(isQQEnabled(event)){
                    openQQLuckyMoney();
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if(isQQEnabled(event)){
                    String className = event.getClassName().toString();
                    if(className.equals(QQUtils.QQ_LIST_VIEW)){
                        openQQLuckyMoney();
                    }

                }
                break;
        }

    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 点击通知
     * @param event
     */
    private void clickNotification(AccessibilityEvent event){
        if (event.getParcelableData() != null
                && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event
                    .getParcelableData();
            PendingIntent pendingIntent = notification.contentIntent;
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 打开微信红包
     */
    @SuppressLint("NewApi")
    private void openWechatLuckyMoney() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = new ArrayList<AccessibilityNodeInfo>();
            //中文版
            list.addAll(nodeInfo.findAccessibilityNodeInfosByText("拆红包"));
            //英文版
            list.addAll(nodeInfo.findAccessibilityNodeInfosByText("Open"));
            //部分中文版text上面没有任何字
            if(list.size()==0) {
                int nodeSize = nodeInfo.getChildCount();
                for (int i = 0; i < nodeSize; i++) {
                    if (nodeInfo.getChild(i).getClassName().toString().equals("android.widget.Button")) {
                        list.add(nodeInfo.getChild(i));
                    }
                }
            }

            if(list!=null && list.size()!=0)
                list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }

    }

    /**
     * 领取微信红包
     */
    @SuppressLint("NewApi")
    private void clickWechatLuckyMoney() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> nodeInfos = rootNode
                    .findAccessibilityNodeInfosByText("领取红包");

            if(nodeInfos!=null && nodeInfos.size()!=0
                    && nodeInfos.get(nodeInfos.size()-1).getParent()!=null)
                nodeInfos.get(nodeInfos.size()-1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

        }
    }

    /**
     * 打开QQ红包
     */
    @SuppressLint("NewApi")
    private void openQQLuckyMoney(){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = new ArrayList<AccessibilityNodeInfo>();
            //先找到红包
            list.addAll(nodeInfo.findAccessibilityNodeInfosByText("QQ红包"));
            if(list.size()!=0){
                //最后一个红包
                AccessibilityNodeInfo lastOne = list.get(list.size()-1);
                //判断是否为口令红包
                if(lastOne.getParent()!=null
                        && lastOne.getParent().findAccessibilityNodeInfosByText("口令红包")!=null
                        && lastOne.getParent().findAccessibilityNodeInfosByText("口令红包已拆开").size()==0
                        && lastOne.getParent().findAccessibilityNodeInfosByText("已拆开").size()==0
                        ){

                    //点击口令红包
                    lastOne.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    //找到弹出框点击输入口令
                    List<AccessibilityNodeInfo> pwdViews = nodeInfo.findAccessibilityNodeInfosByText("点击输入口令");
                    if(pwdViews!=null && pwdViews.size()!=0){
                        pwdViews.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

                        //找到发送按钮并且发送
                        List<AccessibilityNodeInfo> sendBtns =
                                nodeInfo.findAccessibilityNodeInfosByText(QQUtils.QQ_SEND);
                        if(sendBtns!=null && sendBtns.size()!=0) {
                            sendBtns.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);

                        }
                    }

                //如果不是口令红包
                }else if(lastOne.getParent().findAccessibilityNodeInfosByText("已拆开").size()==0) {
                        lastOne.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

                }
            }

        }

    }


    /**
     * 模拟返回
     */
    @SuppressLint("NewApi")
    private void actionBack(){
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        }
        wechatHasFetch = true;
    }

    /**
     * 判断是否为可以抢微信红包
     * @param event
     * @return
     */
    private boolean isWechatEnabled(AccessibilityEvent event){
        if(event.getPackageName().equals(WECHAT_PACKAGE)
                && SPUtils.getStatus(getApplicationContext(),SPUtils.WECHAT))
            return true;
        return false;
    }

    /**
     * 判断是否为可以抢QQ红包
     * @param event
     * @return
     */
    private boolean isQQEnabled(AccessibilityEvent event){
        if(event.getPackageName().equals(QQUtils.QQ_PACKAGE)
                && SPUtils.getStatus(getApplicationContext(),SPUtils.QQ)){
            return true;
        }
        return  false;
    }

    /**
     * QQ红包领取后后退5次，关闭QQ但不停止运行
     * 原因：打开QQ时再有红包，不会有通知提示
     */
    private class GobackRunnable implements Runnable{

        @Override
        public void run() {

            for(int i=0;i<5;i++){
                actionBack();
            }
        }
    }

}
