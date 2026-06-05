package com.example.yourjob;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

<<<<<<< HEAD
=======
/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
<<<<<<< HEAD
=======
        // Context of the app under test.
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.yourjob", appContext.getPackageName());
    }
}