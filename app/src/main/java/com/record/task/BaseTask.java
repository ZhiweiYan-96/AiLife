package com.record.task;

import android.os.Message;
import com.record.bean.User;
import com.record.bean.net.ResponseBean;
import com.record.controller.Controller;
import com.record.conts.Sofeware;
import com.record.utils.net.SafeJSONObject;
import com.record.utils.net.ServerContactor;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class BaseTask extends AbsTask {
    public static final String ACTIVITY_FLAG = "activity_flag";
    public static final String COMPLETED_ID_FIELD = "requset_success";
    public static final String DATA_ACCESS_WAY_FIELD = "data_from";
    public static final int DATA_FROM_LOCAL = 2;
    public static final int DATA_FROM_SERVER = 1;
    public static final String FAILED_ID_FIELD = "requset_fail";
    public static final String REQUEST_URL_FIELD = "request_url_field";
    public int completedResponseID = 0;
    public int dataAccessType = 1;
    public int failedResponseID = 0;
    private JSONObject orginParams = null;
    public String requestURL = "";

    public BaseTask(Controller controller, JSONObject object) {
        super(controller, object);
    }

    protected void initListener() {
        this.taskListener = new TaskListener() {
            public void onTaskStart(Object result) {
            }

            public void onTaskFail(Object result, Throwable ex) {
                Message msg = BaseTask.this.controller.getOutHandler().obtainMessage();
                msg.what = BaseTask.this.failedResponseID;
                ResponseBean bean = ResponseBean.getFailResponBean();
                bean.params = BaseTask.this.orginParams;
                bean.excObject = ex;
                msg.obj = bean;
                msg.arg2 = SafeJSONObject.getInt(BaseTask.this.params, BaseTask.ACTIVITY_FLAG, -1);
                BaseTask.this.controller.getOutHandler().sendMessage(msg);
            }

            public void onTaskCompleted(Object result) {
                Message msg = BaseTask.this.controller.getOutHandler().obtainMessage();
                msg.what = BaseTask.this.completedResponseID;
                ((ResponseBean) result).params = BaseTask.this.orginParams;
                msg.obj = result;
                msg.arg2 = SafeJSONObject.getInt(BaseTask.this.params, BaseTask.ACTIVITY_FLAG, -1);
                BaseTask.this.controller.getOutHandler().sendMessage(msg);
            }
        };
    }

    protected void initCaller() {
        this.taskCaller = new TaskCaller() {
            public Object execute() throws Exception {
                BaseTask.this.orginParams = new JSONObject(BaseTask.this.params.toString());
                BaseTask.this.initAPI();
                BaseTask.this.initInnerParam();
                List<NameValuePair> postParamList = new ArrayList();
                postParamList.add(new BasicNameValuePair("token", Sofeware.getToken()));
                postParamList.add(new BasicNameValuePair("sUserId", User.getInstance().getUid()));
                postParamList.add(new BasicNameValuePair("userName", User.getInstance().getUserName()));
                postParamList.add(new BasicNameValuePair("data", BaseTask.this.params.toString()));
                String strResult = "";
                if (1 == BaseTask.this.dataAccessType) {
                    strResult = ServerContactor.getResponseStringWithHttpPost(BaseTask.this.requestURL, postParamList);
                } else if (2 == BaseTask.this.dataAccessType) {
                }
                return ResponseBean.getResponBean(strResult);
            }
        };
    }

    private void initAPI() throws JSONException {
        this.completedResponseID = this.params.getInt(COMPLETED_ID_FIELD);
        this.failedResponseID = this.params.getInt(FAILED_ID_FIELD);
        this.requestURL = this.params.getString(REQUEST_URL_FIELD);
        this.params.remove(COMPLETED_ID_FIELD);
        this.params.remove(FAILED_ID_FIELD);
        this.params.remove(REQUEST_URL_FIELD);
    }

    private void initInnerParam() {
        try {
            if (!this.params.isNull(DATA_ACCESS_WAY_FIELD)) {
                this.dataAccessType = this.params.getInt(DATA_ACCESS_WAY_FIELD);
                this.params.remove(DATA_ACCESS_WAY_FIELD);
            }
        } catch (JSONException e) {
            this.dataAccessType = 1;
        }
    }
}
