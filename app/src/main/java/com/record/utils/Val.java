package com.record.utils;

import android.content.Context;
import com.record.myLife.BuildConfig;
import com.record.myLife.R;
import java.util.HashMap;

public class Val {
    public static final String ACTION_TOMOTO_COUNTING = "ACTION_TOMOTO_COUNTING";
    public static final String ACTION_TOMOTO_START = "ACTION_TOMOTO_START";
    public static final String ACTION_TOMOTO_STOP = "ACTION_TOMOTO_STOP";
    public static final String CONFIGURE_ADD_RECORD_TYPE = "CONFIGURE_ADD_RECORD_TYPE";
    public static final int CONFIGURE_ADD_RECORD_TYPE_DEFUALT = 1;
    public static final String CONFIGURE_COUNTER_DOWN_START_TIME = "CONFIGURE_COUNTER_DOWN_START_TIME";
    public static final int CONFIGURE_COUNTER_DOWN_START_TIME_DEFUALT = 0;
    public static final String CONFIGURE_DB_STORE = "CONFIGURE_DB_STORE";
    public static final String CONFIGURE_EVERY_DAY_DOUTHNUT_UINT = "EVERY_DAY_DOUTHNUT_UINT";
    public static final String CONFIGURE_FIND_PW_TIME = "CONFIGURE_FIND_PW_TIME";
    public static final String CONFIGURE_FIND_PW_USERNAME = "CONFIGURE_FIND_PW_USERNAME";
    public static final String CONFIGURE_GOAL_LIST_CURRENT_SELECT = "CONFIGURE_GOAL_LIST_CURRENT_SELECT";
    public static final String CONFIGURE_GOAL_LIST_DEFAULT_UI = "CONFIGURE_GOAL_LIST_DEFAULT_UI";
    public static final String CONFIGURE_GOAL_REMINID_NAME = "CONFIGURE_GOAL_REMINID_NAME";
    public static final String CONFIGURE_IS_ADD_CREATETIME_INTO_GOAL = "CONFIGURE_IS_ADD_CREATETIME_ON_GOAL";
    public static final int CONFIGURE_IS_ADD_CREATETIME_INTO_GOAL_VALUE = 1;
    public static final String CONFIGURE_IS_ADD_DATA_TO_LABEL_LINK = "CONFIGURE_IS_ADD_DATA_TO_LABEL_LINK";
    public static final int CONFIGURE_IS_ADD_DATA_TO_LABEL_LINK_VALUE = 3;
    public static final String CONFIGURE_IS_AUTO_BACKUP_DATA = "AUTO_BACKUP_DATA";
    public static final String CONFIGURE_IS_CHANGE_ICON_BROOM_TO_TRASH = "CONFIGURE_IS_CHANGE_ICON_BROOM_TO_TRASH";
    public static final int CONFIGURE_IS_CHANGE_ICON_BROOM_TO_TRASH_VALUE = 1;
    public static final String CONFIGURE_IS_CHANGE_LABEL_ACTTYPE = "CONFIGURE_IS_CHANGE_LABEL_ACTTYPE";
    public static final int CONFIGURE_IS_CHANGE_LABEL_ACTTYPE_VALUE = 1;
    public static final String CONFIGURE_IS_FILTER_RECORD = "FILTER_RECORD";
    public static final String CONFIGURE_IS_GOAL_SHOW_SIMPLE = "GOAL_SHOW_SIMPLE";
    public static final String CONFIGURE_IS_HAD_NEW_VERSION = "NEW_VERSION";
    public static final String CONFIGURE_IS_HAD_SET_REMIND_REST = "HAD_SET_REMIND_REST";
    public static final String CONFIGURE_IS_MOVE_MYLIFE_TO_ITODAYSS = "CONFIGURE_IS_MOVE_MYLIFE_TO_ITODAYSS";
    public static final int CONFIGURE_IS_MOVE_MYLIFE_TO_ITODAYSS_VALUE = 2;
    public static final String CONFIGURE_IS_NOTI_ADD_SLEEP_DATE = "CONFIGURE_IS_NOTI_ADD_SLEEP_DATE";
    public static final int CONFIGURE_IS_NOTI_ADD_SLEEP_DATE_DEFAULT = 1;
    public static final String CONFIGURE_IS_RECALCULATE_TAKE = "RECALCULATE_TAKE";
    public static final int CONFIGURE_IS_RECALCULATE_TAKE_VALUE = 1;
    public static final String CONFIGURE_IS_REMIND_ADD_MONING_NOTE = "REMIND_ADD_MONING_NOTE";
    public static final String CONFIGURE_IS_REMIND_ADD_NOTE = "REMIND_ADD_NOTE";
    public static final String CONFIGURE_IS_REMIND_CHOOSE_INVEST_LABEL = "REMIND_CHOOSE_INVEST_LABEL";
    public static final String CONFIGURE_IS_REMIND_CHOOSE_LABEL = "REMIND_CHOOSE_LABEL";
    public static final String CONFIGURE_IS_REMIND_CHOOSE_OVERRALL_LABEL = "REMIND_CHOOSE_OVERRALL_LABEL";
    public static final String CONFIGURE_IS_REMIND_CHOOSE_ROUTINE_LABEL = "REMIND_CHOOSE_ROUTINE_LABEL";
    public static final String CONFIGURE_IS_REMIND_CHOOSE_SLEEP_LABEL = "REMIND_CHOOSE_SLEEP_LABEL";
    public static final String CONFIGURE_IS_REMIND_CHOOSE_WASTE_LABEL = "REMIND_CHOOSE_WASTE_LABEL";
    public static final String CONFIGURE_IS_REMIND_INTERVAL = "REMIND_INTERVAL";
    public static final int CONFIGURE_IS_REMIND_INTERVAL_DEFAULT = 0;
    public static final String CONFIGURE_IS_REMIND_REST = "REMIND_REST";
    public static final String CONFIGURE_IS_REMIND_REST_WHOLE = "REMIND_REST_WHOLE";
    public static final String CONFIGURE_IS_RESET_AUTO_BACKUP_WHILE_START = "IS_RESET_AUTO_BACKUP_WHILE_START";
    public static final String CONFIGURE_IS_RESET_REMIND_ADD_NOTE = "IS_RESET_REMIND_ADD_NOTE";
    public static final String CONFIGURE_IS_RING_WHILE_START_COUNTER = "IS_RING_WHILE_START_COUNTER";
    public static final String CONFIGURE_IS_SET_LABEL_POSITION = "CONFIGURE_IS_SET_LABEL_POSITION";
    public static final int CONFIGURE_IS_SET_LABEL_POSITION_VALUE = 8;
    public static final String CONFIGURE_IS_SET_RECORD_ACT_TYPE = "CONFIGURE_IS_SET_RECORD_ACT_TYPE";
    public static final int CONFIGURE_IS_SET_RECORD_ACT_TYPE_VALUE = 5;
    public static final String CONFIGURE_IS_SET_RECORD_BY_TIMER = "CONFIGURE_IS_SET_RECORD_BY_TIMER";
    public static final int CONFIGURE_IS_SET_RECORD_BY_TIMER_VALUE = 1;
    public static final String CONFIGURE_IS_SHAKE_WHILE_START_COUNTER = "IS_SHAKE_WHILE_START_COUNTER";
    public static final int CONFIGURE_IS_SHAKE_WHILE_START_COUNTER_DEFUALT = 0;
    public static final String CONFIGURE_IS_SHOW_ADD_GOAL_GUIDE = "SHOW_ADD_GOAL_GUIDE";
    public static final String CONFIGURE_IS_SHOW_ADD_GOAL_INSIDE_GUIDE = "SHOW_ADD_GOAL_INSIDE_GUIDE";
    public static final int CONFIGURE_IS_SHOW_ADD_GOAL_INSIDE_GUIDE_VALUE = 1;
    public static final String CONFIGURE_IS_SHOW_CHANGE_GOAL_GUIDE = "SHOW_CHANGE_GOAL_GUID";
    public static final int CONFIGURE_IS_SHOW_CHANGE_GOAL_GUIDE_VALUE = 3;
    public static final String CONFIGURE_IS_SHOW_CHANGE_ITEMS_TYPEE = "CONFIGURE_IS_SHOW_CHANGE_ITEMS_TYPEE";
    public static final int CONFIGURE_IS_SHOW_CHANGE_ITEMS_TYPEE_VALUE = 2;
    public static final String CONFIGURE_IS_SHOW_CLICK_GOAL_GUIDE = "SHOW_CLICK_GOAL_GUIDE";
    public static final int CONFIGURE_IS_SHOW_CLICK_GOAL_GUIDE_VALUE = 2;
    public static final String CONFIGURE_IS_SHOW_DIALOG_BACKUP_INSTRUCTION = "CONFIGURE_IS_SHOW_DIALOG_BACKUP_INSTRUCTION";
    public static final int CONFIGURE_IS_SHOW_DIALOG_BACKUP_INSTRUCTION_VALUE = 2;
    public static final String CONFIGURE_IS_SHOW_DIALOG_HISTORY_INSTRUCTION = "CONFIGURE_IS_SHOW_DIALOG_HISTORY_INSTRUCTION";
    public static final int CONFIGURE_IS_SHOW_DIALOG_HISTORY_INSTRUCTION_VALUE = 1;
    public static final String CONFIGURE_IS_SHOW_DIALOG_LABEL_INSTRUCTION = "CONFIGURE_IS_SHOW_DIALOG_LABEL_INSTRUCTION";
    public static final int CONFIGURE_IS_SHOW_DIALOG_LABEL_INSTRUCTION_VALUE = 2;
    public static final String CONFIGURE_IS_SHOW_DIALOG_ME_INSTRUCTION = "CONFIGURE_IS_SHOW_DIALOG_ME_INSTRUCTION";
    public static final int CONFIGURE_IS_SHOW_DIALOG_ME_INSTRUCTION_VALUE = 3;
    public static final String CONFIGURE_IS_SHOW_GENERAL_DOT = "CONFIGURE_IS_SHOW_GENERAL_DOT";
    public static final int CONFIGURE_IS_SHOW_GENERAL_DOT_VAL = 1;
    public static final String CONFIGURE_IS_SHOW_GOAL_CORRECTION = "CONFIGURE_IS_SHOW_GOAL_CORRECTION";
    public static final int CONFIGURE_IS_SHOW_GOAL_CORRECTION_VALUE = 1;
    public static final String CONFIGURE_IS_SHOW_GUIDE = "SHOW_GUIDE";
    public static final String CONFIGURE_IS_SHOW_GUIDE_GETUP = "CONFIGURE_IS_SHOW_GUIDE_GETUP";
    public static final int CONFIGURE_IS_SHOW_GUIDE_GETUP_VALUE = 1;
    public static final String CONFIGURE_IS_SHOW_GUIDE_GOALS_LIST = "CONFIGURE_IS_SHOW_GUIDE_GOALS_LIST";
    public static final int CONFIGURE_IS_SHOW_GUIDE_GOALS_LIST_VALUE = 1;
    public static final String CONFIGURE_IS_SHOW_GUIDE_HISTORY = "CONFIGURE_IS_SHOW_GUIDE_HISTORY";
    public static final int CONFIGURE_IS_SHOW_GUIDE_HISTORY_VALUE = 2;
    public static final String CONFIGURE_IS_SHOW_GUIDE_NEW_ADD_RECORD = "CONFIGURE_IS_SHOW_GUIDE_NEW_ADD_RECORD";
    public static final int CONFIGURE_IS_SHOW_GUIDE_NEW_ADD_RECORD_VALUE = 1;
    public static final String CONFIGURE_IS_SHOW_GUIDE_STATICS = "CONFIGURE_IS_SHOW_GUIDE_STATICS";
    public static final int CONFIGURE_IS_SHOW_GUIDE_STATICS_VALUE = 1;
    public static final int CONFIGURE_IS_SHOW_GUIDE_VALUE = 4;
    public static final String CONFIGURE_IS_SHOW_LABEL_DOT = "SET_LABEL_DOT";
    public static final int CONFIGURE_IS_SHOW_LABEL_DOT_VALUE = 3;
    public static final String CONFIGURE_IS_SHOW_MAIN_HISTORY_DOT = "SHOW_MAIN_HISTORY_DOT";
    public static final int CONFIGURE_IS_SHOW_MAIN_HISTORY_DOT_VAL = 1;
    public static final String CONFIGURE_IS_SHOW_MAIN_SET_DOT = "SHOW_MAIN_SET_DOT";
    public static final int CONFIGURE_IS_SHOW_MAIN_SET_DOT_VAL = 3;
    public static final String CONFIGURE_IS_SHOW_NEED_UPLOADING_TO_UPDATE_GOAL_TIMES = "CONFIGURE_IS_SHOW_NEED_UPLOADING_TO_UPDATE_GOAL_TIMES";
    public static final String CONFIGURE_IS_SHOW_NEED_UPLOADING_TO_UPDATE_GOAL_TIMES2 = "CONFIGURE_IS_SHOW_NEED_UPLOADING_TO_UPDATE_GOAL_TIMES2";
    public static final String CONFIGURE_IS_SHOW_NOTI = "SHOW_NOTI";
    public static final String CONFIGURE_IS_SHOW_UPLOADING_GUIDE = "CONFIGURE_IS_SHOW_UPLOADING_GUIDE";
    public static final int CONFIGURE_IS_SHOW_UPLOADING_GUIDE_VALUE = 1;
    public static final int CONFIGURE_IS_SHOW__ADD_GOAL_GUIDE_VALUE = 2;
    public static final String CONFIGURE_IS_STATISTICS_ALL_GOAL = "CONFIGURE_IS_STATISTICS_ALL_GOAL";
    public static final int CONFIGURE_IS_STATISTICS_ALL_GOAL_VALUE = 5;
    public static final String CONFIGURE_LANGUAGE = "CONFIGURE_LANGUAGE";
    public static final String CONFIGURE_LANGUAGE_DEFAULT = "zh";
    public static final String CONFIGURE_LAST_NOTIFY_ADD_MORNING_TIME = "CONFIGURE_LAST_NOTIFY_ADD_MORNING_TIME";
    public static final String CONFIGURE_LAST_NOTI_ADD_SLEEP_DATE = "CONFIGURE_LAST_NOTI_ADD_SLEEP_DATE";
    public static final String CONFIGURE_LAST_TIME_SELECT_LABEL_TYPE = "CONFIGURE_LAST_TIME_SELECT_LABEL_TYPE";
    public static final String CONFIGURE_LAST_UPLOAD_DATE = "CONFIGURE_LAST_UPLOAD_DATE";
    public static final String CONFIGURE_MORNING_VOICE_PROMPT = "CONFIGURE_MORNING_VOICE_PROMPT";
    public static final String CONFIGURE_NAME = "CONFIGURE";
    public static final String CONFIGURE_NAME_DOT = "CONFIGURE_DOT";
    public static final String CONFIGURE_NOTI_ITEMS_NUMBER = "CONFIGURE_NOTI_ITEMS_NUMBER";
    public static final int CONFIGURE_NOTI_ITEMS_NUMBER_VALUE = 6;
    public static final String CONFIGURE_ORDER_SELECT_LABEL = "CONFIGURE_ORDER_SELECT_LABEL";
    public static final String CONFIGURE_REMIND_ADD_NOTE_TIME = "ADD_NOTE_TIME";
    public static final String CONFIGURE_REMIND_INTERVAL_IS_SHAKE = "REMIND_INTERVAL_IS_SHAKE";
    public static final String CONFIGURE_REMIND_INTERVAL_IS_SHOW_DIALOG = "REMIND_INTERVAL_IS_SHOW_DIALOG";
    public static final String CONFIGURE_REMIND_INTERVAL_IS_SOUND = "REMIND_INTERVAL_IS_SOUND";
    public static final String CONFIGURE_REMIND_INTERVAL_NO_RING_HOUR = "REMIND_INTERVAL_NO_RING_HOUR";
    public static final String CONFIGURE_REMIND_INTERVAL_PROMPT = "CONFIGURE_REMIND_INTERVAL_PROMPT";
    public static final String CONFIGURE_REMIND_INTERVAL_VAL = "REMIND_INTERVAL_VALUE";
    public static final int CONFIGURE_REMIND_INTERVAL_VAL_DEFUALT = 30;
    public static final String CONFIGURE_REMIND_MORNING_VOICE_TIME = "REMIND_MORNING_VOICE_TIME";
    public static final String CONFIGURE_REMIND_MORNING_VOICE_TIME_DEFAULT = "07:00";
    public static final String CONFIGURE_REMIND_REST_CLASS_OVER = "REMIND_REST_CLASS_OVER";
    public static final String CONFIGURE_REMIND_REST_CLASS_OVER_RING = "REMIND_REST_CLASS_OVER_RING";
    public static final String CONFIGURE_REMIND_REST_CLASS_START = "REMIND_REST_CLASS_START";
    public static final String CONFIGURE_REMIND_REST_CLASS_START_RING = "REMIND_REST_CLASS_START_RING";
    public static final String CONFIGURE_REMIND_REST_LEARN_TIME = "LEARN_TIME";
    public static final String CONFIGURE_REMIND_REST_REST_TIME = "REST_TIME";
    public static final String CONFIGURE_REMIND_REST_START_TIME = "REMIND_REST_START_TIME";
    public static final String CONFIGURE_SET_BACK_UP = "SET_BACK_UP_DOT";
    public static final int CONFIGURE_SET_BACK_UP_VALUE = 1;
    public static final String CONFIGURE_SET_REMIND_DOT = "SET_REMIND_DOT";
    public static final int CONFIGURE_SET_REMIND_VALUE = 5;
    public static final String CONFIGURE_START_DATE_OF_WEEK = "CONFIGURE_START_DATE_OF_WEEK";
    public static final int CONFIGURE_START_DATE_OF_WEEK_DEFAULT_VALUE = 2;
    public static final String CONFIGURE_STATICS_BUTTON_LOCATION = "CONFIGURE_STATICS_BUTTON_LOCATION";
    public static final String CONFIGURE_STATICS_PIE_TYPE = "CONFIGURE_STATICS_PIE_TYPE";
    public static final String CONFIGURE_SUMMARIZE_PROMPT = "CONFIGURE_SUMMARIZE_PROMPT";
    public static final String CONFIGURE_TOMATO_DELAY_SECOND = "CONFIGURE_TOMATO_DELAY_SECOND";
    public static final String CONFIGURE_TOMATO_IS_REST_TIME_OUT_RING = "CONFIGURE_TOMATO_IS_REST_TIME_OUT_RING";
    public static final String CONFIGURE_TOMATO_IS_RING = "CONFIGURE_TOMATO_IS_RING";
    public static final int CONFIGURE_TOMATO_IS_RING_DEFAULT = 1;
    public static final String CONFIGURE_TOMATO_IS_SCREEN_LIGHT_KEEP_ON = "CONFIGURE_TOMATO_IS_SCREEN_LIGHT_KEEP_ON";
    public static final String CONFIGURE_TOMATO_IS_STUDY_TIME_OUT_RING = "CONFIGURE_TOMATO_IS_STUDY_TIME_OUT_RING";
    public static final String CONFIGURE_TOMATO_IS_VIBRATOR = "CONFIGURE_TOMATO_IS_VIBRATOR";
    public static final int CONFIGURE_TOMATO_IS_VIBRATOR_DEFAULT = 1;
    public static final String CONFIGURE_TOMATO_LEARN_TIME_OUT_RINGTONE = "CONFIGURE_TOMATO_LEARN_TIME_OUT_RINGTONE";
    public static final String CONFIGURE_TOMATO_REST_TIME = "CONFIGURE_TOMATO_REST_TIME";
    public static final String CONFIGURE_TOMATO_REST_TIME_OUT_RINGTONE = "CONFIGURE_TOMATO_REST_TIME_OUT_RINGTONE";
    public static final String CONFIGURE_TOMATO_START_TIME = "CONFIGURE_TOMATO_START_TIME";
    public static final String CONFIGURE_TOMATO_STUDY_TIME = "CONFIGURE_TOMATO_STUDY_TIME";
    public static final int CONFIGURE_TOMATO_STUDY_TIME_DEFAULT = 25;
    public static final String CONFIGURE_TOMATO_TYPE = "CONFIGURE_TOMATO_TYPE";
    public static final String CONFIGURE_TYPE_IS_SHOW_EMPTY_RECORD_IN_TIMELINE = "CONFIGURE_TYPE_IS_SHOW_EMPTY_RECORD_IN_TIMELINE";
    public static final int CONFIGURE_TYPE_IS_SHOW_EMPTY_RECORD_IN_TIMELINE_HIDE = 0;
    public static final int CONFIGURE_TYPE_IS_SHOW_EMPTY_RECORD_IN_TIMELINE_SHOW = 1;
    public static final String CONFIGURE_TYPE_OF_EXIT_APP = "CONFIGURE_TOMATO_IS_EXIT_APP_FROM_TOMATO";
    public static final int CONFIGURE_TYPE_OF_EXIT_APP_DEFUALT = 0;
    public static final int CONFIGURE_TYPE_OF_EXIT_APP_TOMATO = 1;
    public static final String CONFIGURE_UPDATE_WIDGETS_INTERAL = "CONFIGURE_UPDATE_WIDGETS_INTERAL";
    public static final String CONFIGURE_UPLOAD_NET_TYPE = "CONFIGURE_UPLOAD_NET_TYPE";
    public static String DB_NAME = "mylife_db";
    public static String DB_PATH = "/data/data/com.record.myLife/databases/";
    public static int DEFAULT_UPDATE_WIDGET_INTERVAL = 30;
    public static String FileAPKName = "itoday.apk";
    public static String FilePath_SD = "/itodayss/";
    public static String FilePath_SD2 = "itodayss";
    public static String GET_VERSION_CODE_URL = "http://itoday.sinaapp.com/api/getvercode.php";
    public static String GET_VERSION_URL = "http://itoday.sinaapp.com/api/getversion.php";
    public static String HTTP_BASE = "http://localhost/itoday/1/api/";
    public static final String INTENT_ACTION_ACT = "INTENT_ACTION_ACT";
    public static final String INTENT_ACTION_ALARM_SEND = "ALARM_SEND";
    public static final String INTENT_ACTION_AUTO_BACKUP_ERROR = "INTENT_ACTION_AUTO_BACKUP_ERROR";
    public static final String INTENT_ACTION_AUTO_BACKUP_FINISH = "INTENT_ACTION_AUTO_BACKUP_FINISH";
    public static final String INTENT_ACTION_AUTO_BACK_UP_DATA = "AUTO_BACK_UP_DATA";
    public static final String INTENT_ACTION_BACKUP_TOAST = "BACKUP_TOAST";
    public static final String INTENT_ACTION_EDIT_USER_INFO = "INTENT_ACTION_EDIT_USER_INFO";
    public static final String INTENT_ACTION_ITEMS = "INTENT_ACTION_ITEMS";
    public static final int INTENT_ACTION_LEARN_RINGTONE_PICKED = 22;
    public static final String INTENT_ACTION_LOGIN = "LOGIN";
    public static final String INTENT_ACTION_MODIFY_ACTCOUNT = "MODIFY_ACTCOUNT";
    public static final String INTENT_ACTION_NEW_VERSION = "ACTION_NEW_VERSION";
    public static final String INTENT_ACTION_NOTI_ADD_SLEEP_TIME_FOR_ADD = "INTENT_ACTION_NOTI_ADD_SLEEP_TIME_FOR_ADD";
    public static final String INTENT_ACTION_NOTI_ADD_SLEEP_TIME_FOR_EDIT = "INTENT_ACTION_NOTI_ADD_SLEEP_TIME_FOR_EDIT";
    public static final String INTENT_ACTION_NOTI_MORNING_VOICE = "INTENT_ACTION_NOTI_MORNING_VOICE";
    public static final String INTENT_ACTION_NOTI_RESTROPECTION_REGISTER = "NOTI_RESTROPECTION_REGISTER";
    public static final String INTENT_ACTION_REMIND_INTERVAL = "INTENT_ACTION_REMIND_INTERVAL";
    public static final String INTENT_ACTION_REMIND_TOMOTO_CONTINUE = "INTENT_ACTION_REMIND_TOMOTO_CONTINUE";
    public static final String INTENT_ACTION_REMIND_TOMOTO_REST_TIME_OUT = "INTENT_ACTION_REMIND_TOMOTO_REST_TIME_OUT";
    public static final String INTENT_ACTION_REMIND_TOMOTO_STOP = "INTENT_ACTION_REMIND_TOMOTO_STOP";
    public static final String INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT = "INTENT_ACTION_REMIND_TOMOTO_STUDY_TIME_OUT";
    public static final int INTENT_ACTION_REST_RINGTONE_PICKED = 23;
    public static final String INTENT_ACTION_SHOW_TOAST = "INTENT_ACTION_SHOW_TOAST";
    public static final String INTENT_ACTION_START_COUNTER = "START_COUNTER";
    public static final String INTENT_ACTION_STOP_COUNTER = "STOP_COUNTER";
    public static final int INTENT_ACTION_TOMATO_LEARN_RINGTONE_PICKED = 19;
    public static final int INTENT_ACTION_TOMATO_REST_RINGTONE_PICKED = 20;
    public static final String INTENT_ACTION_UPDATE_MAIN_GOAL_LIST = "INTENT_ACTION_UPDATE_MAIN_GOAL_LIST";
    public static final String INTENT_ACTION_UPDATE_UI_GOAL = "UPDATE_UI_GOAL";
    public static final String INTENT_ACTION_UPDATE_UI_MAIN_COUNTER = "COUNTER";
    public static final String INTENT_ACTION_UPDATE_UI_MAIN_START = "UI_START";
    public static final String INTENT_ACTION_UPDATE_UI_MAIN_STOP = "UI_STOP";
    public static final String INTENT_ACTION_UPDATE_UI_TODAY = "UPDATE_UI_TODAY";
    public static final String INTENT_ACTION_WEIBO_SEND_STATUS = "WEIBO_SEND_STATUS";
    public static final String INTENT_ACTION_WIDGET_CHANGE_UPDATE_WIDGET_INTERVAL = "INTENT_ACTION_WIDGET_CHANGE_UPDATE_WIDGET_INTERVAL";
    public static final String INTENT_ACTION_WIDGET_START_STOP_TIMER = "INTENT_ACTION_WIDGET_START_STOP_TIMER";
    public static final String INTENT_ACTION_WIDGET_TEST1 = "INTENT_ACTION_WIDGET_TEST1";
    public static final String INTENT_ACTION_WIDGET_UPDATE_BAR_UI = "WIDGET_UPDATE_BAR_UI";
    public static final int INTENT_ADD_ACTIVITY = 4;
    public static final int INTENT_CHANGE_ACTIVITY = 5;
    public static final int INTENT_CHANGE_ACTIVITY_SUCCESSFUL = 7;
    public static final int INTENT_REQUEST_EDIT_RECORD_TIME = 24;
    public static final int INTENT_REQUEST_GET_LABEL = 25;
    public static final int INTENT_REQUEST_SHOW_CALENDAR = 26;
    public static final int INTENT_REQUEST_SHOW_DIGIT_ADD_RECORD = 27;
    public static final int INTENT_RESUEST_ADD_TODAY_NOTE = 12;
    public static final int INTENT_RESUEST_FIND_PW = 17;
    public static final int INTENT_RESUEST_GOAL_LIST = 13;
    public static final int INTENT_RESUEST_REGISTER = 8;
    public static final int INTENT_RESUEST_WATCH_GOAL_ACTIVITY = 14;
    public static final int INTENT_RESULT_ADD_NEW_RECORD = 28;
    public static final int INTENT_RESULT_CODE_RESTART_ADD_ACTIVITY = 15;
    public static final int INTENT_RESULT_DELETE_ACT = 8;
    public static final int INTENT_RESULT_DELETE_ITEMS = 10;
    public static final int INTENT_RESULT_MODIFY_ITEMS_START_TIME = 11;
    public static final int INTENT_RESULT_START_GUIDE_ACTIVITY = 16;
    public static final int INTENT_RESULT_UPDATE_UI = 3;
    public static final int INTENT_RESULT_UPDATE_UI_ALL = 6;
    public static final int INTENT_SELECT_ACT = 1;
    public static final int INTENT_SELECT_ITEM = 2;
    public static final int MESSAGE_ACTION_TOAST = 100;
    public static final int MESSAGE_ACTION_UPLOAD_COMPLETE = 101;
    public static final int MESSAGE_ACTION_UPLOAD_FAILED = 102;
    public static final int MESSAGE_ACTION_UPLOAD_PROGRESS = 103;
    public static int NOTI_ID_DOWNLOAD = 1;
    public static int NOTI_ID_ITEMS = 2;
    public static String PREFERENCE_COUNTER_NAME = "COUNTER_NAME";
    public static String PREFERENCE_ENDTIME = "ENDTIME";
    public static String PREFERENCE_STARTTIME = "STARTTIME";
    public static String SD_BACKUP_DIR = "itodayss";
    public static String SD_BACKUP_NAME = "data_backup";
    public static String SD_BACKUP_NAME2 = "data_backup.backup";
    public static int STATISTICS_TYPE_ALL = 2;
    public static int STATISTICS_TYPE_COMPARE_LOC_SER = 5;
    public static int STATISTICS_TYPE_COMPARE_LOC_SUB = 6;
    public static int STATISTICS_TYPE_SERVER = 3;
    public static int STATISTICS_TYPE_SUB_FINISH_IN_SERVER = 4;
    public static int STATISTICS_TYPE_UNUPLOAD = 1;
    public static final int TRANSPOST_DB_FROM_SD_TO_SYS = 1;
    public static final int TRANSPOST_DB_FROM_SYS_TO_SD = 2;
    public static int actCount = 0;
    public static int actItemsId = 0;
    public static HashMap<Integer, String> col_Int2Str_Map = new HashMap();
    public static HashMap<String, Integer> col_Str2Ic_72_Map = new HashMap();
    public static HashMap<String, Integer> col_Str2Int_Map = new HashMap();
    public static HashMap<String, Integer> col_Str2xml_circle_Int_Map = new HashMap();
    public static String[] col_Str_Arr = new String[]{"bg_green1", "bg_green2", "bg_green3", "bg_yellow1", "bg_yellow2", "bg_yellow3", "bg_blue1", "bg_blue2", "bg_blue3", "bg_red1", "bg_red2", "bg_red3"};
    public static int[] col_int_72_Arr = new int[]{R.drawable.ic_circle_72x72_green_1, R.drawable.ic_circle_72x72_green_2, R.drawable.ic_circle_72x72_green_3, R.drawable.ic_circle_72x72_yellow_1, R.drawable.ic_circle_72x72_yellow_2, R.drawable.ic_circle_72x72_yellow_3, R.drawable.ic_circle_72x72_blue_1, R.drawable.ic_circle_72x72_blue_2, R.drawable.ic_circle_72x72_blue_3, R.drawable.ic_circle_72x72_red_1, R.drawable.ic_circle_72x72_red_2, R.drawable.ic_circle_72x72_red_3};
    public static int[] col_int_Arr = new int[]{R.color.bg_green1, R.color.bg_green2, R.color.bg_green3, R.color.bg_yellow1, R.color.bg_yellow2, R.color.bg_yellow3, R.color.bg_blue1, R.color.bg_blue2, R.color.bg_blue3, R.color.bg_red1, R.color.bg_red2, R.color.bg_red3};
    public static int[] col_int_circle_Arr = new int[]{R.drawable.x_circle_green_1, R.drawable.x_circle_green_2, R.drawable.x_circle_green_3, R.drawable.x_circle_yellow_1, R.drawable.x_circle_yellow_2, R.drawable.x_circle_yellow_3, R.drawable.x_circle_blue_1, R.drawable.x_circle_blue_2, R.drawable.x_circle_blue_3, R.drawable.x_circle_red_1, R.drawable.x_circle_red_2, R.drawable.x_circle_red_3};
    public static String contact = "有好的建议可联系 QQ:624604006";
    public static String info = "爱今天--让你更高效利用时间。\n本软件永久费,并会一直支持。\n感谢您的支持！";
    public static String ins1 = "\"　　人们眼中的天才之所以卓越非凡，并非天资超人一等，而是付出了持续不断的努力。只要经过10000万小时的锤炼，任何人都能从平凡变成超凡。--《异类》\"";
    public static String ins2 = "\"　　没有例外之人。没有人仅用3000小时就能达到世界级水准；7500小时也不行；一定要10000小时——10年，每天3小时——无论你是谁。--《一万小时天才理论》\"";
    public static String ins_invest = "任何对目标产生实际帮助花费的时间。比如，你目标是英语考第一名，则你背一个小时的单词，做半个钟的阅读，这些时间花费都是投资;添加目标后投资自动隐藏，目标花费的时间归为投资时间。";
    public static String ins_routine = "对目标没产生实际帮助的时间。比如：做无挑战的工作，上下班走路，做饭吃饭洗澡做家务上厕所等等，这些都归为日常固定支出。突发事件若对目标没实际帮助也可算成固定支出。";
    public static String ins_sleep = "每天睡觉的时间。比如：你晚上10点睡，第二天早上6点起床，则理论上睡了8个钟头。系统将每天第一个起床时间为当日起床时间。从每天00:00:00到23:59:59当作一天计算每天时间分配。";
    public static String ins_waste = "即不是必须花费的对你没产生任何实际帮助的时间。比如：玩游戏、逛街、闲聊等对目标没有产生实际帮助的归为此类。系统会自动将未计算时间归入浪费时间，但用户仍然可以记录浪费时间，方便以后查阅。";
    public static boolean isInsertDb = false;
    public static HashMap<String, Integer> label2IntMap2 = new HashMap();
    public static HashMap<Integer, String> label2NameMap2 = new HashMap();
    public static int[] labelIdArr2 = new int[]{R.drawable.ic_label_page, R.drawable.ic_label_page_pen, R.drawable.ic_label_feather_pen, R.drawable.ic_label_java_pen, R.drawable.ic_label_book_open, R.drawable.ic_label_book_text, R.drawable.ic_label_book_abc, R.drawable.ic_label_book_mark, R.drawable.ic_label_book_note, R.drawable.ic_label_books, R.drawable.ic_label_books_group, R.drawable.ic_label_math, R.drawable.ic_label_student_filled, R.drawable.ic_label_university, R.drawable.ic_label_design, R.drawable.ic_label_lab, R.drawable.ic_label_computer, R.drawable.ic_label_iphone, R.drawable.ic_label_earphone, R.drawable.ic_label_guitar, R.drawable.ic_label_music, R.drawable.ic_label_film, R.drawable.ic_label_film_reel, R.drawable.ic_label_globe, R.drawable.ic_label_tea, R.drawable.ic_label_java, R.drawable.ic_label_rest, R.drawable.ic_label_diningroom, R.drawable.ic_label_hammer, R.drawable.ic_label_wrench, R.drawable.ic_label_idea, R.drawable.ic_label_beerbottle, R.drawable.ic_label_roadsign, R.drawable.ic_label_car, R.drawable.ic_label_bed, R.drawable.ic_label_hangbag, R.drawable.ic_label_desklamp, R.drawable.ic_label_trash, R.drawable.ic_label_broom, R.drawable.ic_label_basketball, R.drawable.ic_label_dumbbell, R.drawable.ic_label_tricycle, R.drawable.ic_label_hand_biceps, R.drawable.ic_label_guru, R.drawable.ic_label_walking, R.drawable.ic_label_running, R.drawable.ic_label_yoga, R.drawable.ic_label_football, R.drawable.ic_label_swimming, R.drawable.ic_label_padding, R.drawable.ic_label_medal_number1, R.drawable.ic_label_targit, R.drawable.ic_label_apple, R.drawable.ic_label_tree, R.drawable.ic_label_clover, R.drawable.ic_label_citrus};
    public static String[] labelNameArr2 = new String[]{"page", "page_pen", "feather_pen", "java_pen", "book_open", "book_text", "book_abc", "book_mark", "book_note", "books", "books_group", "math", "student_filled", "university", "design", "lab", "computer", "iphone", "earphone", "guitar", "music", "film", "film_reel", "globe", "tea", "java", "rest", "diningroom", "hammer", "wrench", "idea", "beerbottle", "roadsign", "car", "bed", "hangbag", "desklamp", "trash", "broom", "basketball", "dumbbell", "tricycle", "hand_biceps", "guru", "walking", "running", "yoga", "football", "swimming", "padding", "medal_number1", "targit", "apple", "tree", "clover", "citrus"};
    public static String packageName = BuildConfig.APPLICATION_ID;
    public static String picFilePath = "";
    public static String slogan = "让时间更高效";
    public static String slogan1 = "让你的时间更高效";
    public static String slogan2 = "更高效的时间利用";
    public static String slogan3 = "让你更高效规划时间";
    public static String versionUrl = "";

    public interface SHARE_STRING {
        public static final String WEIBO_FILED_GOAL_DEADLINE = "{目标期限}";
        public static final String WEIBO_FILED_GOAL_HAD_INVEST = "{已经投入时间}";
        public static final String WEIBO_FILED_GOAL_NAME = "{目标名称}";
        public static final String WEIBO_FILED_GOAL_SEVEN = "{7天平均投入}";
        public static final String WEIBO_FILED_TODAY = "{今天投入时间}";
        public static final String weiboDefault = "#爱今天#今天我已经通过 爱今天 在目标上投入了{今天投入时间}，距离目标更进一步。@爱今天APP";
        public static final String weiboDefault2 = "#爱今天#@爱今天APP";
        public static final String weiboEvery = "#爱今天#我的一天是这样过的，过来瞧瞧吧。@爱今天APP";
        public static final String weiboGoal = "#爱今天#我勇敢地设定一个目标：{目标名称}，目标期限是{目标期限}，最近7天平均每天投入{7天平均投入}，目前已在目标上投入了{已经投入时间}。@爱今天APP";
        public static final String weiboGoalNoDeadline = "#爱今天#我设定一个目标：{目标名称}，最近7天每天投入{7天平均投入}，目前已在目标上投入了{已经投入时间}。@爱今天APP";
        public static final String weiboHistory = "#爱今天#看看我的时间记录。@爱今天APP";
        public static final String weiboSleep = "#爱今天#这是最近的起床时间。@爱今天APP";
    }

    static {
        for (int i = 0; i < col_int_Arr.length; i++) {
            col_Str2Int_Map.put(col_Str_Arr[i], Integer.valueOf(col_int_Arr[i]));
            col_Int2Str_Map.put(Integer.valueOf(col_int_Arr[i]), col_Str_Arr[i]);
            col_Str2xml_circle_Int_Map.put(col_Str_Arr[i], Integer.valueOf(col_int_circle_Arr[i]));
            col_Str2Ic_72_Map.put(col_Str_Arr[i], Integer.valueOf(col_int_72_Arr[i]));
        }
    }

    public static String getPassword() {
        return "e0s48I51Ol63ayhm";
    }

    public static String getAuthPassword() {
        return "asoioidfn2389wjd";
    }

    public static HashMap<String, Integer> getCol_Str2Int_Map() {
        if (col_Str2Int_Map == null) {
            setMap();
        }
        return col_Str2Int_Map;
    }

    public static void setMap() {
        if (col_Str2Int_Map == null) {
            col_Str2Int_Map = new HashMap();
        }
        if (col_Int2Str_Map == null) {
            col_Int2Str_Map = new HashMap();
        }
        if (col_Str2xml_circle_Int_Map == null) {
            col_Str2xml_circle_Int_Map = new HashMap();
        }
        if (col_Str2Ic_72_Map == null) {
            col_Str2Ic_72_Map = new HashMap();
        }
        for (int i = 0; i < col_int_Arr.length; i++) {
            col_Str2Int_Map.put(col_Str_Arr[i], Integer.valueOf(col_int_Arr[i]));
            col_Int2Str_Map.put(Integer.valueOf(col_int_Arr[i]), col_Str_Arr[i]);
            col_Str2xml_circle_Int_Map.put(col_Str_Arr[i], Integer.valueOf(col_int_circle_Arr[i]));
            col_Str2Ic_72_Map.put(col_Str_Arr[i], Integer.valueOf(col_int_72_Arr[i]));
        }
    }

    public static void initLabeMap() {
        if (label2IntMap2 == null) {
            label2IntMap2 = new HashMap();
        }
        if (label2NameMap2 == null) {
            label2NameMap2 = new HashMap();
        }
        if (label2IntMap2.size() == 0 || label2NameMap2.size() == 0) {
            for (int i = 0; i < labelIdArr2.length; i++) {
                label2IntMap2.put(labelNameArr2[i], Integer.valueOf(labelIdArr2[i]));
                label2NameMap2.put(Integer.valueOf(labelIdArr2[i]), labelNameArr2[i]);
            }
        }
    }

    public static int getLabelIntByName(String name) {
        initLabeMap();
        try {
            int id = ((Integer) label2IntMap2.get(name)).intValue();
            if (id == 0) {
                return labelIdArr2[35];
            }
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return labelIdArr2[35];
        }
    }

    public static String getLabelNameById(int id) {
        initLabeMap();
        String name = "";
        try {
            name = (String) label2NameMap2.get(Integer.valueOf(id));
            if (name == null || name.length() == 0) {
                return "desklamp";
            }
            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return "desklamp";
        }
    }

    public static String[] getWeeKNextRankNameAndTime(Context context, int weekInvest) {
        String[] arr = new String[4];
        int nextRateNeed = 0;
        String rank = context.getString(R.string.str_rate10);
        String nextRank = context.getString(R.string.str_rate10);
        int color = context.getResources().getColor(R.color.black_tran_es);
        if (weekInvest < 36000) {
            rank = context.getString(R.string.str_rate10);
            nextRank = context.getString(R.string.str_rate9);
            nextRateNeed = 36000 - weekInvest;
            color = context.getResources().getColor(R.color.black_tran_es);
        } else if (weekInvest >= 36000 && weekInvest < 54000) {
            rank = context.getString(R.string.str_rate9);
            nextRateNeed = 54000 - weekInvest;
            nextRank = context.getString(R.string.str_rate8);
            color = context.getResources().getColor(R.color.bg_yellow1);
        } else if (weekInvest >= 54000 && weekInvest < 72000) {
            rank = context.getString(R.string.str_rate8);
            nextRateNeed = 72000 - weekInvest;
            nextRank = context.getString(R.string.str_rate7);
            color = context.getResources().getColor(R.color.bg_yellow2);
        } else if (weekInvest >= 72000 && weekInvest < 90000) {
            rank = context.getString(R.string.str_rate7);
            nextRateNeed = 90000 - weekInvest;
            nextRank = context.getString(R.string.str_rate6);
            color = context.getResources().getColor(R.color.bg_yellow3);
        } else if (weekInvest >= 90000 && weekInvest < 108000) {
            rank = context.getString(R.string.str_rate6);
            nextRateNeed = 108000 - weekInvest;
            nextRank = context.getString(R.string.str_rate5);
            color = context.getResources().getColor(R.color.bg_blue1);
        } else if (weekInvest >= 108000 && weekInvest < 126000) {
            rank = context.getString(R.string.str_rate5);
            nextRateNeed = 126000 - weekInvest;
            nextRank = context.getString(R.string.str_rate4);
            color = context.getResources().getColor(R.color.bg_blue2);
        } else if (weekInvest >= 126000 && weekInvest < 144000) {
            rank = context.getString(R.string.str_rate4);
            nextRateNeed = 144000 - weekInvest;
            nextRank = context.getString(R.string.str_rate3);
            color = context.getResources().getColor(R.color.bg_blue3);
        } else if (weekInvest >= 144000 && weekInvest < 194400) {
            rank = context.getString(R.string.str_rate3);
            nextRateNeed = 194400 - weekInvest;
            nextRank = context.getString(R.string.str_rate2);
            color = context.getResources().getColor(R.color.bg_green1);
        } else if (weekInvest >= 194400 && weekInvest < 216000) {
            rank = context.getString(R.string.str_rate2);
            nextRateNeed = 216000 - weekInvest;
            nextRank = context.getString(R.string.str_rate1);
            color = context.getResources().getColor(R.color.bg_green2);
        } else if (weekInvest >= 216000) {
            rank = context.getString(R.string.str_rate1);
            nextRateNeed = -1;
            nextRank = "";
            color = context.getResources().getColor(R.color.bg_green3);
        }
        arr[0] = rank;
        arr[1] = nextRank;
        arr[2] = nextRateNeed + "";
        arr[3] = color + "";
        return arr;
    }

    public static String getWeekRank(Context context, int weekInvest) {
        String rank = context.getString(R.string.str_rate10);
        if (weekInvest < 36000) {
            return context.getString(R.string.str_rate10);
        }
        if (weekInvest >= 36000 && weekInvest < 54000) {
            return context.getString(R.string.str_rate9);
        }
        if (weekInvest >= 54000 && weekInvest < 72000) {
            return context.getString(R.string.str_rate8);
        }
        if (weekInvest >= 72000 && weekInvest < 90000) {
            return context.getString(R.string.str_rate7);
        }
        if (weekInvest >= 90000 && weekInvest < 108000) {
            return context.getString(R.string.str_rate6);
        }
        if (weekInvest >= 108000 && weekInvest < 126000) {
            return context.getString(R.string.str_rate5);
        }
        if (weekInvest >= 126000 && weekInvest < 144000) {
            return context.getString(R.string.str_rate4);
        }
        if (weekInvest >= 144000 && weekInvest < 194400) {
            return context.getString(R.string.str_rate3);
        }
        if (weekInvest >= 194400 && weekInvest < 216000) {
            return context.getString(R.string.str_rate2);
        }
        if (weekInvest >= 216000) {
            return context.getString(R.string.str_rate1);
        }
        return rank;
    }
}
