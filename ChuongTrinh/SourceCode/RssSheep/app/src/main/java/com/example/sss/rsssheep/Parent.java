package com.example.sss.rsssheep;

import java.util.List;

/**
 * Created by SSS on 25/04/2017.
 */

public interface Parent<C> {
    /**
     * Getter for the list of this parent's child items.
     * <p>
     * If list is empty, the parent has no children.
     *
     * @return A {@link List} of the children of this {@link Parent}
     */
    List<C> getChildList();
    /**
     * Getter used to determine if this {@link Parent}'s
     * {@link android.view.View} should show up initially as expanded.
     *
     * @return true if expanded, false if not
     */
    boolean isInitiallyExpanded();
}
