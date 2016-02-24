# Gps-tracker
**Get the apk from [here](http://www.mediafire.com/download/l18a13g5ucfdjzo/gps_tracker_1.1.apk).**
#### Screenshots
Gps monitor|in-app map|add new gps|customize each gps|change gps's name
-----------|---------|-----------|---------|-----------
![gps monitor](https://www.mediafire.com/convkey/857d/i3dh4h9u24niiiy6g.jpg)|![map](https://www.mediafire.com/convkey/7750/uftdaqsd8izz66n6g.jpg)|![add new gps](http://www.mediafire.com/convkey/6d91/ohg3gplcb1uq6ly6g.jpg)|![customization](https://www.mediafire.com/convkey/e182/fq710e40qx51nta6g.jpg)|![Edit name](http://www.mediafire.com/convkey/cc19/vcp8aea4j6ipnxw6g.jpg)

#### About the app
Hybrid android app used to track multiple GPS devices assembled on your cars,motorbikes,child,elderly
or anything you'd like to keep your eyes on.It uses sms and telephony to connect to its registered gps devices.Once a device is 
registered and turned on, the user can start tracking that device with just a single touch.To be as user-friendly as possible, vivid localized instructions will walk the user through the boring process of installation and registration of 
each gps.The app is also zaw/uni compactible.
> To see the big picture of how the app works,
have a look at the basic workflow of the gps used along with this app.

#### Basic workflow of the Gps

![Gps device](http://www.mediafire.com/convkey/dc14/84eksctowyy3jg16g.jpg)

* *Gps device used along with this app*

This device can be easily avialable in the market with a fair price of somewhere around `25 US $`. It is designed to be tracked from a mobile phone capable of displaying the gps location on a google map. Sms and telephony is used as a way of interacting between the gps and the handheld device.With a GSM simcard embeded inside, the simcard's number is used as the gps's identity which is also the target address to which sms & telephony interactions are performed.

Here're some of the sms command pattern used in interaction.
* To initialize the gps, send `#begin#123456#` to the gps.The replied sms `begin ok` indicates initialization's succeeded.
 `123456` is default password.
* To query gps' current location, send `#smslink#123456` to the gps.The sms replied will include gps' location,tracked time, IMEI etc.The replied lat/long will both be 0.0000 in case of failure.
* To change gps' password, send `#password#old-password#new-password#`.The replied sms `password ok` indicates password change's succeeded.
* To authenticate the gps, send `#admin#password#authenticating-number#`.The replied sms `admin ok` indicates authentication's success.

Overwhelming? I guess not in the case of tracking a single device.There're still more than half a dozen of such command left.Well here comes the pain. Imagine you're to track multiple gpses secretly assembled on your various vehicles , you type in those sms command and send to each device everytime you need to interact with them.Well it's the sole purpose of this app to eleminate all those tedious procedures required to interact with gps devices assembled.You don't need to memorize those commands or grap the user manual whenever it comes to start interacting. The app will do all the job for you.

> As all interactions solely rely on sms and telephony, make sure both your handheld and gps' simcard have enough balance.Otherwise,either of your device or gps' simcard won't be able to send or reply associated sms.

#### Weak points
* The device needs to be recharged very frequently. It can only be used as long as twelve hours when it's fully charged.
* It's not water-proof.

#### Potential improvements
* Use a different device model that can be assembled in such a way that it can levarage its hosted vehicle's power consumption.
* Move the app's architecture from listener-driven to intent-driven in interactions.
* Enable live tracking.

#### Credits
* [mmtext](https://github.com/htoomyintnaung/mmtext.git) by Ko Htoo Myint Naung.
* [System bar tint](https://github.com/jgilfelt/SystemBarTint.git) by Jeff Gilfelt.
* [CircleImageView](https://github.com/hdodenhof/CircleImageView.git) by Henning Dodenhof








