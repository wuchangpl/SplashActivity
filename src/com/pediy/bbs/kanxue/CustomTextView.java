package com.pediy.bbs.kanxue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomTextView extends TextView {
	boolean dontConsumeNonUrlClicks = true;
	boolean linkHit;

	public CustomTextView(Context context) {
		super(context);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public static String addAToString(String context)
    {
        //为<img/>加<a src=".."></a>
		String src="";
        Pattern pimg = Pattern.compile("(\\<img)(.*?)(\\>)");
        Pattern psrc = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)");
        Matcher mimg=pimg.matcher(context);
        while(mimg.find()){
        	Matcher msrc = psrc.matcher(mimg.group());
        	 while(msrc.find()){
        		 src = msrc.group();
        		 int start = src.indexOf("src=\"");
     			 int end = src.indexOf("\"",start+5);
     			context=context.replace(mimg.group(), "<a href='"+src.substring(start+5, end)+"'>"+mimg.group()+"</a>");
             }
            
        }        
        return context;
    }
	public static void ThumbnailAttachmentsClick(String mUrl, View widget) {
		Bundle data = new Bundle();
		data.putString("url", mUrl);
		Intent intent = new Intent(widget.getContext(), ImageActivity.class);
		intent.putExtras(data);
		widget.getContext().startActivity(intent); 
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		linkHit = false;
		boolean res = super.onTouchEvent(event);

		if (dontConsumeNonUrlClicks)
			return linkHit;
		return res;

	}
	public static class LocalLinkMovementMethod extends LinkMovementMethod {
		static LocalLinkMovementMethod sInstance;

		public static LocalLinkMovementMethod getInstance() {
			if (sInstance == null)
				sInstance = new LocalLinkMovementMethod();

			return sInstance;
		}

		@Override
		public boolean onTouchEvent(TextView widget, Spannable buffer,
				MotionEvent event) {
			int action = event.getAction();
			Log.v("thing", "LocalLinkMovementMethod==========");
			if (action == MotionEvent.ACTION_UP
					|| action == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX();
				int y = (int) event.getY();

				x -= widget.getTotalPaddingLeft();
				y -= widget.getTotalPaddingTop();

				x += widget.getScrollX();
				y += widget.getScrollY();

				Layout layout = widget.getLayout();
				int line = layout.getLineForVertical(y);
				int off = layout.getOffsetForHorizontal(line, x);

				ClickableSpan[] link = buffer.getSpans(off, off,
						ClickableSpan.class);

				if (link.length != 0) {
					if (action == MotionEvent.ACTION_UP) {
							String content = ((URLSpan)link[0]).getURL();
						    Log.v("thing",content);
						    LinkClick(content,widget);
					} else if (action == MotionEvent.ACTION_DOWN) {
						Selection.setSelection(buffer,
								buffer.getSpanStart(link[0]),
								buffer.getSpanEnd(link[0]));
					}

					if (widget instanceof CustomTextView) {
						((CustomTextView) widget).linkHit = true;
					}
					return true;
				} else {
					Selection.removeSelection(buffer);
					Touch.onTouchEvent(widget, buffer, event);
					return false;
				}
			}
			return Touch.onTouchEvent(widget, buffer, event);
		}
	}
	
	public static void LinkClick(String urlstr, TextView widget)
	{
		if(urlstr.contains("attachmentid") && 
		   urlstr.contains("thumb") && 
		   urlstr.contains("bbs.pediy.com"))
		{
			CustomTextView.ThumbnailAttachmentsClick(urlstr, widget);
			Log.v("thing", "=============你点击的是图片=============");
		}else if(urlstr.contains("attachmentid") &&
				 urlstr.contains("bbs.pediy.com"))
		{
			Toast.makeText(widget.getContext(), "暂不支持附件下载", Toast.LENGTH_SHORT).show();
			Log.v("thing", "=============你点击的是附件=============");
		}else if(urlstr.contains("http://bbs.pediy.com/images/smilies")){}
		else
		{
			 Context context = widget.getContext();
		        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlstr));
		        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
		        context.startActivity(intent);
			Log.v("thing", "=============你点击的是超链接=============");
		}
		
	}
}
