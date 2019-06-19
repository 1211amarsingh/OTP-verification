# Automatic SMS/ OTP Verification with the SMS Retriever API
## Say no to read sms permission

### * Work if package name is registered on play store

With the SMS Retriever API, you can perform SMS-based user verification in your Android app automatically, without requiring the user to manually type verification codes, and without requiring any extra app permissions. When you implement automatic SMS verification in your app, the verification flow looks like this:
<img src="https://developers.google.com/identity/sms-retriever/flow-overview.png">


### Dependency for SMS Retriever API
<code>implementation "com.google.android.gms:play-services-auth:16.0.1"</code>

### App Side Implimentation -> Follow the code of demo

### Server-side implementation

To get the SmsRetrieverClient to work, a specific format to be followed at server side while creating the OTP message.

* 1. The message should start with the prefix <#>
* 2. The message must be no longer than 140 bytes
* 3. The message should end with the 11-character hash string that identifies your app.
* 4. Sample verification message received at the mobile side would look something like this.
    ```
    <#> 123456 is your secret one time password (OTP)
     to login to your Account. 7bhnjAR5gBE
     ```
## Demo
Feel free to clone this project and run in your IDE to see how can be implemented :).
