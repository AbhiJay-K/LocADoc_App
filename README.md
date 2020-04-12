# LocAdoc 

## About the application
This project aim to provide user a way to store confidential documents in mobile devices and access it only in the area he/she find it is safe. By including two factor protections, one being password (what the user knows) and second being the location (where the user is currently), we will be able to provide a better solution compared to the applications currently in the market (based on market survey).
These are few ways a document in a mobile device may be compromised: -  
-	The documents stored in mobile device may end up in the wrong hands if the device itself is stolen. 
-	The user may lend the device to someone who intern may wish to gain access to these documents.
-	The documents may be accessed remotely by penetrating device through network. 
  
  
Our solution aim to provide a secure vault for document storage so the it does not get into wrong hands even if the device is compromised. The solution also provide a secure backup cloud storage with double layer encryption one by the app itself and one by Amazon server.
  
## 2.2 Product Features
The features of this system are:
  
### 2.2.1 PDF Viewer
A PDF viewer for user to view his documents in the vault. The pdf viewer will only be accessible after the user has been authenticated and if the user is within the radius of the location stored in the database. The pdf viewer will close when the user moves out of this zone. The file that the user wishes to see will be the only one that will be decrypted. The rest will remain as cipher text even when the user is in authorised area. This pdf viewer will help the user to be more productive by having the ability to access sensitive document while moving within the secure location.

### 2.2.2 Deleting files
The user has the option to delete the files that are not needed and these files will also be deleted from the backup.

### 2.2.3 Setting preferred locational radius 
Once the user adds a new file he can set the radius he wishes with small radius being more secure and larger radius being more convenient. The files will be grouped based on the location and the user can choose the area if there is an overlap.

### 2.2.4 Less clustered interface
The user will be only able to view the files that was saved to a location making file accessing, pleasant and less tedious. 

### 2.2.5 Import files
The user will be able to import a new file from the local file directory and secure it through encryption. The original file can be deleted to prevent adversary from viewing it.

### 2.2.6 Secure cloud storage
The data will be safely stored in the central database with additional layer of encryption by the cloud infrastructure provider.
These are the key features that will be included in the application. Further enhancements such as support for more file types will be added if these basic requirements are met.

