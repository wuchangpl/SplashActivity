package com.pediy.bbs.kanxue;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pediy.bbs.kanxue.widget.DragImageView;

public class ImageActivity extends Activity {
 
	private DragImageView  dragImageView = null;
	private  ProgressBar      progressBar = null;
	     
 
	
	    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.imageshow); 
		
		dragImageView = (DragImageView) findViewById(R.id.div_main);
		progressBar = (ProgressBar) findViewById(R.id.progressBar3);
		
		Bundle data = this.getIntent().getExtras();
		String imageURL = data.getString("url");
		
		BitmapDownloaderTask task = new BitmapDownloaderTask(dragImageView);
        task.execute(imageURL);
     
	}
	
	private Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					final Bitmap bitmap = BitmapFactory
							.decodeStream(inputStream);
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			getRequest.abort();
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {

		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		protected Bitmap doInBackground(String... params) {
			return downloadBitmap(params[0]);
		}

		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
					progressBar.setVisibility(View.INVISIBLE);
				}
			}
		}
	}
}