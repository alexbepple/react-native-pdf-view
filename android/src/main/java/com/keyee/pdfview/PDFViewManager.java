package com.keyee.pdfview;

import java.io.File;
import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.util.Log;
import android.graphics.PointF;
import android.graphics.Bitmap;
import android.util.Base64;
import android.graphics.Canvas;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnDrawListener;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.common.MapBuilder;

import static java.lang.String.format;

public class PDFViewManager extends SimpleViewManager<PDFView>
    implements OnPageChangeListener, OnLoadCompleteListener, OnDrawListener {

    private static final String REACT_CLASS = "RCTPDFViewAndroid";
    private Context context;
    private PDFView pdfView;
    Integer pageNumber = 1;
    String fileName;

    public PDFViewManager(ReactApplicationContext reactContext){
        this.context = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public PDFView createViewInstance(ThemedReactContext context) {
        if (pdfView == null){
            pdfView = new PDFView(context, null);
        }
        return pdfView;
        //return new PDFView(context, null);
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        showLog(format("%s %s / %s", fileName, page, pageCount));
    }

    @Override
    public void loadComplete(int nbPages) {
        WritableMap event = Arguments.createMap();
        event.putString("message", ""+nbPages);

        ReactContext reactContext = (ReactContext)pdfView.getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
            pdfView.getId(),
            "topChange",
            event
         );
    }

    private boolean renderedThumbnail = false;

    public void renderThumbnail() {
        if (renderedThumbnail) return;
        Bitmap bitmap = pdfView.getThumbnail();
        if (bitmap == null) return;

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        String thumbnail = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);

        WritableMap event = Arguments.createMap();
        event.putString("thumbnailJpegBase64", thumbnail);

        ReactContext reactContext = (ReactContext)pdfView.getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
            pdfView.getId(),
            "topLayout",
            event
        );

        renderedThumbnail = true;
    }

    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
        renderThumbnail();
    }

    private void display(boolean jumpToFirstPage) {
        if (jumpToFirstPage)
            pageNumber = 1;
        showLog(format("display %s %s", fileName, pageNumber));
        if (fileName != null){
            //fromFile,fromAsset
            //pdfView.fromAsset(fileName)
            File pdfFile = new File(fileName);
            pdfView.fromFile(pdfFile)
                .defaultPage(pageNumber)
                //.showMinimap(false)
                //.enableSwipe(true)
                .swipeVertical(true)
                .onPageChange(this)
                .onLoad(this)
                .onDraw(this)
                .load();
        }
    }

    @ReactProp(name = "src")
    public void setSrc(PDFView view, String src) {
        //view.setSource(src);
        fileName = src;
        display(false);
    }

    @ReactProp(name = "pageNumber")
    public void setPageNumber(PDFView view, Integer pageNum) {
        //view.setPageNumber(pageNum);
        if (pageNum > 0){
            pageNumber = pageNum;
            display(false);
        }
    }

    @ReactProp(name = "zoom")
    public void zoomTo(PDFView view, float zoomScale) {
        PointF pivot = new PointF(zoomScale, zoomScale);
        pdfView.zoomCenteredTo(zoomScale, pivot);
    }

    private void showLog(final String str) {
        Log.w(REACT_CLASS, str);
    }
}
