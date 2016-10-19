package me.example.sauce.anychattest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AnyChatBaseEvent {

    @BindView(R.id.local_surface)
    SurfaceView mSurfaceLocal;
    @BindView(R.id.remote_surface)
    SurfaceView mSurfaceRemote;
    private AnyChatCoreSDK anyChatCoreSDK;
    private final String IP = "114.112.81.62";
    private int remoteIndex;

    public static void start(Context context, String userId, int room) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.putExtra("userId", userId);
        starter.putExtra("room", room);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        anyChatCoreSDK = AnyChatCoreSDK.getInstance(this);
        anyChatCoreSDK.SetBaseEvent(this);
        anyChatCoreSDK.InitSDK(android.os.Build.VERSION.SDK_INT, 0);

        ApplyVideoConfig();
        anyChatCoreSDK.SetBaseEvent(this);
        anyChatCoreSDK.mSensorHelper.InitSensor(this);
        AnyChatCoreSDK.mCameraHelper.SetContext(this);
        // 默认打开前置摄像头
        mSurfaceLocal.getHolder().addCallback(AnyChatCoreSDK.mCameraHelper);
        mSurfaceLocal.setZOrderOnTop(true);


        AnyChatCoreSDK.mCameraHelper.SelectVideoCapture(AnyChatCoreSDK.mCameraHelper.CAMERA_FACING_FRONT);
        remoteIndex = anyChatCoreSDK.mVideoHelper.bindVideo(mSurfaceRemote.getHolder());

        anyChatCoreSDK.Connect(IP, 8906);

    }


    @Override
    public void OnAnyChatConnectMessage(boolean bSuccess) {
        Log.e("ChatConnectMessage: ", String.valueOf(bSuccess));
        if (bSuccess)
            anyChatCoreSDK.Login(getIntent().getStringExtra("userId"), "");
        else
            Toast.makeText(this, "connect error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
        Log.e("ChatLoginMessage: ", "userId" + dwUserId + "----" + "dwErrorCode" + dwErrorCode);
        if (dwErrorCode == 0) {
            if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {


                anyChatCoreSDK.UserCameraControl(dwUserId, 1);
                anyChatCoreSDK.UserSpeakControl(dwUserId, 1);

                mSurfaceLocal.setZOrderOnTop(true);


                anyChatCoreSDK.EnterRoom(getIntent().getIntExtra("room", 9), "");
            }
        } else
            Toast.makeText(this, "login error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
        Log.e("EnterRoomMessage: ", "dwRoomId" + dwErrorCode + "----" + "dwErrorCode" + dwErrorCode);
        anyChatCoreSDK.UserCameraControl(-1, 1);// -1表示对本地视频进行控制，打开本地视频
        anyChatCoreSDK.UserSpeakControl(-1, 1);// -1表示对本地音频进行控制，打开本地音频
    }

    @Override
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
        Log.e("AtRoomMessage: ", "dwUserId" + dwUserNum + "----" + "bEnter" + dwRoomId);
        if (dwUserNum > 1) {
            int dwUserId = anyChatCoreSDK.GetOnlineUser()[0];
            anyChatCoreSDK.mVideoHelper.SetVideoUser(remoteIndex, dwUserId);
            anyChatCoreSDK.UserCameraControl(dwUserId, 1);
            anyChatCoreSDK.UserSpeakControl(dwUserId, 1);

        }
    }

    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
        Log.e("AtRoomMessage: ", "dwUserId" + dwUserId + "----" + "bEnter" + bEnter);

        if (bEnter) {
            anyChatCoreSDK.mVideoHelper.SetVideoUser(remoteIndex, dwUserId);
            anyChatCoreSDK.UserCameraControl(dwUserId, 1);
            anyChatCoreSDK.UserSpeakControl(dwUserId, 1);
        } else {
            //关闭远程视频, mRemoteUserid 为通话目标的 userid
            anyChatCoreSDK.UserCameraControl(dwUserId, 0);
//             关闭远程音频,
            anyChatCoreSDK.UserSpeakControl(dwUserId, 0);
            finish();
        }
    }

    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {

    }


    private void ApplyVideoConfig() {

        // 设置本地视频编码的码率（如果码率为0，则表示使用质量优先模式）
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL, 1000000);
//			if (configEntity.mVideoBitrate == 0) {
        // 设置本地视频编码的质量
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL,
                4);
//			}
        // 设置本地视频编码的帧率
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL,
                25);
        // 设置本地视频编码的关键帧间隔
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL,
                25 * 4);
        // 设置本地视频采集分辨率
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL,
                720);
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL,
                960);
        // 设置视频编码预设参数（值越大，编码质量越高，占用CPU资源也会越高）
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL,
                5);
        // 让视频参数生效
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM,
                1);
        // P2P设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_NETWORK_P2PPOLITIC,
                1);
        // 本地视频Overlay模式设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_OVERLAY,
                1);
        // 回音消除设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL,
                1);
        // 平台硬件编码设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_CORESDK_USEHWCODEC,
                0);
        // 视频旋转模式设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL,
                1);
        // 本地视频采集偏色修正设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA,
                0);
        // 视频GPU渲染设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_VIDEOSHOW_GPUDIRECTRENDER,
                1);
        // 本地视频自动旋转设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,
                1);
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_VIDEOSHOW_AUTOROTATION,
                1);
    }

    @Override
    protected void onDestroy() {
        anyChatCoreSDK.Logout();
        anyChatCoreSDK.Release();
        super.onDestroy();

    }
}
