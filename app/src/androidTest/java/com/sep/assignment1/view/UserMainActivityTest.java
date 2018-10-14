package com.sep.assignment1.view;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.runner.RunWith;
import android.support.test.rule.ActivityTestRule;
import org.junit.Test;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.*;
import com.sep.assignment1.R;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
@RunWith(AndroidJUnit4.class)

@LargeTest
public class UserMainActivityTest {
    @Rule
    public ActivityTestRule <UserMainActivity> rule = new ActivityTestRule<>(UserMainActivity.class);

    @Test
    public void scrollToPosition(){

        onView(withId(R.id.user_restaurant_recycler_view)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Hungry Jacks")),click()));

    }
}