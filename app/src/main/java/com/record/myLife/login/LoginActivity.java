package com.record.myLife.login;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.record.bean.IDemoChart;
import com.record.conts.Sofeware;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.main.TodayActivity;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.thread.UploadRunnable;
import com.record.utils.DateTime;
import com.record.utils.GeneralUtils;
import com.record.utils.Md5;
import com.record.utils.NetUtils;
import com.record.utils.Regexp;
import com.record.utils.ToastUtils;
import com.record.utils.UserUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.net.HttpRequestProxy;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.a;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LoginActivity extends BaseActivity {
    static boolean isUseTryUser = false;
    int ACTION_LOGIN = 1;
    int HANDLER_CLOSE_DIALOG = 2;
    int HANDLER_TOAST = 1;
    Button btn_login_login;
    Button btn_login_try2;
    Context context;
    Thread downloadInfoThread = null;
    EditText ed_login_name;
    EditText ed_login_password;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == LoginActivity.this.ACTION_LOGIN) {
                LoginActivity.this.isUseTryUserData((ContentValues) msg.obj);
            }
        }
    };
    Dialog initDialog;
    boolean isLogining = false;
    Thread loginThread = null;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_login_login) {
                LoginActivity.this.clickLogin();
            } else if (id == R.id.tv_login_reg) {
                LoginActivity.this.startActivityForResult(new Intent(LoginActivity.this.context, SignUpActivity.class), 8);
                LoginActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.tv_login_try || id == R.id.btn_login_try2) {
                UserUtils.initTryUser(LoginActivity.this.context);
                LoginActivity.this.sendBroadcast(new Intent(Val.INTENT_ACTION_LOGIN));
                LoginActivity.this.finish();
            } else if (id == R.id.tv_login_find_password) {
                String email = LoginActivity.this.ed_login_name.getText().toString().trim();
                if (email == null) {
                    email = "";
                }
                Intent it = new Intent(LoginActivity.this.context, FindPwActivity_v2.class);
                it.putExtra("userName", email);
                LoginActivity.this.startActivityForResult(it, 17);
            }
        }
    };
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == LoginActivity.this.HANDLER_TOAST) {
                ToastUtils.toastShort(LoginActivity.this.context, (String) msg.obj);
            } else if (msg.arg1 == LoginActivity.this.HANDLER_CLOSE_DIALOG) {
                try {
                    if (LoginActivity.this.initDialog != null && LoginActivity.this.initDialog.isShowing()) {
                        LoginActivity.this.initDialog.cancel();
                    }
                } catch (Exception e) {
                    DbUtils.exceptionHandler(LoginActivity.this.context, e);
                }
            }
        }
    };
    RelativeLayout rl_login_title;
    JSONObject rootJson;
    TextView tv_login_find_password;
    TextView tv_login_reg;
    TextView tv_login_try;

    class downloadInfo implements Runnable {
        downloadInfo() {
        }

        public void run() {
            LoginActivity.this.initUserInfo(LoginActivity.this.rootJson);
            LoginActivity.initGoalsInfo(LoginActivity.this.context, LoginActivity.this.rootJson);
            LoginActivity.this.initGoalStatics(LoginActivity.this.context, LoginActivity.this.rootJson);
            LoginActivity.this.initLabelInfo(LoginActivity.this.context, LoginActivity.this.rootJson);
            LoginActivity.this.initTwoDaysItems(LoginActivity.this.context, LoginActivity.this.rootJson);
            LoginActivity.this.initTwoDaysAllocation(LoginActivity.this.context, LoginActivity.this.rootJson);
            LoginActivity.this.initTwoDaysLabelLink(LoginActivity.this.context, LoginActivity.this.rootJson);
            DbUtils.staticsGoalAll(LoginActivity.this.context);
            LoginActivity.this.initAfterUserLogin();
        }
    }

    class loginRun implements Runnable {
        loginRun() {
        }

        public void run() {
            String http = Sofeware.HTTP_BASE + Sofeware.LOGIN;
            LoginActivity.this.isLogining = true;
            String result = HttpRequestProxy.doPost(http, new HashMap(), "UTF-8");
            LoginActivity.log(result);
            if (result != null) {
                Map<String, Object> map = (Map) JSON.parse(result);
                String error = (String) map.get("error");
                if (error != null) {
                    LoginActivity.this.isLogining = false;
                    if ("password is not correct".equals(error)) {
                        LoginActivity.this.sendMsg("密码不正确！");
                        return;
                    } else if ("account has not existed".equals(error)) {
                        LoginActivity.this.sendMsg("用户名不存在！");
                        return;
                    } else if ("mail address is not valid".equals(error)) {
                        LoginActivity.this.sendMsg("邮箱格式不正确！");
                        return;
                    } else {
                        return;
                    }
                }
                String uid = map.get("uid") + "";
                if (uid == null || uid.length() <= 0) {
                    LoginActivity.this.isLogining = false;
                    LoginActivity.this.sendMsg("登陆失败，请稍候再试!");
                    return;
                }
                String email = (String) map.get("email");
                String password = (String) map.get("password");
                String nickname = (String) map.get("nickname");
                ContentValues values = new ContentValues();
                values.put("uid", uid);
                values.put("userName", email);
                values.put("email", email);
                values.put("nickname", nickname);
                values.put("password", Md5.toMd5(password));
                values.put("isLogin", Integer.valueOf(1));
                Cursor cur = DbUtils.getDb(LoginActivity.this.context).rawQuery("Select * from t_user", null);
                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        String id = cur.getString(cur.getColumnIndex("id"));
                        String username = cur.getString(cur.getColumnIndex("userName"));
                        if (username != null && (username.equals("测试") || username.equals(email))) {
                            LoginActivity.this.initLoginDb();
                            DbUtils.getDb(LoginActivity.this.context).update("t_user", values, "id is ?", new String[]{id});
                            UserUtils.isLoginUser(LoginActivity.this.context);
                            LoginActivity.this.sendBroadcast(new Intent(Val.INTENT_ACTION_LOGIN));
                            LoginActivity.this.sendMsg("登陆成功!");
                            LoginActivity.this.finish();
                            return;
                        }
                    }
                }
                LoginActivity.this.initLoginDb();
                DbUtils.getDb(LoginActivity.this.context).insert("t_user", null, values);
                UserUtils.isLoginUser(LoginActivity.this.context);
                LoginActivity.this.sendBroadcast(new Intent(Val.INTENT_ACTION_LOGIN));
                LoginActivity.this.finish();
                LoginActivity.this.sendMsg("登陆成功!");
                return;
            }
            LoginActivity.this.isLogining = false;
            LoginActivity.this.sendMsg("登陆失败，请稍候再试!");
        }
    }

    class loginRun_v2 implements Runnable {
        String password;
        String userName;

        public loginRun_v2(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        public void run() {
            try {
                String http = Sofeware.HTTP_BASE + Sofeware.LOGIN;
                HashMap<String, String> parameterMap = new HashMap();
                parameterMap.put("token", Sofeware.getToken());
                parameterMap.put("userName", this.userName);
                parameterMap.put("password", this.password);
                LoginActivity.this.isLogining = true;
                String result = HttpRequestProxy.doPost(http, parameterMap, "UTF-8");
                if (result != null) {
                    LoginActivity.this.rootJson = (JSONObject) JSON.parse(result);
                    if (LoginActivity.this.rootJson.getInteger("status").intValue() == 1) {
                        String uid = LoginActivity.this.rootJson.get("uid") + "";
                        if (uid == null || uid.length() <= 0 || uid.equals("null")) {
                            LoginActivity.this.isLogining = false;
                            LoginActivity.this.sendMsg(LoginActivity.this.getString(R.string.str_login_fail1));
                            return;
                        }
                        String userName = LoginActivity.this.rootJson.get("userName") + "";
                        String password = LoginActivity.this.rootJson.get("password") + "";
                        String nickname = LoginActivity.this.rootJson.get("nick") + "";
                        ContentValues values = new ContentValues();
                        values.put("uid", uid);
                        values.put("userName", userName);
                        values.put("password", password);
                        values.put("isLogin", Integer.valueOf(1));
                        values.put("loginTime", DateTime.getTimeString());
                        Message msg = new Message();
                        msg.obj = values;
                        msg.arg1 = LoginActivity.this.ACTION_LOGIN;
                        LoginActivity.this.handler.sendMessage(msg);
                        return;
                    }
                    LoginActivity.this.sendMsg(LoginActivity.this.rootJson.get("msg") + "");
                    return;
                }
                LoginActivity.this.isLogining = false;
                LoginActivity.this.sendMsg(LoginActivity.this.getString(R.string.str_login_fail2));
            } catch (Exception e) {
                e.printStackTrace();
                LoginActivity.this.sendMsg(LoginActivity.this.getString(R.string.str_login_fail2));
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.context = this;
        SystemBarTintManager.setMIUIbar(this);
        initUI();
        initEmail();
    }

    private void initEmail() {
        try {
            Cursor cursor = DbUtils.getDb(this.context).rawQuery("select userName,password from t_user where id is not " + DbUtils.queryTryUserId(this.context) + " order by loginTime desc limit 1", null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                String userName = cursor.getString(cursor.getColumnIndex("userName"));
                String password = cursor.getString(cursor.getColumnIndex("password"));
                if (userName != null && userName.length() > 0 && !userName.equals("测试")) {
                    this.ed_login_name.setText(userName);
                    this.ed_login_password.setText(password);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        isUseTryUser = false;
        this.rl_login_title = (RelativeLayout) findViewById(R.id.rl_login_title);
        this.ed_login_name = (EditText) findViewById(R.id.ed_login_name);
        this.ed_login_password = (EditText) findViewById(R.id.ed_login_password);
        this.btn_login_login = (Button) findViewById(R.id.btn_login_login);
        this.btn_login_try2 = (Button) findViewById(R.id.btn_login_try2);
        this.tv_login_reg = (TextView) findViewById(R.id.tv_login_reg);
        this.tv_login_try = (TextView) findViewById(R.id.tv_login_try);
        this.tv_login_find_password = (TextView) findViewById(R.id.tv_login_find_password);
        this.btn_login_login.setText(getString(R.string.str_sign_up));
        this.rl_login_title.setVisibility(8);
        this.tv_login_reg.setVisibility(0);
        this.btn_login_login.setText(getString(R.string.str_sign_in));
        this.btn_login_login.setOnClickListener(this.myClickListener);
        this.tv_login_reg.setOnClickListener(this.myClickListener);
        this.tv_login_try.setOnClickListener(this.myClickListener);
        this.btn_login_try2.setOnClickListener(this.myClickListener);
        this.tv_login_find_password.setOnClickListener(this.myClickListener);
    }

    private void clickLogin() {
        String email = this.ed_login_name.getText().toString().trim();
        String pass = this.ed_login_password.getText().toString();
        if (email == null || email.length() == 0) {
            ToastUtils.toastShort(this.context, getString(R.string.str_userName_not_null));
        } else if (!email.matches(Regexp.email_regexp)) {
            ToastUtils.toastShort(this.context, getString(R.string.str_email_format_is_not_correct));
        } else if (pass == null || pass.length() == 0) {
            ToastUtils.toastLong(this.context, getString(R.string.str_password_not_null));
        } else if (pass.length() < 6) {
            ToastUtils.toastLong(this.context, getString(R.string.str_password_should_more_than_six));
        } else {
            Cursor cursor = DbUtils.getDb(this.context).rawQuery("select id from t_user where userName = '" + email + "' and password = '" + pass + "'", null);
            if (cursor.getCount() > 0) {
                DbUtils.close(cursor);
                if (NetUtils.isNetworkAvailable2noToast(this.context)) {
                    if (pass.length() > 64) {
                        pass = pass.substring(0, 64);
                    }
                    login(email, pass);
                    return;
                }
                pointUserNameToLogin(email);
                return;
            }
            DbUtils.close(cursor);
            if (NetUtils.isNetworkAvailable(this.context)) {
                if (pass.length() > 64) {
                    pass = pass.substring(0, 64);
                }
                login(email, pass);
            }
        }
    }

    private void pointUserNameToLogin(String email) {
        initLoginDb();
        ContentValues values = new ContentValues();
        values.put("isLogin", Integer.valueOf(1));
        DbUtils.getDb(this.context).update("t_user", values, "userName = '" + email + "'", null);
        UserUtils.isLoginUser(this.context);
        initAfterUserLogin();
    }

    private void initAfterUserLogin() {
        Intent it = new Intent(Val.INTENT_ACTION_LOGIN);
        it.putExtra(Val.INTENT_ACTION_LOGIN, 1);
        sendBroadcast(it);
        sendMsg(getString(R.string.str_login_success));
        closeDialog();
        finish();
    }

    public void login(String email, String pass) {
        ToastUtils.toastLong(this.context, "登陆中...");
        if (this.loginThread == null || !this.loginThread.isAlive()) {
            this.loginThread = new Thread(new loginRun_v2(email, pass));
            this.loginThread.start();
            return;
        }
        ToastUtils.toastLong(this.context, "登陆中,请稍候...");
    }

    private void isUseTryUserData(ContentValues contentValues) {
        if (DbUtils.getDb(this.context).rawQuery("Select id from t_user where userName is '" + contentValues.getAsString("userName") + "'", null).getCount() > 0) {
            initLoginDb();
            DbUtils.getDb(this.context).update("t_user", contentValues, "userName is ?", new String[]{contentValues.getAsString("userName")});
            loginSuccess();
            return;
        }
        int tryUserId = DbUtils.queryTryUserId(this.context);
        if (tryUserId > 0) {
            Cursor cursor = DbUtils.getDb(this.context).rawQuery("select id from t_act where userId = " + tryUserId + " and type = 11", null);
            Cursor cursor2 = DbUtils.getDb(this.context).rawQuery("select id from t_act_item where userId = " + tryUserId + " limit 15", null);
            if (cursor.getCount() > 0 || cursor2.getCount() > 10) {
                showIsUserTryDataDialog(contentValues);
            } else {
                loginDirectly(contentValues);
            }
            DbUtils.close(cursor);
            DbUtils.close(cursor2);
            return;
        }
        loginDirectly(contentValues);
    }

    private void loginDirectly(ContentValues contentValues) {
        initLoginDb();
        DbUtils.getDb(this.context).insert("t_user", null, contentValues);
        loginSuccess();
    }

    private void loginSuccess() {
        UserUtils.isLoginUser(this.context);
        if (this.downloadInfoThread == null) {
            showInitDialog();
            this.downloadInfoThread = new Thread(new downloadInfo());
            this.downloadInfoThread.start();
            GeneralUtils.toastShort(this.context, getString(R.string.str_init));
        }
    }

    private void showInitDialog() {
        try {
            this.initDialog = new Builder(this.context).setTitle(getString(R.string.str_reminder_gentle)).setMessage(getString(R.string.str_syncing2)).create();
            if (!isFinishing()) {
                this.initDialog.show();
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(this.context, e);
        }
    }

    private void closeDialog() {
        Message msg = new Message();
        msg.arg1 = this.HANDLER_CLOSE_DIALOG;
        this.myHandler.sendMessage(msg);
    }

    private void showIsUserTryDataDialog(final ContentValues contentValues) {
        new Builder(this.context).setTitle(getString(R.string.str_prompt)).setMessage(getString(R.string.str_is_use_try_data)).setPositiveButton(getString(R.string.str_used), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LoginActivity.this.initLoginDb();
                DbUtils.getDb(LoginActivity.this.context).update("t_user", contentValues, "userName is '测试'", null);
                LoginActivity.isUseTryUser = true;
                LoginActivity.this.loginSuccess();
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.str_not_use), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LoginActivity.this.initLoginDb();
                DbUtils.getDb(LoginActivity.this.context).insert("t_user", null, contentValues);
                LoginActivity.this.loginSuccess();
                dialog.cancel();
            }
        }).setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == 4) {
                    LoginActivity.this.initLoginDb();
                    DbUtils.getDb(LoginActivity.this.context).insert("t_user", null, contentValues);
                    LoginActivity.this.loginSuccess();
                }
                return false;
            }
        }).create().show();
    }

    private void initUserInfo(JSONObject jsonObject) {
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.getJSONArray("user");
            if (jsonArray != null) {
                Iterator it = jsonArray.iterator();
                while (it.hasNext()) {
                    JSONObject subJson = JSON.parseObject(it.next().toString());
                    if (subJson != null) {
                        String endUpdateTime = subJson.getString("endUpdateTime");
                        ContentValues values = getUserConteValues(this.context, subJson);
                        if (isNotNull(endUpdateTime)) {
                            values.put("endUpdateTime", endUpdateTime);
                        }
                        DbUtils.getDb(this.context).update("t_user", values, "id is ?", new String[]{DbUtils.queryUserId(this.context)});
                    }
                }
            }
        }
    }

    public static ContentValues getUserConteValues(Context context, JSONObject subJson) {
        String nick = subJson.getString("nick");
        String authorization = subJson.getString("authorization");
        String gender = subJson.getString("gender");
        String birthday = subJson.getString("birthday");
        String integral = subJson.getString("integral");
        String profession = subJson.getString("profession");
        String phone = subJson.getString("phone");
        String qq = subJson.getString("qq");
        String investment = subJson.getString("investment");
        String property = subJson.getString("property");
        ContentValues values = new ContentValues();
        if (isNotNull(nick)) {
            values.put("nickname", nick);
        }
        if (isNotNull(gender)) {
            values.put("genderInt", gender);
        }
        if (isNotNull(birthday)) {
            values.put("birthday", birthday);
        }
        if (isNotNull(profession)) {
            values.put("professionInt", profession);
        }
        if (isNotNull(integral)) {
            values.put("integral", integral);
        }
        if (isNotNull(phone)) {
            values.put("phone", phone);
        }
        if (isNotNull(qq)) {
            values.put("qq", qq);
        }
        if (isNotNull(authorization)) {
            values.put("age", authorization);
        }
        if (isNotNull(investment)) {
            values.put("investment", investment);
        }
        if (isNotNull(property)) {
            values.put("property", property);
        }
        values.put("uploadTime", DateTime.getTimeString());
        values.put("isUpload", Integer.valueOf(1));
        return values;
    }

    public static void initGoalsInfo(Context context, JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("goals");
                if (jsonArray != null) {
                    ArrayList<JSONObject> subGoalArr = new ArrayList();
                    Iterator it = jsonArray.iterator();
                    while (it.hasNext()) {
                        insertOrUpdateGoals(context, JSON.parseObject(it.next().toString()), subGoalArr);
                    }
                    if (subGoalArr == null) {
                        return;
                    }
                    if (subGoalArr.size() > 0) {
                        it = subGoalArr.iterator();
                        while (it.hasNext()) {
                            insertOrUpdateGoals(context, (JSONObject) it.next(), null);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void insertOrUpdateGoals(Context context, JSONObject subJson, ArrayList<JSONObject> subGoalArr) {
        if (subJson != null) {
            int sSubGoal = subJson.getIntValue("isSubGoal");
            if (sSubGoal > 0) {
                int bigGoalActId = DbUtils.queryActIdBysGoalId(context, sSubGoal + "");
                if (bigGoalActId > 0) {
                    sSubGoal = bigGoalActId;
                } else if (subGoalArr != null) {
                    subGoalArr.add(subJson);
                    log(sSubGoal + " t_act其大目标未保存，先存到集合里，sSubGoal" + sSubGoal);
                    return;
                } else {
                    sSubGoal = 0;
                    log(0 + "t_act 插入子目标时，大目标因为未知原因没有下载下来,暂时将这个子目标转为大目标");
                }
            }
            int sGoalId = Integer.parseInt(subJson.getString("id").replace(",", ""));
            String endUpdateTime = subJson.getString("endUpdateTime");
            int actId = DbUtils.queryActIdBysGoalId(context, sGoalId + "");
            ContentValues values;
            if (actId <= 0) {
                values = getGoalConteValues(context, subJson);
                if (isNotNull(endUpdateTime)) {
                    values.put("endUpdateTime", endUpdateTime);
                }
                values.put("severId", Integer.valueOf(sGoalId));
                values.put("isSubGoal", Integer.valueOf(sSubGoal));
                int type = values.getAsInteger(a.a).intValue();
                if (type == 10 || type == 20 || type == 30 || type == 40) {
                    int tempActId = DbUtils.queryActIdByType2(context, type + "");
                    if (tempActId > 0) {
                        DbUtils.getDb(context).update("t_act", values, "id = " + tempActId, null);
                        log("t_act 插入目标时，因本地已经存在这个类型，所以进行更新，actId" + tempActId + ",severId:" + sGoalId + ",isSubGoal:" + sSubGoal);
                        return;
                    }
                }
                log("t_act 插入目标完成，actId" + DbUtils.getDb(context).insert("t_act", null, values) + ",severId:" + sGoalId + ",isSubGoal:" + sSubGoal);
            } else if (isNotNull(endUpdateTime)) {
                String localEndUpdate = DbUtils.queryEndUpdateTimeBysGoalId(context, sGoalId + "");
                if (!isNotNull(localEndUpdate)) {
                    log("t_act更新数据时 localEndUpdate为空不修改,localEndUpdate:" + localEndUpdate);
                } else if (DateTime.compare_date(endUpdateTime, localEndUpdate) > 0) {
                    values = getGoalConteValues(context, subJson);
                    if (isNotNull(endUpdateTime)) {
                        values.put("endUpdateTime", endUpdateTime);
                    }
                    values.put("severId", Integer.valueOf(sGoalId));
                    values.put("isSubGoal", Integer.valueOf(sSubGoal));
                    DbUtils.getDb(context).update("t_act", values, "id is ?", new String[]{actId + ""});
                    log("t_act 更新成功，actId" + actId + ",severId:" + sGoalId + ",isSubGoal:" + sSubGoal);
                } else {
                    log("t_act更新数据时,本地数据比较新，不修改,localEndUpdate:" + localEndUpdate + ",endUpdateTime:" + endUpdateTime);
                }
            } else {
                log("t_act更新数据时 endUpdateTime为空不修改,endUpdateTime:" + endUpdateTime);
            }
        }
    }

    public static ContentValues getGoalConteValues(Context context, JSONObject subJson) {
        int sUserId = Integer.parseInt(subJson.getString("userId").replace(",", ""));
        String image = subJson.getString("image");
        String color = subJson.getString("color");
        String goalName = subJson.getString("goalName");
        int type = subJson.getIntValue(a.a);
        String startTime = subJson.getString("startTime");
        String deadline = subJson.getString("deadline");
        String level = subJson.getString("level");
        String timeOfEveryday = subJson.getString("timeOfEveryday");
        String expectSpend = subJson.getString("expectSpend");
        String hadSpend = subJson.getString("hadSpend");
        String hadWaste = subJson.getString("hadWaste");
        String isFinish = subJson.getString("isFinish");
        String isDelete = subJson.getString("isDelete");
        String finishTime = subJson.getString("finishTime");
        String deleteTime = subJson.getString("deleteTime");
        String intruction = subJson.getString("intruction");
        String position = subJson.getString("position");
        String isManuscript = subJson.getString("isManuscript");
        String isDefault = subJson.getString("isDefault");
        String isHided = subJson.getString("isHided");
        int resetCount = subJson.getIntValue("resetCount");
        String createTime = subJson.getString("createTime");
        ContentValues values = new ContentValues();
        values.put("userId", DbUtils.queryUserId(context));
        values.put("image", image);
        values.put("color", color);
        values.put("actName", goalName);
        values.put(a.a, Integer.valueOf(type));
        if (isNotNull(startTime)) {
            values.put("startTime", startTime);
        }
        if (isNotNull(deadline)) {
            values.put("deadline", deadline);
        }
        values.put("level", level);
        values.put("timeOfEveryday", timeOfEveryday);
        values.put("expectSpend", expectSpend);
        values.put("hadSpend", hadSpend);
        values.put("hadWaste", hadWaste);
        values.put("isFinish", isFinish);
        if (isNotNull(finishTime)) {
            values.put("finishTime", finishTime);
        }
        values.put("isDelete", isDelete);
        if (isNotNull(deleteTime)) {
            values.put("deleteTime", deleteTime);
        }
        if (isNotNull(intruction)) {
            values.put("intruction", intruction);
        }
        values.put("position", position);
        values.put("isManuscript", isManuscript);
        values.put("isDefault", isDefault);
        values.put("isUpload", Integer.valueOf(1));
        values.put("isHided", isHided);
        values.put("resetCount", Integer.valueOf(resetCount));
        values.put("uploadTime", DateTime.getTimeString());
        if (isNotNull(createTime)) {
            values.put("createTime", createTime);
        }
        return values;
    }

    public static boolean isNotNull(String time) {
        if (time == null) {
            return false;
        }
        time = time.trim();
        if (time.length() == 0 || time.equalsIgnoreCase("null") || time.equalsIgnoreCase("0000-00-00 00:00:00")) {
            return false;
        }
        return true;
    }

    public static int getInt(JSONObject subJson, String key) {
        int i = 0;
        try {
            return subJson.getIntValue(key);
        } catch (Exception e) {
            e.printStackTrace();
            return i;
        }
    }

    private void initGoalStatics(Context context, JSONObject rootObject) {
        String tableName = "t_goal_statics";
        JSONArray staticsArr = rootObject.getJSONArray("GoalStatics");
        if (staticsArr != null) {
            UploadRunnable.downLoadStaticsFromArray(tableName, context, staticsArr);
        }
    }

    private ContentValues getStaticsContentValue(Context context, JSONObject subJson) {
        ContentValues value = new ContentValues();
        value.put("hadInvest", subJson.getDouble("hadInvest"));
        value.put("todayInvest", subJson.getDouble("todayInvest"));
        value.put("sevenInvest", subJson.getDouble("sevenInvest"));
        return value;
    }

    private void initLabelInfo(Context context, JSONObject rootObject) {
        JSONArray labelArr = rootObject.getJSONArray("labels");
        if (labelArr != null) {
            Iterator it = labelArr.iterator();
            while (it.hasNext()) {
                JSONObject subJson = JSON.parseObject(it.next().toString());
                if (subJson != null) {
                    int sLabelId = subJson.getIntValue("id");
                    int subTypeId = DbUtils.queryLabelIdBysLabelId(context, sLabelId + "").intValue();
                    String endUpdateTime = subJson.getString("endUpdateTime");
                    ContentValues values;
                    if (subTypeId <= 0) {
                        values = getLabelConteValues(context, subJson);
                        if (isNotNull(endUpdateTime)) {
                            values.put("endUpdateTime", endUpdateTime);
                        }
                        values.put("sLabelId", Integer.valueOf(sLabelId));
                        DbUtils.getDb(context).insert("t_sub_type", null, values);
                    } else if (isNotNull(endUpdateTime)) {
                        String localEndUpdate = DbUtils.queryEndUpdateTimeBysLabelId(context, sLabelId + "");
                        if (!isNotNull(localEndUpdate)) {
                            log("标签 更新数据时 localEndUpdate为空不修改,localEndUpdate:" + localEndUpdate);
                        } else if (DateTime.compare_date(endUpdateTime, localEndUpdate) > 0) {
                            values = getLabelConteValues(context, subJson);
                            if (isNotNull(endUpdateTime)) {
                                values.put("endUpdateTime", endUpdateTime);
                            }
                            values.put("sLabelId", Integer.valueOf(sLabelId));
                            DbUtils.getDb(context).update("t_sub_type", values, "id is ?", new String[]{subTypeId + ""});
                            log("标签 更新成功，,sLabelId:" + sLabelId + ",subTypeId:" + subTypeId);
                        } else {
                            log("标签 更新数据时,本地数据比较新，不修改,localEndUpdate:" + localEndUpdate + ",endUpdateTime:" + endUpdateTime);
                        }
                    } else {
                        log("标签 更新数据时 endUpdateTime为空不修改,endUpdateTime:" + endUpdateTime);
                    }
                }
            }
        }
    }

    private ContentValues getLabelConteValues(Context context, JSONObject subJson) {
        if (subJson == null) {
            return null;
        }
        int goalType = subJson.getIntValue("goalType");
        int sGoalId = subJson.getIntValue("goalId");
        int labelType = subJson.getIntValue("labelType");
        int isDelete = subJson.getIntValue("isDelete");
        int labelColor = subJson.getIntValue("labelColor");
        int labelPosition = subJson.getIntValue("labelPosition");
        String name = subJson.getString(IDemoChart.NAME);
        String describe = subJson.getString("describe");
        String lastUseTime = subJson.getString("lastUseTime");
        String createTime = subJson.getString("createTime");
        String deleteTime = subJson.getString("deleteTime");
        if (sGoalId > 0) {
            int goalId = DbUtils.queryActIdBysGoalId(context, sGoalId + "");
            if (goalId > 0) {
                sGoalId = goalId;
            } else {
                sGoalId = 0;
            }
        }
        ContentValues values = new ContentValues();
        values.put("userId", DbUtils.queryUserId(context));
        values.put("actType", Integer.valueOf(goalType));
        values.put("actId", Integer.valueOf(sGoalId));
        values.put("labelType", Integer.valueOf(labelType));
        values.put(IDemoChart.NAME, name);
        if (isNotNull(describe)) {
            values.put("describe", describe);
        }
        if (isNotNull(lastUseTime)) {
            values.put("lastUseTime", lastUseTime);
        }
        values.put("isDelete", Integer.valueOf(isDelete));
        if (isNotNull(createTime)) {
            values.put("time", createTime);
        }
        values.put("labelColor", Integer.valueOf(labelColor));
        values.put("labelPosition", Integer.valueOf(labelPosition));
        if (isNotNull(deleteTime)) {
            values.put("deleteTime", deleteTime);
        }
        values.put("isUpload", Integer.valueOf(1));
        values.put("uploadTime", DateTime.getTimeString());
        return values;
    }

    private void initTwoDaysItems(Context context, JSONObject rootJson) {
        if (rootJson != null) {
            try {
                JSONArray jsonArr = rootJson.getJSONArray("items");
                if (jsonArr != null) {
                    Iterator it = jsonArr.iterator();
                    while (it.hasNext()) {
                        JSONObject subJson = JSON.parseObject(it.next().toString());
                        double sItemIds = subJson.getDoubleValue("id");
                        String endUpdateTime = subJson.getString("endUpdateTime");
                        int itemsId = DbUtils.queryItemsIdBysItemsId(context, sItemIds + "").intValue();
                        ContentValues values;
                        if (itemsId <= 0) {
                            values = getItemsContentValue(context, subJson);
                            if (isNotNull(endUpdateTime)) {
                                values.put("endUpdateTime", endUpdateTime);
                            }
                            values.put("sGoalItemId", Double.valueOf(sItemIds));
                            DbUtils.getDb(context).insert("t_act_item", null, values);
                        } else if (isNotNull(endUpdateTime)) {
                            String localEndUpdate = DbUtils.queryEndUpdateTimeByItemsId(context, itemsId + "");
                            if (!isNotNull(localEndUpdate)) {
                                log("最近两天的记录 更新数据时 localEndUpdate为空不修改,localEndUpdate:" + localEndUpdate);
                            } else if (DateTime.compare_date(endUpdateTime, localEndUpdate) > 0) {
                                values = getItemsContentValue(context, subJson);
                                if (isNotNull(endUpdateTime)) {
                                    values.put("endUpdateTime", endUpdateTime);
                                }
                                values.put("sGoalItemId", Double.valueOf(sItemIds));
                                DbUtils.getDb(context).update("t_act_item", values, "id is ?", new String[]{itemsId + ""});
                                log("最近两天的记录 更新成功，,itemsId:" + itemsId);
                            } else {
                                log("最近两天的记录 更新数据时,本地数据比较新，不修改,localEndUpdate:" + localEndUpdate + ",endUpdateTime:" + endUpdateTime);
                            }
                        } else {
                            log("最近两天的记录 更新数据时 endUpdateTime为空不修改,endUpdateTime:" + endUpdateTime);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static ContentValues getItemsContentValue(Context context, JSONObject subJson) {
        if (subJson == null) {
            return null;
        }
        double sUserId = (double) subJson.getIntValue("userId");
        double sGoalId = (double) subJson.getIntValue("goalId");
        int goalType = subJson.getIntValue("goalType");
        String startTime = subJson.getString("startTime");
        double take = subJson.getDoubleValue("take");
        String stopTime = subJson.getString("stopTime");
        int isEnd = subJson.getIntValue("isEnd");
        int isRecord = subJson.getIntValue("isRecord");
        String remarks = subJson.getString("remarks");
        int isDelete = subJson.getIntValue("isDelete");
        String deleteTime = subJson.getString("deleteTime");
        int actId = DbUtils.queryActIdBysGoalId(context, sGoalId + "");
        ContentValues value = new ContentValues();
        value.put("userId", DbUtils.queryUserId(context));
        value.put("actId", Integer.valueOf(actId));
        value.put("actType", Integer.valueOf(goalType));
        value.put("startTime", startTime);
        value.put("take", Double.valueOf(take));
        if (isNotNull(stopTime)) {
            value.put("stopTime", stopTime);
        }
        value.put("isEnd", Integer.valueOf(isEnd));
        value.put("isRecord", Integer.valueOf(isRecord));
        if (isNotNull(remarks)) {
            value.put("remarks", remarks);
        }
        value.put("isDelete", Integer.valueOf(isDelete));
        if (isNotNull(deleteTime)) {
            value.put("deleteTime", deleteTime);
        }
        value.put("isUpload", Integer.valueOf(1));
        value.put("uploadTime", DateTime.getTimeString());
        return value;
    }

    private void initTwoDaysAllocation(Context context, JSONObject rootJson) {
        if (rootJson != null) {
            try {
                JSONArray jsonArr = rootJson.getJSONArray("allocations");
                if (jsonArr != null) {
                    Iterator it = jsonArr.iterator();
                    while (it.hasNext()) {
                        JSONObject subJson = JSON.parseObject(it.next().toString());
                        String endUpdateTime = subJson.getString("endUpdateTime");
                        TodayActivity.insertOrUpdateDb_allocation(subJson.getString("time"), getAllocationContentValue(context, subJson));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static ContentValues getAllocationContentValue(Context context, JSONObject subJson) {
        if (subJson == null) {
            return null;
        }
        int invest = subJson.getIntValue("invest");
        int waste = subJson.getIntValue("waste");
        int routine = subJson.getIntValue("routine");
        int sleep = subJson.getIntValue("sleep");
        String time = subJson.getString("time");
        String remarks = subJson.getString("remarks");
        String morningVoice = subJson.getString("morningVoice");
        double earnMoney = subJson.getDoubleValue("earnMoney");
        ContentValues values = new ContentValues();
        values.put("userId", DbUtils.queryUserId(context));
        values.put("invest", Integer.valueOf(invest));
        values.put("waste", Integer.valueOf(waste));
        values.put("routine", Integer.valueOf(routine));
        values.put("sleep", Integer.valueOf(sleep));
        values.put("time", time);
        if (isNotNull(remarks)) {
            values.put("remarks", remarks);
        }
        if (isNotNull(morningVoice)) {
            values.put("morningVoice", morningVoice);
        }
        values.put("earnMoney", Double.valueOf(earnMoney));
        values.put("isUpload", Integer.valueOf(1));
        values.put("uploadTime", DateTime.getTimeString());
        return values;
    }

    private void initTwoDaysLabelLink(Context context, JSONObject rootJson) {
        if (rootJson != null) {
            try {
                JSONArray jsonArr = rootJson.getJSONArray("labelLinks");
                if (jsonArr != null) {
                    Iterator it = jsonArr.iterator();
                    while (it.hasNext()) {
                        JSONObject subJson = JSON.parseObject(it.next().toString());
                        String endUpdateTime = subJson.getString("endUpdateTime");
                        Context context2 = context;
                        int labelLinkId = DbUtils.queryLabelLinkIdBysLabelLinkId(context2, subJson.getIntValue("id") + "").intValue();
                        ContentValues values;
                        if (labelLinkId <= 0) {
                            values = getLabelLinkContentValue(context, subJson);
                            if (isNotNull(endUpdateTime)) {
                                values.put("endUpdateTime", endUpdateTime);
                            }
                            DbUtils.getDb(context).insert("t_routine_link", null, values);
                        } else if (isNotNull(endUpdateTime)) {
                            String localEndUpdate = DbUtils.queryEndUpdateTimeByLabelLinksId(context, labelLinkId + "");
                            if (!isNotNull(localEndUpdate)) {
                                log("最近两天的分配 更新数据时 localEndUpdate为空不修改,localEndUpdate:" + localEndUpdate);
                            } else if (DateTime.compare_date(endUpdateTime, localEndUpdate) > 0) {
                                values = getLabelLinkContentValue(context, subJson);
                                if (isNotNull(endUpdateTime)) {
                                    values.put("endUpdateTime", endUpdateTime);
                                }
                                DbUtils.getDb(context).update("t_routine_link", values, " Id is ?", new String[]{labelLinkId + ""});
                                log("最近两天的分配 更新成功，,itemsId:" + labelLinkId);
                            } else {
                                log("最近两天的分配 更新数据时,本地数据比较新，不修改,localEndUpdate:" + localEndUpdate + ",endUpdateTime:" + endUpdateTime);
                            }
                        } else {
                            log("最近两天的分配 更新数据时 endUpdateTime为空不修改,endUpdateTime:" + endUpdateTime);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ContentValues getLabelLinkContentValue(Context context, JSONObject subJson) {
        if (subJson == null) {
            return null;
        }
        int sItemsId = subJson.getIntValue("itemsId");
        int sGoalId = subJson.getIntValue("goalId");
        int goalType = subJson.getIntValue("goalType");
        int take = subJson.getIntValue("take");
        int sLabelId = subJson.getIntValue("labelId");
        String time = subJson.getString("time");
        int isDelete = subJson.getIntValue("isDelete");
        int sLabelLinkId = subJson.getIntValue("id");
        String deleteTime = subJson.getString("deleteTime");
        String endUpdateTime = subJson.getString("endUpdateTime");
        ContentValues values = new ContentValues();
        values.put("userId", DbUtils.queryUserId(context));
        values.put("itemsId", DbUtils.queryItemsIdBysItemsId(context, sItemsId + ""));
        values.put("goalId", Integer.valueOf(DbUtils.queryActIdBysGoalId(context, sGoalId + "")));
        values.put("goalType", Integer.valueOf(goalType));
        values.put("take", Integer.valueOf(take));
        values.put("sLabelLinkId", Integer.valueOf(sLabelLinkId));
        values.put("subTypeId", DbUtils.queryLabelIdBysLabelId(context, sLabelId + ""));
        if (isNotNull(time)) {
            values.put("time", time);
        }
        values.put("isDelete", Integer.valueOf(isDelete));
        if (isNotNull(deleteTime)) {
            values.put("deleteTime", deleteTime);
        }
        if (isNotNull(endUpdateTime)) {
            values.put("endUpdateTime", endUpdateTime);
        }
        values.put("isUpload", Integer.valueOf(1));
        values.put("uploadTime", DateTime.getTimeString());
        return values;
    }

    public void initLoginDb() {
        ContentValues values = new ContentValues();
        values.put("isLogin", Integer.valueOf(0));
        DbUtils.getDb(this.context).update("t_user", values, "isLogin is ?", new String[]{"1"});
    }

    public void sendMsg(String str) {
        Message msg = new Message();
        msg.obj = str;
        msg.arg1 = this.HANDLER_TOAST;
        this.myHandler.sendMessage(msg);
    }

    public void onBackPressed() {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8) {
            if (resultCode == -1) {
                this.ed_login_name.setText(data.getStringExtra("userName"));
                this.ed_login_password.setText(data.getStringExtra("password"));
            }
        } else if (requestCode == 17 && resultCode == -1) {
            String userName = data.getStringExtra("userName");
            String password = data.getStringExtra("password");
            log("userName:" + userName + ",password:" + password);
            setUserNameAndPassword(userName, password);
        }
    }

    private void setUserNameAndPassword(String userName, String password) {
        if (userName != null && userName.length() > 0) {
            String email = this.ed_login_name.getText().toString();
            if (email == null || email.length() == 0 || email.equals(userName)) {
                this.ed_login_name.setText(userName);
                this.ed_login_password.setText(password);
            }
        }
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public static void log(String str) {
        Log.i("override Login", ":" + str);
    }
}
