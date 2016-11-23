package com.example.administrator.weibo.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.administrator.weibo.AccessTokenKeeper;
import com.example.administrator.weibo.R;
import com.example.administrator.weibo.adapter.WeiBoPageAdapter;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/22.
 */
public class LoginActivity extends Activity {
    ListView weiBoListview;
    private AuthInfo mAuthInfo;
    SsoHandler mSsoHandler;
    Button button;

    ArrayList<WeiBoPage> list = new ArrayList<WeiBoPage>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weibo_layout);

        weiBoListview = (ListView) findViewById(R.id.weibo_listview);
        button = (Button) findViewById(R.id.button_weibo);

        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
//        mSsoHandler = new SsoHandler(LoginActivity.this, mAuthInfo);
//        mSsoHandler. authorizeClientSso(new AuthListener());

        button.setOnClickListener(onClickListener);

        WeiBoPageAdapter weiBoPageAdapter = new WeiBoPageAdapter(LoginActivity.this, list);
        weiBoListview.setAdapter(weiBoPageAdapter);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_weibo:
                    new Thread() {
                        @Override
                        public void run() {
                            getDData();
                        }
                    }.start();
                    break;
            }
        }
    };

    public void getDData() {
//        hh = 2.00xjuVOGyY87OCb2def0402basm2dE
        HttpURLConnection httpURLConnection = null;
        String httpURL = "https://api.weibo.com/2/statuses/public_timeline.json?access_token=2.00CwhycGyY87OC47105f10c6INyxiD";
        try {
            URL url = new URL(httpURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setRequestProperty("App key", "976730530");
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                StringBuilder stringBuilder = new StringBuilder();
                InputStreamReader inputStreamReader = new InputStreamReader(
                        httpURLConnection.getInputStream(), "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    stringBuilder.append(str);
                }
                Log.i("data===>", stringBuilder.toString());
                String data = stringBuilder.toString();
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("statuses");
                WeiBoPage weiBoPage = new WeiBoPage();
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                     weiBoPage.setCreated_at(object.getString("created_at"));//	微博创建时间
                    weiBoPage.setId(object.getLong("id"));//微博ID
                   //	微博MID
                    weiBoPage.setMid(object.getString("mid"));
                   //字符串型的微博ID
                    weiBoPage.setIdstr( object.getString("idstr"));
                   //微博信息内容
                    weiBoPage.setText( object.getString("text"));
                   //微博来源
                    weiBoPage.setSource(object.getString("source"));
                   //是否收藏
                    weiBoPage.setFavorited(object.getBoolean("favorited"));
                    //是否拦截
                    weiBoPage.setTruncated(object.getBoolean("truncated"));
                    //回复人ID
                    weiBoPage.setIn_reply_to_status_id(object.getString("in_reply_to_status_id"));
                    //（暂未支持）回复人UID
                    weiBoPage.setIn_reply_to_user_id(object.getString("in_reply_to_user_id"));                    //（暂未支持）回复人UID
                    //（暂未支持）回复人昵称
                    weiBoPage.setIn_reply_to_screen_name(object.getString("in_reply_to_screen_name"));                    //（暂未支持）回复人UID
                    //发表的图片集合
//                    JSONArray pic_urls = object.getJSONArray("pic_urls");
//                    if(pic_urls.length() <= 0 ){
//                        for (int j = 0; j < pic_urls.length(); j++){
//
//                        }
//                    }else {
//                        pic_urls = null;
//                    }
                    //地理信息字段 详细
                    weiBoPage.setGeo(object.getString("geo"));
                    //	微博作者的用户信息字段 详细
                    JSONObject user = object.getJSONObject("user");
                    //用户Id
                    weiBoPage.setUseriID(user.getLong("id"));
                    //用户名
                    weiBoPage.setUserName(user.getString("name"));
                    //用户地址
                    weiBoPage.setUserLocation(user.getString("location"));

                    list.add(weiBoPage);

                    inputStreamReader.close();
                }
            } else {
                Log.i("请求失败", "状态码==>" + httpURLConnection.getResponseCode());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(LoginActivity.this, mAccessToken);
                 Log.i("mAccessToken", "mAccessToken===>"+ mAccessToken.toString());
            } else {
                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                String code = values.getString("code", "签名不正确");
                Log.i("code", "code==>"+ code);

            }
        }

        @Override
        public void onWeiboException(WeiboException e) {

        }

        @Override
        public void onCancel() {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
