# SiFli_OTA_APP
This is a demo project of SifliDfu Library, which demonstrates the use of SifliDfu for OTA

##1.ota nand
this.sifliDFUService.startActionDFUNand(this, bluetoothAddress, imagePaths, 1, 0);

##2.ota nor
sifliDFUService.startActionDFUNor(this,bluetoothAddress,imagePaths,mode,0);

##3.ota nor offline
sifliDFUService.startActionDFUNorOffline(this,bluetoothAddress,image.getImageUri(),null);