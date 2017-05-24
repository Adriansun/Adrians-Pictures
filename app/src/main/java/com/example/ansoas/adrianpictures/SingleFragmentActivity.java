package com.example.ansoas.adrianpictures;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Fragment class finds fragments in the fragment container and adds them.
 *
 * @author Adrian Lundhe
 */
public abstract class SingleFragmentActivity extends FragmentActivity
{
  protected abstract Fragment createFragment();
  /**
   * When start: set content and if there is no fragment, then get it.
   * @param savedInstanceState saved instance state
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    FragmentManager manager = getSupportFragmentManager();
    Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

    // If null then add fragments.
    if (fragment == null) {
      fragment = createFragment();
      manager.beginTransaction()
          .add(R.id.fragmentContainer, fragment)
          .commit();
    }
  }
}