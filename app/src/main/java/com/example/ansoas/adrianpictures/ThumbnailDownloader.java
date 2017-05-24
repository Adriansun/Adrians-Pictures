package com.example.ansoas.adrianpictures;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.os.Handler;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class ThumbnailDownloader gathers the thumbnails from Flickr.
 *
 * @author Adrian Lundhe
 */
public class ThumbnailDownloader<Handle> extends HandlerThread
{
  private static final String TAG = "ThumbnailDownloader";
  private static final int MESSAGE_DOWNLOAD = 0;
  Handler mHandler;
  Map<Handle,String> requestMap = Collections.synchronizedMap(new HashMap<Handle, String>());
  Handler mResponseHandler;
  Listener<Handle> mListener;

  /**
   * Listener.
   * @param <Handle> handles the thumbnails
   */
  interface Listener<Handle> {
    void onThumbnailDownloaded(Handle handle, Bitmap thumbnail);
  }

  /**
   * Setting the listener.
   * @param listener listener
   */
  void setListener(Listener<Handle> listener) {
    mListener = listener;
  }

  /**
   * Handling the responses.
   * @param responseHandler response handler
   */
  ThumbnailDownloader(Handler responseHandler) {
    super(TAG);
    mResponseHandler = responseHandler;
  }

  /**
   * Handling the loop of downloading.
   */
  @SuppressLint("HandlerLeak")
  @Override
  protected void onLooperPrepared() {
    mHandler = new Handler() {
      @Override
      public void handleMessage(Message msg) {

        if (msg.what == MESSAGE_DOWNLOAD) {
          @SuppressWarnings("unchecked")
          Handle handle = (Handle)msg.obj;
          Log.i(TAG, "Request for url: " + requestMap.get(handle));
          handleRequest(handle);
        }
      }
    };
  }

  /**
   * Asking for a handle
   * @param handle handle
   */
  private void handleRequest(final Handle handle) {
    try {
      final String url = requestMap.get(handle);

      if (url == null)
        return;

      byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
      final Bitmap bitmap = BitmapFactory
              .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);

      mResponseHandler.post(new Runnable() {
        public void run() {
          if (requestMap.get(handle) != url)
            return;

          requestMap.remove(handle);
          mListener.onThumbnailDownloaded(handle, bitmap);
        }
      });
    } catch (IOException ioe) {
      Log.e(TAG, "Error downloading the image to handler.", ioe);
    }
  }

  /**
   * Puts the thumbnails in a queue.
   * @param handle handle
   * @param url url
   */
  void queueThumbnail(Handle handle, String url) {
    requestMap.put(handle, url);
    mHandler
            .obtainMessage(MESSAGE_DOWNLOAD, handle)
            .sendToTarget();
  }

  /**
   * Emptying the queue.
   */
  void clearQueue() {
    mHandler.removeMessages(MESSAGE_DOWNLOAD);
    requestMap.clear();
  }
}