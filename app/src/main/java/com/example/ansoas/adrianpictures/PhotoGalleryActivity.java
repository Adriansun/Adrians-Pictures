package com.example.ansoas.adrianpictures;

import android.support.v4.app.Fragment;

/**
 * Class adds creates fragments.
 *
 * @author Adrian Lundhe
 */
public class PhotoGalleryActivity extends SingleFragmentActivity {
  /**
   * Start fragment.
   * @return PhotoGalleryFragment() new fragment for the photo gallery
   */
  @Override
  public Fragment createFragment() {
    return new PhotoGalleryFragment();
  }
}