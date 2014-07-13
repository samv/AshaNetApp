
package org.ashanet.interfaces;

import android.support.v4.app.Fragment;

public interface FragmentNavigation {
    abstract void pushFragment(Fragment newFrag, String tag);
    abstract void popFragment(String tag);
}
