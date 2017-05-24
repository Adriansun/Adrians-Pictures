package com.example.ansoas.adrianpictures;


/**
 * The class os a setter and getter class of id's and strings.
 *
 * @author Adrian Lundhe
 */
class GalleryItem
{
  private String mCaption;
  private String mId;
  private String mUrl;

  /**
   * getCaption.
   * @return mCaption get the caption
   */
  public String getCaption() {
    return mCaption;
  }

  /**
   * setCaption.
   * @param caption set the caption
   */
  void setCaption(String caption) {
    mCaption = caption;
  }


  /**
   * getId
   * @return mId return id
   */
  public String getId() {
    return mId;
  }


  /**
   * setId.
   * @param id set the id
   */
  public void setId(String id) {
    mId = id;
  }


  /**
   * getUrl.
   * @return mUrl return url
   */
  String getUrl() {
    return mUrl;
  }


  /**
   * SetUrl
   * @param url set the url
   */
  void setUrl(String url) {
    mUrl = url;
  }


  /**
   * Convert to string.
   * @return mCaption caption to string
   */
  public String toString() {
    return mCaption;
  }
}
