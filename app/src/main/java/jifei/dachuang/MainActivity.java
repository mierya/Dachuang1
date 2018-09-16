package jifei.dachuang;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import de.hdodenhof.circleimageview.CircleImageView;
import jifei.dachuang.accountOperations.ModifyPassword;
import jifei.dachuang.accountOperations.SignIn;
import jifei.dachuang.camera.CameraActivity;
import jifei.dachuang.fragments.Album;
import jifei.dachuang.fragments.Discover;
import jifei.dachuang.fragments.Share;
import jifei.dachuang.helper.StrokeTextView;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileNotFoundException;
/*
2018.3.*——?
人生如梦，一樽还酹江月
*/
public class MainActivity extends AppCompatActivity implements
        BottomNavigationBar.OnTabSelectedListener,
       TakePhoto.TakeResultListener,InvokeListener
{
    //TakePhoto库，从相册或相机获取图片，然后裁切、压缩
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;//这鬼东西害我debug半天
    private int photoRequestCode;
    private File head;
    private File headerBackground;
    //UI
    private DrawerLayout drawer;
    private TextView title;
    private BottomNavigationBar navBar;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        drawer=findViewById(R.id.drawer);
        title=findViewById(R.id.title);
        navBar=findViewById(R.id.navBar);
        toolbar=findViewById(R.id.toolbar);

        getTakePhoto().onCreate(savedInstanceState);
        takePhoto=getTakePhoto();
        takePhoto.onEnableCompress(new CompressConfig.Builder().enableQualityCompress(true).create(),true);


        //Toolbar、ActionBar
        toolbar.setTitle("");//除去label指定的文字，自定义居中
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        }

        //BottomNavigationBar底部导航
        navBar.addItem(new BottomNavigationItem(R.mipmap.discover,"发现")).
                addItem(new BottomNavigationItem(R.mipmap.album,"相册")).
                addItem(new BottomNavigationItem(R.mipmap.share,"分享")).initialise();
        navBar.setTabSelectedListener(this);
        navBar.selectTab(1);//默认选中中间（相册）

        //侧滑菜单，不用NavigationView了，太难用了，自定义吧
        try
        {
            initView(R.id.profilePic);
            initView(R.id.name);
            initView(R.id.bio);
            initView(R.id.headerBackground);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        (findViewById(R.id.modifyP)).setOnClickListener(sideItemSelected(R.id.modifyP));
        (findViewById(R.id.shift)).setOnClickListener(sideItemSelected(R.id.shift));
        (findViewById(R.id.exit)).setOnClickListener(sideItemSelected(R.id.exit));
    }

    //初始化用户名、自我介绍和头像，设置监听器
    /*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/
    public void initView(int id) throws FileNotFoundException
    {
        head=new File(getExternalCacheDir(),"head.jpg");
        headerBackground=new File(getExternalCacheDir(),"background.jpg");
        switch (id)
        {
            case R.id.name:
            {   //都是描边文字，否则用户设置了颜色接近的背景容易看不清
                StrokeTextView name=findViewById(id);
                name.setStroke(2,"#000000");
                name.setClickable(true);
                name.setText(Start.sp.getString("userName","用户名为空"));//此处设置用户名
                break;
            }
            case R.id.bio:
            {
                final StrokeTextView bio=findViewById(id);
                bio.setStroke(2,"#000000");
                bio.setText(Start.sp.getString("bio","介绍一下自己吧\n(点击可设置)"));//处设置自我介绍
                bio.setOnClickListener(v -> {
                    final EditText editText=new EditText(MainActivity.this);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});//控制输入长度
                    AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("设置自我介绍");
                    dialog.setView(editText);
                    dialog.setPositiveButton("确定", (dialog1, which) -> {
                        String s=editText.getText().toString();
                        if(s.length()==0)
                        {
                            Toast.makeText(MainActivity.this,"输入为空！",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Start.editor.putString("bio",editText.getText().toString());
                            Start.editor.commit();
                            bio.setText(Start.sp.getString("bio","介绍一下自己吧"));
                        }
                    });
                    dialog.show();
                });
                break;
            }
            case R.id.profilePic:
            {
                //用ConstraintLayout，头像居中出了问题，无法理解
                //改成了用LinearLayout，一开始头像的宽总是大于高，点击事件会很迷，恼火(||❛︵❛.)
                //后来才想到动态设置长宽，总算解决(-_-;)彡
                //可惜bio的长又无法wrap_content了
                final CircleImageView profilePic = findViewById(id);
                profilePic.post(() -> {
                    int height=profilePic.getHeight();
                    profilePic.setLayoutParams(new LinearLayout.LayoutParams(height,height));
                });
                //实际上这些应该先从服务器端获取
                if (head.exists())
                {
                    ((CircleImageView) findViewById(R.id.profilePic)).setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(
                            Uri.fromFile(head)
                    )));
                }
                else
                {
                    profilePic.setImageResource(R.mipmap.head);//此处设置头像
                }
                profilePic.setOnClickListener(v -> {
                    photoRequestCode=1;
                    changePicture("更换头像",head,200,200);

                });
                break;
            }
            case R.id.headerBackground:
            {
                final LinearLayout linearLayout=findViewById(id);
                if(headerBackground.exists())
                {
                    linearLayout.setBackground(Drawable.createFromPath(headerBackground.getPath()));
                }
                else
                {
                    linearLayout.setBackgroundResource(R.mipmap.header_bac);
                }
                linearLayout.setOnClickListener(v -> {
                    photoRequestCode=0;
                    double d=(double) linearLayout.getWidth()/linearLayout.getHeight();
                    changePicture("更换背景",headerBackground,(int) (800*d),800);
                });
                break;
            }
        }
    }
    /*↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/
    //设置默认自我介绍和头像，设置监听器



    //BottomNavigationBar底部导航监听器
    /*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/
    @Override
    public void onTabSelected(int position)//未选中 -> 选中
    {
        //根据选中的Tab更改Toolbar的标题与图标，替换Fragment
        //没找到直接返回Tab并获取title的方法，只能一次次自己写
        switch (position)
        {
            //现在碎片用的是replace，以后改成add与hide
            case  0:
            {
                title.setText("发现");
                toolbar.getMenu().findItem(R.id.camera).setVisible(true);
                replaceFragment(new Discover());
                break;
            }

            case  1:
            {
                title.setText("相册");
                //莫名其妙会NullPointerException！
                MenuItem menuItem=toolbar.getMenu().findItem(R.id.share);
                if(menuItem!=null)
                {
                    menuItem.setVisible(true);
                }
                replaceFragment(new Album());
                Toast.makeText(MainActivity.this,"请点一下右上角！",Toast.LENGTH_LONG).show();
                break;
            }

            case  2:
            {
                title.setText("分享");
                replaceFragment(new Share());
                break;
            }

        }
    }

    @Override
    public void onTabUnselected(int position)//选中 -> 未选中
    {
        switch (position)
        {
            case  0:
                toolbar.getMenu().findItem(R.id.camera).setVisible(false);
                break;
            case  1:
                toolbar.getMenu().findItem(R.id.share).setVisible(false);
                break;
            case  2:
                break;
        }
    }

    @Override
    public void onTabReselected(int position)//选中 -> 选中
    {

    }
    /*↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/
    //BottomNavigationBar底部导航监听器

    //侧滑菜单Menu部分
    /*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/
    public View.OnClickListener sideItemSelected(int id)
    {
        //drawer.closeDrawer(GravityCompat.START);
        switch (id)
        {
            case R.id.modifyP://修改密码
            {
                return v -> startActivity(new Intent(MainActivity.this,ModifyPassword.class));
            }

            case R.id.shift://切换账号
            {
                return v -> {
                    Start.editor.remove("userName");
                    Start.editor.remove("password");
                    Start.editor.remove("bio");
                    Start.editor.commit();
                    head.delete();
                    headerBackground.delete();
                    //先清空个人数据
                    startActivity(new Intent(MainActivity.this,SignIn.class));
                    finish();
                };
            }
            case R.id.exit://退出软件
            {
                return v -> finish();
            }
        }
        return null;//明明不会被执行，非要我return(´ｰ`)丿
    }
    /*↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/
    //侧滑菜单Menu部分

    //Menu
    /*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.tbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.getItem(0).setIcon(android.R.drawable.ic_menu_camera);
        toolbar.getMenu().findItem(R.id.camera).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                drawer.openDrawer(GravityCompat.START);
                break;
            }

            case R.id.camera:
            {
                AlertDialog.Builder dialog=new AlertDialog.Builder(this);
                dialog.setMessage("拍摄自己的一张照片，系统将通过人脸识别返回景区拍摄的照片。");
                dialog.setPositiveButton("确定", (dialog1, which) -> {
                    RxPermissions rxPermissions=new RxPermissions(this);
                    rxPermissions.request(Manifest.permission.CAMERA).subscribe(granted ->
                    {
                        if (granted)
                        {
                            startActivity(new Intent(MainActivity.this,CameraActivity.class));
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"权限获取失败，请授予相机权限！",Toast.LENGTH_SHORT).show();
                        }
                    });


                });
                dialog.show();
                break;
            }

            case R.id.share:
            {
                AlertDialog.Builder dialog=new AlertDialog.Builder(this);
                dialog.setMessage("图片预览，双击/手势可放大缩小，滑动可切换");
                dialog.show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    /*↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/
    //Menu

    //碎片操作
    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment,fragment);
        fragmentTransaction.commit();
    }

    //更换头像与背景
    void changePicture(String title, final File file, final int x, final int y)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(title);
        dialog.setItems(new String[]{"相机拍摄", "图库选取"}, (dialog1, which) -> {
            switch (which)
            {
                case 0:
                {
                    takePhoto.onPickFromCaptureWithCrop(Uri.fromFile(file)
                            ,new CropOptions.Builder().setAspectX(x).setAspectY(y).setOutputX(x).setOutputY(y).setWithOwnCrop(false).create());
                    break;
                }
                case 1:
                {
                    takePhoto.onPickFromGalleryWithCrop(Uri.fromFile(file)
                            ,new CropOptions.Builder().setAspectX(x).setAspectY(y).setOutputX(x).setOutputY(y).setWithOwnCrop(false).create());
                    break;
                }
            }
        });
        dialog.show();
    }

    //TakePhoto裁剪图片
    /*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/
    public TakePhoto getTakePhoto()
    {
        if (takePhoto==null)
        {
            takePhoto= (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
        }
        return takePhoto;
    }

    @Override
    public void takeSuccess(TResult result)
    {
        //Glide加载CircleImageView出问题了，绕了个大弯来实现(๑òᆺó๑)
        switch (photoRequestCode)
        {
            case 0:
            {
                (findViewById(R.id.headerBackground)).setBackground(Drawable.createFromPath(result.getImage().getOriginalPath()));
                break;
            }
            case 1:
            {
                try
                {
                    ((CircleImageView)findViewById(R.id.profilePic)).setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(
                            Uri.fromFile(new File(result.getImage().getOriginalPath()))
                    )));
                } catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void takeFail(TResult result, String msg)
    {

    }

    @Override
    public void takeCancel()
    {

    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam)
    {
        PermissionManager.TPermissionType type=PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
        if(PermissionManager.TPermissionType.WAIT.equals(type))
        {
            this.invokeParam=invokeParam;
        }
        return type;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type=PermissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        PermissionManager.handlePermissionsResult(this,type,invokeParam,this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    /*↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/
    //TakePhoto裁剪图片
}
