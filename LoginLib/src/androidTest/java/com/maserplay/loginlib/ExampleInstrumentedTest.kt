package com.maserplay.loginlib


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
    @Test
    fun LoginTest() {
        val email: String = getRandomString(10)
        val password: String = getRandomString(10)
        onView(withId(R.id.Email))
            .perform(typeText(email));
        onView(withId(R.id.Password))
            .perform(typeText(password));
        onView(withId(R.id.Email))
            .check(matches(withText(email)));
        onView(withId(R.id.Password))
            .check(matches(withText(password)));

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.maserplay.loginlib.test", appContext.packageName)
    }
}