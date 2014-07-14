
package org.ashanet.interfaces;

import android.support.v4.app.Fragment;

public interface FragmentNavigation {
    abstract void pushFragment(Fragment newFrag, int titleResourceId, String newFragTitle);
    abstract void popFragment(int titleResourceId);
}
