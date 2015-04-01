/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.huzefagadi.brownbear.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huzefagadi.brownbear.R;
import com.huzefagadi.brownbear.beans.PhotoResponse;
import com.huzefagadi.brownbear.beans.PhotoResponse.ImagesUrl;
import com.huzefagadi.brownbear.utility.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
 

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImagePagerFragment extends BaseFragment {

	public static final int INDEX = 2;

	List<String> imageUrls;// = Constants.IMAGES;
	ImageAdapter imageAdapter;
	int i=0;
	DisplayImageOptions options;
	Context context;
	CountDownTimer timer;
    ViewPager viewPager;
    PhotoRecieverInternal receiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.resetViewBeforeLoading(true)
				.cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300))
				.build();
		
		context = getActivity();
		receiver = new PhotoRecieverInternal();
		
	    imageUrls = new ArrayList<String>();
	    imageUrls.add(Constants.IMAGES[0]);
	    imageUrls.add(Constants.IMAGES[1]);
	    imageUrls.add(Constants.IMAGES[2]);
	    imageUrls.add(Constants.IMAGES[3]);
	    imageUrls.add(Constants.IMAGES[4]);
	    imageUrls.add(Constants.IMAGES[5]);
	    imageUrls.add(Constants.IMAGES[6]);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		imageAdapter = new ImageAdapter();
		View rootView = inflater.inflate(R.layout.fr_image_pager, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        viewPager.setAdapter(imageAdapter);
        viewPager.setCurrentItem(0);
		
		try {
		    Field mScroller;
		    mScroller = ViewPager.class.getDeclaredField("mScroller");
		    mScroller.setAccessible(true); 
		    Interpolator sInterpolator = new AccelerateInterpolator();
		    FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(), sInterpolator);
		    // scroller.setFixedDuration(5000);
		  //  mScroller.set(pager, scroller);
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} 
		slideshow();
		return rootView;
	}
	
	public void slideshow()
	{
		if(timer == null)
		{
			

		}
        else
        {
            timer.cancel();
        }
        timer = new CountDownTimer(300000, 10000) {

            public void onTick(long millisUntilFinished) {
                if(i < imageUrls.size())
                {
                    i = viewPager.getCurrentItem()+1;
                    viewPager.setCurrentItem(i);
                    System.out.println("CALLED YES "+i);
                }
                else
                {
                    i=0;
                    viewPager.setCurrentItem(i,false);
                    System.out.println("CALLED NO");
                }

            }

            public void onFinish() {
                this.start();
            }
        }.start();
		
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		 try
         {
             context.unregisterReceiver(receiver);
         }
         catch (Exception e)
         {

         }

		if(timer!=null)
		{

			timer.cancel();
		}
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		IntentFilter intentFilterEdit = new IntentFilter("com.huzefagadi.brownbear.NOTIFY");
	    context.registerReceiver(receiver, intentFilterEdit);
		if(timer!=null)
		{
            slideshow();
		}

		
	}

	private class ImageAdapter extends PagerAdapter {

		private LayoutInflater inflater;

		ImageAdapter() {
			inflater = LayoutInflater.from(getActivity());
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return imageUrls.size();
		}
		
		
		

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			ImageLoader.getInstance().displayImage(imageUrls.get(position), imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					String message = null;
					switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
					}
					Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}
	
	public class FixedSpeedScroller extends Scroller {

	    private int mDuration = 1000;

	    public FixedSpeedScroller(Context context) {
	        super(context);
	    }

	    public FixedSpeedScroller(Context context, Interpolator interpolator) {
	        super(context, interpolator);
	    }

	    @SuppressLint("NewApi") 
	    public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
	        super(context, interpolator, flywheel);
	    }


	    @Override
	    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
	        // Ignore received duration, use fixed one instead
	        super.startScroll(startX, startY, dx, dy, mDuration);
	    }

	    @Override
	    public void startScroll(int startX, int startY, int dx, int dy) {
	        // Ignore received duration, use fixed one instead
	        super.startScroll(startX, startY, dx, dy, mDuration);
	    }
	}
	
	public void sendPostRequest()
	{
		class SendPostReqAsyncTask  extends AsyncTask<Void, Void, String> {

			protected String doInBackground(Void... urls) {
				Gson gson = new GsonBuilder().create();
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(
						"http://brownbearlabs.com/photo_frame/sample_api.json");// replace with your url
				httpPost.addHeader("Content-type",
						"application/json");
				 
				 
				try {
					 
					HttpResponse httpResponse = httpClient
							.execute(httpPost);
					InputStream inputStream = httpResponse.getEntity()
							.getContent();
					InputStreamReader inputStreamReader = new InputStreamReader(
							inputStream);
					BufferedReader bufferedReader = new BufferedReader(
							inputStreamReader);
					StringBuilder stringBuilder = new StringBuilder();
					String bufferedStrChunk = null;
					while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
						stringBuilder.append(bufferedStrChunk);
					}

					return stringBuilder.toString();

				} catch (ClientProtocolException cpe) {
					System.out
					.println("First Exception coz of HttpResponese :"
							+ cpe);
					cpe.printStackTrace();
				} catch (IOException ioe) {
					System.out
					.println("Second Exception coz of HttpResponse :"
							+ ioe);
					ioe.printStackTrace();
				}
				return null;
			}

			@SuppressLint("NewApi") 
			protected void onPostExecute(String result) {


				System.out.println(result);
				Gson gson = new GsonBuilder().create();
				PhotoResponse photoRespone = gson.fromJson(result, PhotoResponse.class);
				
				
				
				List <String> listOfImagesFromInternet = new ArrayList<String>();
				for( ImagesUrl url : photoRespone.getData().getPhotos())
				{
					listOfImagesFromInternet.add(url.getUrl());
				}
				
				if(imageUrls !=null && !imageUrls.isEmpty() &&listOfImagesFromInternet!=null &&!listOfImagesFromInternet.isEmpty()&& imageUrls.get(0).equals(listOfImagesFromInternet.get(0)))
				{
					System.out.println("NO CHANGES IN IMAGES");
				}
				{
					imageUrls.clear();
					imageUrls.addAll(listOfImagesFromInternet);
					imageAdapter.notifyDataSetChanged();
				}
				
				
			}

		}
		SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
		sendPostReqAsyncTask.execute();
	}
	
	
	class PhotoRecieverInternal extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			System.out.println("ALARM CALLED");
			sendPostRequest();
		}
    	
    }
}