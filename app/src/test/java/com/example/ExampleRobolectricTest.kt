package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.quotes.QuoteBank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Rise", appName)
  }

  @Test
  fun `verify quote bank has at least 365 quotes`() {
    val quotes = QuoteBank.quotes
    assertTrue("Quotes count must be >= 365, was ${quotes.size}", quotes.size >= 365)
  }
}
