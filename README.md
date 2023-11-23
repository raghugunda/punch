# todo list in kotlin
                 Punch Interview Task

1. Design a data structure or format to organize and transmit the captured analytics data to the
server
2. Establish a connection and ensure secure data transmission to the server using appropriate
   protocols. 
3. Implement error handling and logging mechanisms to address any potential issues during the
   data transfer process.

Process

1. Added Retrofit and Okhttp logger Dependencies in build.gradle
   //retrofit
   implementation 'com.squareup.retrofit2:retrofit:2.9.0'
   implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
   implementation 'com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2'
   implementation 'com.squareup.okhttp3:okhttp:5.0.0-alpha.2'

2. Added Required Permission in Androidmanifest.xml
   <uses-permission android:name="android.permission.INTERNET"/>
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

3. Created Okhttp Client Class to Send data to Server. Here I Don’t Have Endpoint So I Added
   Company URL for Refference. Snapshots Available in Document

4. Call The Api In ViewModel

5. Check Internet Connection Before Calling API

6. Once Data Sent to Server Response Like This. Current Company URL is Passed because
    Endpoint Details Not there.

7. Log All action Using Logger class

Please Refer github link or attached document on mail


