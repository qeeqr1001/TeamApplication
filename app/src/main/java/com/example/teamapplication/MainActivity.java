package com.example.teamapplication;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.security.MessageDigest;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG="MainActivity";
    private SignInButton btn_google; //구글 로그인 버튼
    private FirebaseAuth auth; //파이어 베이스 인증 객체
    private GoogleApiClient googleApiClient; //구글 API 클라이언트 객체
    private static final int REQ_SIGN_GOOGLE=100; //구글 로그인 결과 코드

    private Button btn_move;
    private ImageView test;
    private View loginButton, logoutButton;
    private TextView nickName;
    private ImageView profileImage;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //구글 로그인 인증을 요청했을때 결과 값을 되돌려 받는 곳
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQ_SIGN_GOOGLE){
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()==true){ //인증결과가 성공적이면
                GoogleSignInAccount account = result.getSignInAccount(); //account라는 데이터는 구글 로그인 정보를 담고 있습니다.(닉네임,프로필사진,이메일 등)
                resultLogin(account); //로그인 결과 값 출력 수행하라는 메소드

            }

        }
    }

    private void resultLogin(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //로그인이 성공했으면
                            Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), GoogleLogin.class);

                            intent.putExtra("nickName", account.getDisplayName());
                            intent.putExtra("photoUrl",String.valueOf(account.getPhotoUrl())); //String.valueOf() 특정 자료형을 String 형태로 변환.

                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {  //앱이 실행될때 처음 수행되는 곳
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //옵션 세팅
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        auth = FirebaseAuth.getInstance(); //파이어베이스 인증 객체 초기화

        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(new View.OnClickListener() { //구글 로그인 버튼을 클릭했을 때 이곳을 수행.

            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, REQ_SIGN_GOOGLE);

            }

        });








       getAppKeyHash();
        KakaoSdk.init(this, "{5041245e68adc7573c3ff192c7169148}");


        loginButton=findViewById(R.id.login);
        logoutButton=findViewById(R.id.logout);
        nickName=findViewById(R.id.nickname);
        profileImage=findViewById(R.id.profile);


        // 카카오가 설치되어 있는지 확인 하는 메서드또한 카카오에서 제공 콜백 객체를 이용함
        Function2<OAuthToken, Throwable, Unit> callback = new  Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                // 이때 토큰이 전달이 되면 로그인이 성공한 것이고 토큰이 전달되지 않았다면 로그인 실패
                if(oAuthToken != null) {

                }
                if (throwable != null) {

                }
                updateKakaoLoginUi();
                return null;
            }
        };
        // 로그인 버튼
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, new Function2<OAuthToken, Throwable, Unit>() {
                        @Override
                        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                            if (oAuthToken!=null){
                                //TBD
                            }
                            if (throwable!=null){
                                //TBD
                            }
                            updateKakaoLoginUi();
                            return null;
                        }
                    });
                }else {
                    UserApiClient.getInstance().loginWithKakaoAccount(MainActivity.this, callback);
                }
            }
        });
        // 로그 아웃 버튼
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        updateKakaoLoginUi();
                        return null;
                    }
                });
            }
        });


        updateKakaoLoginUi();


        btn_move=findViewById(R.id.btn_move);
        btn_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                startActivity(intent); //액티비티 이동
            }
        });

        test=(ImageView) findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "반갑습니다",Toast.LENGTH_SHORT).show();

            }
        });


    }
    private void updateKakaoLoginUi(){
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user!=null){
                    // 유저의 아이디
                    Log.d(TAG,"invoke: id" + user.getId());
                    // 유저의 어카운트정보에 이메일
                    Log.d(TAG,"invoke: nickname" + user.getKakaoAccount().getEmail());
                    // 유저의 어카운트 정보의 프로파일에 닉네임
                    Log.d(TAG,"invoke: email" + user.getKakaoAccount().getProfile().getNickname());
                    // 유저의 어카운트 파일의 성별
                    Log.d(TAG,"invoke: gerder" + user.getKakaoAccount().getGender());
                    // 유저의 어카운트 정보에 나이
                    Log.d(TAG,"invoke: age" + user.getKakaoAccount().getAgeRange());

                    nickName.setText(user.getKakaoAccount().getProfile().getNickname());

                    Glide.with(profileImage).load(user.getKakaoAccount().
                            getProfile().getProfileImageUrl()).circleCrop().into(profileImage);

                    loginButton.setVisibility(View.GONE);
                    logoutButton.setVisibility(View.VISIBLE);

                }else{
                    nickName.setText(null);
                    profileImage.setImageBitmap(null);
                    loginButton.setVisibility(View.VISIBLE);
                    logoutButton.setVisibility(View.GONE);

                }
                return null;
            }
        });
    }
    //카카오 로그인 시 필요한 해시키를 얻는 메소드.
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key", something);
            }
        } catch (Exception e) {

            Log.e("name not found", e.toString());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

