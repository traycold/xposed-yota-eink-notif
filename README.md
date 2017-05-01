# xposed-yota-eink-notif
Filter by app the notification widget of the always-on display of Yotaphone 2.
## Usage
[Download](../../releases/latest) and install the module. Create on root of SD-Card a file called `no-notification.properties` with a list of the packages (one per line) you want to exclude from notification widget. Reboot the phone for the modifications on the file to take effect.
> :bulb: Typically you can find the package name of an app looking at the url of the detail page of the app on the Play Store (browser); the package name is the value of the param "id".

When the module is installed and there is no file `no-notification.properties` on the SD-Card, a new empty one will be created.
