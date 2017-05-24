package com.example.ansoas.adrianpictures;

import android.net.Uri;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author Adrian Lundhe
 */
class FlickrFetchr {
  private static final String TAG = "PhotoFetcher";
  // ENDPOINT = Own private key for Flickr.
  private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
  private static final String API_KEY = "ab9038def6ff78fde9cac545e30b94ba";
  private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
  private static final String PARAM_EXTRAS = "extras";
  private static final String EXTRA_SMALL_URL = "url_s";
  private static final String XML_PHOTO = "photo";

  /**
   * Handles the connection to Flickr.
   *
   * @param urlSpec url
   * @return connection
   * @throws IOException exception
   */
  byte[] getUrlBytes(String urlSpec) throws IOException {
    URL url = new URL(urlSpec);
    HttpURLConnection connection = (HttpURLConnection)url.openConnection();

    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      InputStream in = connection.getInputStream();

      if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        return null;
      }

      int bytesRead = 0;
      byte[] buffer = new byte[1024];
      while ((bytesRead = in.read(buffer)) > 0) {
        out.write(buffer, 0, bytesRead);
      }
      out.close();
      return out.toByteArray();
    } finally {
      connection.disconnect();
    }
  }


  /**
   * Get Url to Flickr.
   * @param urlSpec url
   * @return string of url
   * @throws IOException exception
   */
  private String getUrl(String urlSpec) throws IOException
  {
    return new String(getUrlBytes(urlSpec));
  }


  /**
   * Contacts Flickr and get items.
   * @return items items
   */
  ArrayList<GalleryItem> fetchItems() {
    ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();

    try {
      String url = Uri.parse(ENDPOINT).buildUpon()
              .appendQueryParameter("method", METHOD_GET_RECENT)
              .appendQueryParameter("api_key", API_KEY)
              .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
              .build().toString();
      String xmlString = getUrl(url);
      Log.i(TAG, "Received xml: " + xmlString);
      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
      XmlPullParser parser = factory.newPullParser();
      parser.setInput(new StringReader(xmlString));

      parseItems(items, parser);
    } catch (IOException ioe) {
      Log.e(TAG, "Failed to fetch items", ioe);
    } catch (XmlPullParserException xppe) {
      Log.e(TAG, "Failed to parse items", xppe);
    }
    return items;
  }


  /**
   * Parsing the pictures / items.
   * @param items items
   * @param parser parser
   * @throws XmlPullParserException exception
   * @throws IOException exeption
   */
  private void parseItems(ArrayList<GalleryItem> items, XmlPullParser parser) throws XmlPullParserException,
          IOException {
    int eventType = parser.next();

    while (eventType != XmlPullParser.END_DOCUMENT)
    {
      if (eventType == XmlPullParser.START_TAG &&
              XML_PHOTO.equals(parser.getName()))
      {
        String id = parser.getAttributeValue(null, "id");
        String caption = parser.getAttributeValue(null, "title");
        String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
        GalleryItem item = new GalleryItem();
        item.setId(id);
        item.setCaption(caption);
        item.setUrl(smallUrl);
        items.add(item);
      }
      eventType = parser.next();
    }
  }
}