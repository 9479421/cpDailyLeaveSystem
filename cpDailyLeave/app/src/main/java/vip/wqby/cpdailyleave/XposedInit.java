package vip.wqby.cpdailyleave;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.github.kyuubiran.ezxhelper.init.EzXHelperInit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import de.robv.android.xposed.BuildConfig;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import vip.wqby.cpdailyleave.http.Http;
import vip.wqby.cpdailyleave.http.HttpResponse;

public class XposedInit implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    public static String ApkPath;
    public static String userId;
    public static String name;

    public static String baseUrl = "http://150.158.97.234:8888"; //150.158.97.234

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals("com.wisedu.cpdaily")) {//com.wisedu.cpdaily   || !loadPackageParam.isFirstApplication
            return;
        }

        EzXHelperInit.INSTANCE.initHandleLoadPackage(loadPackageParam);

        XposedHelpers.findAndHookMethod("com.stub.StubApp", loadPackageParam.classLoader, "a", android.content.Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Context context = (Context) param.args[0];
                //获取360的classloader，之后hook加固后的就使用这个classloader
                ClassLoader classLoader = context.getClassLoader();
                //下面就是强classloader修改成360的classloader就可以成功的hook了
//                XposedHelpers.findAndHookMethod("xxx.xxx.xxx.xxx", classLoader, "xxx", String.class, String.class, new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//
//                    }
//                });

                //com.wisorg.wisedu.home.viewtype.view.head.CpAppView   load

                XposedHelpers.findAndHookMethod("android.content.Intent", classLoader, "putExtra", String.class,String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.d("intent","intent===="+param.args[0].toString()+"==||=="+param.args[1].toString());
                        if (param.args[0].toString().equals("url") && param.args[1].toString().equals("https://s74hnmocct.jiandaoyun.com/f/5e3f79eb92c2f000065d493f")){
                            param.args[1] = baseUrl+"/checkin";
                        }
                    }
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);


                    }
                });



                XposedHelpers.findAndHookMethod("com.wisorg.wisedu.home.ui.HomeActivity", classLoader, "onCreate",Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Activity act = (Activity) param.thisObject;
                        Context ctx = (Context) act;
                        EzXHelperInit.INSTANCE.initAppContext(ctx, true, true);
//                        EzXHelperInit.INSTANCE.initActivityProxyManager(BuildConfig.APPLICATION_ID,
//                                "com.wisorg.wisedu.amp.teahceramp.mine.settings.TeacherSettingsActivity",
//                                XposedInit.class.getClassLoader(),
//                                param.thisObject.getClass().getClassLoader());
//                        EzXHelperInit.INSTANCE.initSubActivity();

                        EzXHelperInit.INSTANCE.addModuleAssetPath(act);

//
//                        int id = act.getResources().getIdentifier("llInfos", "id", act.getPackageName());
//                        XposedBridge.log("id==="+id);
//                        XposedBridge.log("test==="+String.valueOf(act.findViewById(id)));

                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        try {
                            Double currentVersion = 3.0;

                            Http http_verify = new Http();
                            http_verify.open(baseUrl+"/getVersion");
                            http_verify.setHeader("Content-Type","text/html; charset=utf-8");
                            http_verify.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.67 Safari/537.36");
                            HttpResponse response = http_verify.get();
                            Log.d("hahahha",response.getBody());
                            Double version = Double.valueOf(response.getBody());

                            if (currentVersion >= version) {
                                Toast.makeText(act, "获取版本成功--王权霸业", Toast.LENGTH_SHORT).show();

                                SharedPreferences sharedPreferences = act.getSharedPreferences("3.0", MODE_PRIVATE);
                                boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                if (isFirstRun)
                                {
                                    Log.d("debug", "第一次运行");
                                    editor.putBoolean("isFirstRun", false);
                                    editor.commit();
                                    new AlertDialog.Builder(act)
                                            .setTitle("使用教程")
                                            .setMessage("使用教程：\n首次打开APP请先去下方《服务》页面，然后回到《今选》页面，可以看到最近使用列表多出了一个学生请销假模块，点开即可使用\n添加假条要连续点击请假页面右下角加号10次才可以！")
                                            .setIcon(R.mipmap.ic_logo)
                                            .create().show();
                                } else
                                {
                                    Log.d("debug", "不是第一次运行");
                                }

                            } else {
                                System.out.println("强制更新");
                                //强制更新
                                new AlertDialog.Builder(act)
                                        .setTitle("权权警告")
                                        .setMessage("该版本过旧，已失去功能！请卸载后下载最新版--王权霸业")
                                        .setIcon(R.mipmap.ic_logo)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                android.os.Process.killProcess(android.os.Process.myPid());
                                            }
                                        })
                                        .create().show();
                            }
                        } catch (Exception e) {
                            Log.d("ceshi","e.getMessage()");
                            e.printStackTrace();
                            //强制更新
                            new AlertDialog.Builder(act)
                                    .setTitle("权权警告")
                                    .setMessage("网络异常，请联网后再打开--王权霸业")
                                    .setIcon(R.mipmap.ic_logo)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            android.os.Process.killProcess(android.os.Process.myPid());
                                        }
                                    })
                                    .create().show();
                        }

                }


            });

                /*

                XposedHelpers.findAndHookMethod("com.wisorg.wisedu.home.viewtype.view.head.CpAppView", classLoader, "load", boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);




                        int id = act.getResources().getIdentifier("rc_home_app", "id", act.getPackageName());

//                LinearLayout mRoot = (LinearLayout) act.findViewById(id).getParent().getParent().getParent();
                        RecyclerView mRoot = (RecyclerView) act.findViewById(id);
                        LinearLayout myItem = (LinearLayout) LayoutInflater.from(act).inflate(R.layout.my_item, null);
//                myItem.setOnClickListener(v -> {
//                    Intent intent = new Intent(act, TestActivity.class);
//                    act.startActivity(intent);
//                });
                        TextView textView = new TextView(act);
                        textView.setText("3213213");
                        mRoot.addView(textView, 0);
                    }
                });
*/







                XposedHelpers.findAndHookMethod("com.wisorg.wisedu.utils.CommonUtils", classLoader, "G", java.lang.String.class, "com.google.gson.reflect.TypeToken",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);

                                XposedBridge.log("三生三世" + param.args[0].toString());
                                XposedBridge.log("哈哈哈哈" + param.getResult().toString());

                            }
                        });

                //读取姓名
                XposedHelpers.findAndHookMethod("com.blankj.utilcode.util.LogUtils", classLoader, "n", java.lang.Object.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.args[0].toString().indexOf("personId")!=-1){
                            int fromIndex = param.args[0].toString().indexOf("name=")+5;
                            int endIndex = param.args[0].toString().indexOf("personId");
                            name =  param.args[0].toString().substring(fromIndex,endIndex);
                            XposedBridge.log("哈哈哈哈"+name);
                        }
                        XposedBridge.log("哈哈哈哈"+param.args[0].toString());
                        super.afterHookedMethod(param);
                    }
                });


                XposedHelpers.findAndHookMethod("com.wisedu.campushoy.common.utils.c", classLoader, "g", java.lang.String.class, new

                        XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                Log.d("阿巴阿巴", param.args[0].toString());
                                Log.d("咕噜咕噜", param.getResult().toString());
                                if (param.args[0].toString().equals("open_id")) {
                                    userId = param.getResult().toString();
                                }
                                //获取姓名
                                if (param.getResult().toString().indexOf("authId")!=-1){
                                    JSONObject jsonObject = new JSONObject(param.getResult().toString());
                                    if (jsonObject.getString("name")!=null){
                                        name = jsonObject.getString("name");
                                    }
                                    Log.d("咕噜咕噜111",name);
                                }


                                if (param.args[0].toString().indexOf("RecentUseService") != -1) {
                                    JSONArray jsonArray = new JSONArray(param.getResult().toString());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        if (jsonArray.getJSONObject(i).getString("name").equals("学生请销假")) {
                                            jsonArray.remove(i);
                                        }
                                    }
                                    JSONArray newArray = new JSONArray();
                                    String iconUrl = "http://ehall.ccit.js.cn/resources/app/6166618601012518/1.0_R1/mobile_icon_64.png?_=1645606769000";
                                    String openUrl = baseUrl+"/pageList?userId="+userId+"&name="+name;//150.158.97.234
                                    Log.d("测试网址", openUrl + "?userId=" + userId);
                                    JSONObject json_leave = new JSONObject("{\"accessAuth\":\"4\",\"appId\":\"6166618601012518\",\"appSize\":0,\"appSource\":1,\"appState\":\"ONLINE\",\"appType\":2,\"assessCount\":0,\"assessMark\":0.0,\"deamon\":false,\"fromPlatform\":\"private\",\"fromPlatformType\":\"AMP2\",\"iconUrl\":\"" + iconUrl + "\",\"isRecommendAppFlag\":false,\"isTop\":false,\"lastTime\":0,\"messageSize\":0,\"name\":\"学生请销假\",\"openUrl\":\"" + openUrl + "\",\"publishTime\":0,\"sortIndex\":0,\"tipViewId\":0,\"unreadNum\":0,\"useCount\":0,\"version\":\"1.0_R1\"}");
                                    newArray.put(0, json_leave);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        newArray.put(i + 1, jsonArray.getJSONObject(i));
                                    }
                                    param.setResult(newArray.toString());
                                }

                            }
                        });

            }
        });


    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        ApkPath = startupParam.modulePath;

        EzXHelperInit.INSTANCE.initZygote(startupParam);
    }
}
