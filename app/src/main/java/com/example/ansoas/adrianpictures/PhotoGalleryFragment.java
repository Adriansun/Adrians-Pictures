package com.example.ansoas.adrianpictures;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.os.Handler;
import java.util.ArrayList;

/**
 * Class handles all fragments.
 *
 * @author Adrian Lundhe
 */
public class PhotoGalleryFragment extends Fragment
{
  GridView mGridView;
  ArrayList<GalleryItem> mItems;
  ThumbnailDownloader<ImageView> mThumbnailThread;

  /**
   * Saving the instance.
   * @param savedInstanceState saved instance state
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
    new FetchItemsTask().execute();

    mThumbnailThread = new ThumbnailDownloader<ImageView>(new Handler());
    mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {
      public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
        if (isVisible()) {
          imageView.setImageBitmap(thumbnail);
        }
      }
    });
    mThumbnailThread.start();
    mThumbnailThread.getLooper();
  }

  /**
   * Start the photo gallery.
   * @param inflater inflate
   * @param container container
   * @param savedInstanceState saved instance state
   * @return v view
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

    mGridView = (GridView)v.findViewById(R.id.gridView);

    setupAdapter();

    return v;
  }

  /**
   * When stopping; stop the thread with thumbnails.
   */
  @Override
  public void onDestroy() {
    super.onDestroy();
    mThumbnailThread.quit();
  }

  /**
   * When stopping; stop the queue with thumbnails in the view.
   */
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mThumbnailThread.clearQueue();
  }

  /**
   * Setting the adapter for the view if there is anything there.
   */
  void setupAdapter() {
    if (getActivity() == null || mGridView == null)
      return;

    if (mItems != null) {
      mGridView.setAdapter(new GalleryItemAdapter(mItems));
    } else {
      mGridView.setAdapter(null);
    }
  }

  /**
   * Add items.
   */
  private class FetchItemsTask extends AsyncTask<Void,Void,ArrayList<GalleryItem>> {
    @Override
    protected ArrayList<GalleryItem> doInBackground(Void... params) {
      return new FlickrFetchr().fetchItems();
    }

    /**
     * After starting add items and start adapter.
     * @param items items
     */
    @Override
    protected void onPostExecute(ArrayList<GalleryItem> items) {
      mItems = items;
      setupAdapter();
    }
  }

  /**
   * Gallery adapter.
   */
  private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {
    public GalleryItemAdapter(ArrayList<GalleryItem> items) {
      super(getActivity(), 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = getActivity().getLayoutInflater()
                .inflate(R.layout.gallery_item, parent, false);
      }

      GalleryItem item = getItem(position);
      ImageView imageView = (ImageView)convertView
              .findViewById(R.id.gallery_item_imageView);
      mThumbnailThread.queueThumbnail(imageView, item.getUrl());

      return convertView;
    }
  }
}