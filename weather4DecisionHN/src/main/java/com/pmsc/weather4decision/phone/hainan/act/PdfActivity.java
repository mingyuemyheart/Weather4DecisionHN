package com.pmsc.weather4decision.phone.hainan.act;

import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.android.lib.data.JsonMap;
import com.lidong.pdf.PDFView;
import com.lidong.pdf.listener.OnDrawListener;
import com.lidong.pdf.listener.OnLoadCompleteListener;
import com.lidong.pdf.listener.OnPageChangeListener;
import com.pmsc.weather4decision.phone.hainan.R;


/**
 * Depiction: PDF文件加载界面
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年11月13日 下午4:33:38
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class PdfActivity extends AbsDrawerActivity implements OnPageChangeListener, OnLoadCompleteListener, OnDrawListener {
	public final static String PDF_URL = "pdf_url";
	private PDFView            pdfView;
	private String             pdfUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(false);
		leftButton.setBackgroundResource(R.drawable.back);
		rightButton.setBackgroundResource(R.drawable.share);
		rightButton.setVisibility(View.VISIBLE);
		setContentView(R.layout.activity_abs_pdf);
		
		pdfView = (PDFView) findViewById(R.id.pdf_view);
		pdfView.enableDoubletap(true);
		pdfView.enableSwipe(true);

		if (getIntent().hasExtra("parm1")) {//推送消息
			JsonMap data = JsonMap.parseJson(getIntent().getStringExtra("parm1"));
			pdfUrl = data.getString("pdfUrl");
		}else {
			pdfUrl = getIntent().getStringExtra(PDF_URL);
		}

		if (TextUtils.isEmpty(pdfUrl)) {
			showToast(R.string.loading_fail);
			return;
		}
		String[] array = pdfUrl.split("/");
		if (array != null && array.length > 0) {
			displayFromFile1(pdfUrl, array[array.length-1]);
		}
	}
	
	/**
     * 获取打开网络的pdf文件
     * @param fileUrl
     * @param fileName
     */
    private void displayFromFile1( String fileUrl ,String fileName) {
        pdfView.fileFromLocalStorage(this,this,this,fileUrl,fileName);   //设置pdf文件地址
    }

    /**
     * 翻页回调
     * @param page
     * @param pageCount
     */
    @Override
    public void onPageChanged(int page, int pageCount) {
//        Toast.makeText( MainActivity.this , "page= " + page + " pageCount= " + pageCount , Toast.LENGTH_SHORT).show();
    }

    /**
     * 加载完成回调
     * @param nbPages  总共的页数
     */
    @Override
    public void loadComplete(int nbPages) {
//        Toast.makeText( MainActivity.this ,  "加载完成" + nbPages  , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
        // Toast.makeText( MainActivity.this ,  "pageWidth= " + pageWidth + "
        // pageHeight= " + pageHeight + " displayedPage="  + displayedPage , Toast.LENGTH_SHORT).show();
    }
    
}
