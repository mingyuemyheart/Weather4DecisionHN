package com.pmsc.weather4decision.phone.hainan.act;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.android.lib.data.CONST;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.pmsc.weather4decision.phone.hainan.R;
import com.pmsc.weather4decision.phone.hainan.util.CommonUtil;
import com.pmsc.weather4decision.phone.hainan.util.OkHttpUtil;
import com.umeng.socialize.UMShareAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * PDF列表界面
 */
public class ShawnPDFActivity extends BaseActivity implements OnClickListener {
	
	private PDFView pdfView;
	private TextView tvPercent;
	private String pdfUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_pdfview);
		initWidget();
		initPDFView();
	}

	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText("详情");
		ImageView ivShare = findViewById(R.id.ivShare);
		ivShare.setOnClickListener(this);
		ivShare.setVisibility(View.VISIBLE);
		tvPercent = findViewById(R.id.tvPercent);
	}
	
	// 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }
 
    // 完整的判断中文汉字和符号
    private String isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (char c : ch) {
            if (isChinese(c)) {
            	try {
					strName = strName.replace(c+"", URLEncoder.encode(c+"", "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
            }
        }
		return strName;
    }
	
	private void initPDFView() {
		pdfView = findViewById(R.id.pdfView);

		pdfUrl = getIntent().getStringExtra(CONST.WEB_URL);
		if (TextUtils.isEmpty(pdfUrl)) {
			return;
		}else {
			pdfUrl = isChinese(pdfUrl);
		}
		OkHttpFile(pdfUrl);
	}

	private void OkHttpFile(final String url) {
		if (TextUtils.isEmpty(url)) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
					}
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						InputStream is = null;
						FileOutputStream fos = null;
						try {
							is = response.body().byteStream();//获取输入流
							float total = response.body().contentLength();//获取文件大小
							if(is != null){
								File files = new File(Environment.getExternalStorageDirectory()+"/ChinaWeather");
								if (!files.exists()) {
									files.mkdirs();
								}
								String filePath = files.getAbsolutePath()+"/"+"1.pdf";
								fos = new FileOutputStream(filePath);
								byte[] buf = new byte[1024];
								int ch = -1;
								int process = 0;
								while ((ch = is.read(buf)) != -1) {
									fos.write(buf, 0, ch);
									process += ch;

									int percent = (int) Math.floor((process / total * 100));
									Log.e("percent", process+"--"+total+"--"+percent);
									Message msg = handler.obtainMessage(1001);
									msg.what = 1001;
									msg.obj = filePath;
									msg.arg1 = percent;
									handler.sendMessage(msg);

								}

							}
							fos.flush();
							fos.close();// 下载完成

						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (is != null) {
								is.close();
							}
							if (fos != null) {
								fos.close();
							}
						}

					}
				});
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1001) {
				if (tvPercent == null || pdfView == null) {
					return;
				}
				int percent = msg.arg1;
				tvPercent.setText(percent+"%");
				if (percent >= 100) {
					tvPercent.setVisibility(View.GONE);
					String filePath = msg.obj+"";
					if (!TextUtils.isEmpty(filePath)) {
						File file = new File(msg.obj+"");
						if (file.exists()) {
							pdfView.fromFile(file)
									.defaultPage(0)
									.scrollHandle(new DefaultScrollHandle(ShawnPDFActivity.this))
									.load();
						}
					}
				}
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.ivShare:
			CommonUtil.share(this, getString(R.string.app_name), getString(R.string.app_name), "", pdfUrl);
			break;

		default:
			break;
		}
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}
