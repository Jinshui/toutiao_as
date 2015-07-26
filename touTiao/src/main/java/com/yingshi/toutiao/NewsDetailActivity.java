package com.yingshi.toutiao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.exception.WeiboShareException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.actions.GetCommentsAction;
import com.yingshi.toutiao.actions.ParallelTask;
import com.yingshi.toutiao.model.Comment;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.social.AccountInfo;
import com.yingshi.toutiao.util.DialogHelper;
import com.yingshi.toutiao.util.Utils;
import com.yingshi.toutiao.view.CommentListRow;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.HeaderView;

public class NewsDetailActivity extends Activity implements IWeiboHandler.Response 
{
	private final static String tag = "TT-NewsDetailActivity";
	private final static String NEWS_URL = "http://115.28.85.247/sharepage/newsDetails.html?id=";
	private View mShareNewsWidget;
	private LinearLayout mCommentsList;
	private ImageButton mShowCommentsBtn;
	private EditText mCommentTextView;
	private CustomizeImageView mImageView;
	private News mNews;
	private GetCommentsAction mGetCommentsAction;
	private LinearLayout mMoreImgPanel;

    
    private IWXAPI mWxapi = null;
    private Tencent mTencent = null;
    private IWeiboShareAPI mWeiboShareAPI = null;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_detail);

		HeaderView header = (HeaderView)findViewById(R.id.id_news_detail_header);
		header.setLeftImage(R.drawable.fanhui, new OnClickListener(){
			public void onClick(View v) {
				finish();
			}
		});
		
		mCommentTextView = (EditText)findViewById(R.id.id_news_detail_comment_text);
		mCommentTextView.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(((TouTiaoApp)getApplication()).getUserInfo() == null){
					showLonginConfirmDialog();
				}
			}
		});
		mCommentTextView.setOnEditorActionListener(new OnEditorActionListener(){
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(event != null && event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
					showAddCommentConfirmDialog();
					return true;
				}
				return false;
			}
		});
		
		//For samsung only
		mCommentTextView.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				mCommentTextView.removeTextChangedListener(this);//防止递归错误
				if (s.length() > 1 && s.charAt(s.length() - 1) == '\n') { 
					s.delete(s.length() - 1, s.length());
					mCommentTextView.clearFocus(); 
					showAddCommentConfirmDialog();
				}
				mCommentTextView.addTextChangedListener(this);  
            }  
        }); 
		
		mShareNewsWidget = findViewById(R.id.id_news_share_widget);
		mCommentsList = (LinearLayout)findViewById(R.id.id_news_detail_comments);
		mShowCommentsBtn = (ImageButton)findViewById(R.id.id_news_get_comment);
		
		mNews = getIntent().getParcelableExtra(Constants.INTENT_EXTRA_NEWS);
		updateUI(mNews);
		
        //Register weixin
        mWxapi = WXAPIFactory.createWXAPI(this, Constants.APP_WEIXIN_ID, true);
        mWxapi.registerApp(Constants.APP_WEIXIN_ID);
        mTencent = Tencent.createInstance(Constants.APP_TENCENT_ID, this);
        initWeiboAPI(savedInstanceState);
	}
	
	private void initWeiboAPI(Bundle savedInstanceState){
        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_WEIBO_KEY);
        
        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
//        mWeiboShareAPI.registerApp();
        
		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
	}
	
	private void showLonginConfirmDialog(){
		DialogHelper.createDialog(NewsDetailActivity.this, R.string.add_comment_login_dlg_msg, R.string.add_comment_login_dlg_title, 0, android.R.string.ok, new android.content.DialogInterface.OnClickListener(){
			@Override
			public void onClick(android.content.DialogInterface dialog, int which) {
				Intent intent = new Intent(NewsDetailActivity.this, LoginActivity.class);
				intent.putExtra(LoginActivity.INTENT_EXTRA_GO_NEWS_LIST, false);
				startActivity(intent);
			}
		}, android.R.string.cancel, null).show();
	}
    
	private void showAddCommentConfirmDialog(){
		DialogHelper.createDialog(this, R.string.confirm_add_comment_dlg_msg, R.string.confirm_add_comment_dlg_title, 0, android.R.string.ok, new android.content.DialogInterface.OnClickListener(){
			@Override
			public void onClick(android.content.DialogInterface dialog, int which) {
				addComment();
			}
		}, android.R.string.cancel, null).show();
	}
	
    private void addComment(){
    	AccountInfo userInfo = ((TouTiaoApp)getApplication()).getUserInfo();
    	if(userInfo == null){
    		showLonginConfirmDialog();
    		return;
    	}
    	AsyncHttpClient httpClient = new AsyncHttpClient();
    	RequestParams params = new RequestParams();
    	params.put("newsid", mNews.getId());
    	params.put("username", Utils.encode(userInfo.getUserName(), "UTF-8"));
    	params.put("content", Utils.encode(mCommentTextView.getText().toString(), "UTF-8"));
    	params.put("img", userInfo.getPhotoBase64());
    	httpClient.post(Constants.UPLOAD_ADDRESS, params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
                Log.d(tag, "Received JSON response : " + response.toString());
                Toast.makeText(NewsDetailActivity.this, R.string.add_comment_succ, Toast.LENGTH_SHORT).show();
                mCommentTextView.setText("");
                mCommentTextView.clearFocus();
                loadComments(null);
			}
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				super.onSuccess(statusCode, headers, response);
                Log.d(tag, "Received JSON response : " + response.toString());
                Toast.makeText(NewsDetailActivity.this, R.string.add_comment_succ, Toast.LENGTH_SHORT).show();
                mCommentTextView.setText("");
                mCommentTextView.clearFocus();
                loadComments(null);
			}
			public void onSuccess(int statusCode, Header[] headers, String response) {
				super.onSuccess(statusCode, headers, response);
                Log.d(tag, "Received JSON response : " + response);
                Toast.makeText(NewsDetailActivity.this, R.string.add_comment_succ, Toast.LENGTH_SHORT).show();
                mCommentTextView.setText("");
                mCommentTextView.clearFocus();
                loadComments(null);
			}
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
				onFailure(statusCode, headers, errorResponse == null? "" : errorResponse.toString(), throwable);
			}
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse){
				onFailure(statusCode, headers, errorResponse == null? "" : errorResponse.toString(), throwable);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(NewsDetailActivity.this, R.string.add_comment_fail, Toast.LENGTH_SHORT).show();
                Log.e(tag, "Received Error response : StatusCode: " + statusCode + ", Received response : " + responseString);
			} 
		});    		
    }
    
	private void updateUI(final News news){
		mNews = news;
		TextView titleView = (TextView)findViewById(R.id.id_news_detail_title);
		titleView.setText(mNews.getName());
		
		TextView dateView = (TextView)findViewById(R.id.id_news_detail_time);
		String dateViewText = String.format("%s  %s", Utils.formatDate("yyyy/MM/dd HH:mm:ss", mNews.getTime()), mNews.getAuthor());
		dateView.setText(dateViewText);
		
		mImageView = (CustomizeImageView)findViewById(R.id.id_news_detail_img);
		if(mNews.getPhotoUrls().size() == 0)
			mImageView.setVisibility(View.GONE);
		else
			mImageView.loadImage(mNews.getPhotoUrls().get(0));

		View playButton = findViewById(R.id.id_news_detail_play);
		playButton.setVisibility( ( mNews.isHasVideo() && mNews.getThumbnailUrls().size()>0 ) ?  View.VISIBLE : View.GONE);

		mMoreImgPanel = (LinearLayout)findViewById(R.id.id_news_more_img_panel);
		if(mNews.getPhotoUrls().size() > 1 && !mNews.isHasVideo()){
			mMoreImgPanel.setVisibility(View.VISIBLE);
			for(int i=1; i < mNews.getPhotoUrls().size(); i++){
				String url = mNews.getPhotoUrls().get(i);
				CustomizeImageView view = new CustomizeImageView(this);
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 15, 0, 0);
				params.gravity = Gravity.CENTER_HORIZONTAL;
				view.setPadding(0, 0, 0, 0);
				view.setAdjustViewBounds(true);
				mMoreImgPanel.addView(view, params);
				view.loadImageToSrc(url);
			}
		}else{
			mMoreImgPanel.setVisibility(View.GONE);
		}
		
		TextView textView = (TextView)findViewById(R.id.id_news_detail_text);
		textView.setText(mNews.getContent());
	}

	public void playVideo(View view){
		if(mNews == null)
			return;
        String extension = MimeTypeMap.getFileExtensionFromUrl(mNews.getVideoUrl());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
        mediaIntent.setDataAndType(Uri.parse(mNews.getVideoUrl()), mimeType);
        startActivity(mediaIntent);
	}
	
	public void share(View view){
		if(mShareNewsWidget.getVisibility() == View.GONE){
			mShareNewsWidget.setVisibility(View.VISIBLE);
		}else{
			mShareNewsWidget.setVisibility(View.GONE);
		}
	}
	
	public void addToFavorites(View view){
		new ParallelTask<Void>() {
			protected Void doInBackground(Void... params) {
				((TouTiaoApp)getApplication()).getFavoritesDAO().save(mNews);
				return null;
			}
			public void onPostExecute(Void result){
				Toast.makeText(getApplication(), R.string.add_favorite_succ, Toast.LENGTH_SHORT).show();
			}
		}.execute();
	}
	
	public void loadComments(View view){
		mShowCommentsBtn.setVisibility(View.GONE);
		mGetCommentsAction = new GetCommentsAction(this, mNews.getId(), mNews.getCategory(), 1, 30);
		mGetCommentsAction.execute(new UICallBack<Pagination<Comment>>(){
			public void onSuccess(Pagination<Comment> result) {
                List<Comment> comments = result.getItems();
                if(comments != null && !comments.isEmpty()){
                    fillItems(mCommentsList, comments);
    				Toast.makeText(NewsDetailActivity.this, R.string.no_more_load, Toast.LENGTH_SHORT).show();
                }else{
    				Toast.makeText(NewsDetailActivity.this, R.string.no_comments_found, Toast.LENGTH_SHORT).show();
                }
			}
			public void onFailure(ActionError error) {
				mShowCommentsBtn.setVisibility(View.VISIBLE);
				Toast.makeText(NewsDetailActivity.this, R.string.load_comment_failed, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
    protected void fillItems(LinearLayout contentView, List<Comment> items){
    	contentView.removeAllViews();
        for(final Comment item : items){
            CommentListRow view = new CommentListRow(this, null);
            view.setProduct(item);
            @SuppressWarnings("deprecation")
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            contentView.addView(view, params);
        }
    }
	
	public void shareWeiChat(View view){
		Log.d(tag, "shareWeiChat");
		
		if(!mWxapi.isWXAppInstalled()){
            Toast.makeText(this, R.string.share_weichat_not_installed, Toast.LENGTH_SHORT).show();
			return;
		}
		
		mShareNewsWidget.setVisibility(View.GONE);
	    WXWebpageObject webpage = new WXWebpageObject();  
	    webpage.webpageUrl = getNewsUrl(); 
		
		WXMediaMessage msg = new WXMediaMessage(webpage);
	    msg.title = mNews.getName().length() > 512 ? mNews.getName().substring(0, 512) : mNews.getName();
	    msg.description = mNews.getContent().length() > 1024 ? mNews.getContent().substring(0, 1024) : mNews.getContent();
    	msg.setThumbImage(getNewsImage());
	    
	    
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); 
		req.message = msg;
		req.scene = view.getId() == R.id.id_share_weichat_moments_btn ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		mWxapi.sendReq(req);
		Log.d(tag, "shareWeiChat done");
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	private Bitmap getNewsImage(){
	    Bitmap thumbnail = null;
	    if(!mNews.getThumbnailUrls().isEmpty()){
	    	String imagePath = CustomizeImageView.getCachedImagePath(mNews.getThumbnailUrls().get(0));
	    	if(imagePath != null){
	    		thumbnail = BitmapFactory.decodeFile(imagePath);
	    	}
	    }
	    if(thumbnail == null){
	    	thumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
	    }
	    return thumbnail;
	}
	
	private String getNewsUrl(){
		return NEWS_URL + mNews.getId();
	}
	
	public void shareWeibo(View view){
		mShareNewsWidget.setVisibility(View.GONE);
        try {
	        // 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
	        if (mWeiboShareAPI.checkEnvironment(true)) {    
	        	mWeiboShareAPI.registerApp();
	            if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
	                int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
	                if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
	                    sendMultiMessage();
	                } else {
	                    sendSingleMessage();
	                }
	            } else {
	                Toast.makeText(this, R.string.share_sina_weibo_not_support, Toast.LENGTH_SHORT).show();
	            }
	        }
	    } catch (WeiboShareException e) {
	    	Log.e(tag, "Failed to share news to weibo", e);
	        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
	    }
	}
	
	   /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     * 
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     * @param hasVoice   分享的内容是否有声音
     */
    private void sendMultiMessage() {
        
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = new TextObject();
        weiboMessage.textObject.text = mNews.getContent().length() > 140 ? mNews.getContent().substring(0, 140) : mNews.getContent();
        weiboMessage.imageObject = new ImageObject();
        weiboMessage.imageObject.setImageObject(getNewsImage());
        // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
        weiboMessage.mediaObject = new WebpageObject();
        weiboMessage.mediaObject.identify = Utility.generateGUID();
        weiboMessage.mediaObject.title = mNews.getName().length() > 512 ? mNews.getName().substring(0, 512) : mNews.getName();
	    weiboMessage.mediaObject.description = mNews.getContent().length() > 1024 ? mNews.getContent().substring(0, 1024) : mNews.getContent();
        weiboMessage.mediaObject.setThumbImage(getNewsImage());
        weiboMessage.mediaObject.actionUrl = getNewsUrl();
        
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(request);
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     * 
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     */
    private void sendSingleMessage() {
        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        weiboMessage.mediaObject = new WebpageObject();
        weiboMessage.mediaObject.identify = Utility.generateGUID();
        weiboMessage.mediaObject.title = mNews.getName();
        weiboMessage.mediaObject.description = mNews.getContent();
        
        // 设置 Bitmap 类型的图片到视频对象里
        weiboMessage.mediaObject.setThumbImage(getNewsImage());
        weiboMessage.mediaObject.actionUrl = getNewsUrl();
        
        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(request);
    }
	
	public void shareQQ(View view){
		mShareNewsWidget.setVisibility(View.GONE);
		//分享类型
		Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, mNews.getName());
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mNews.getContent());
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, getNewsUrl());
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, (ArrayList<String>)mNews.getPhotoUrls());
        doShareToQzone(params);
	}
	
    private void doShareToQzone(final Bundle params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
            	mTencent.shareToQzone(NewsDetailActivity.this, params, new IUiListener() {
                    public void onCancel() {
                    }
                    public void onError(UiError e) {
                    }
					public void onComplete(Object response) {
					}

                });
            }
        }).start();
    }
	
	public void cancelShare(View view){
		mShareNewsWidget.setVisibility(View.GONE);
	}

	@Override
	public void onResponse(BaseResponse arg0) {
		
	}
}
