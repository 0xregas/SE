import firebase_admin
from firebase_admin import credentials, auth, db

cred = credentials.Certificate("/home/ubuntu/Desktop/auth.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://sistemb-23c9a-default-rtdb.europe-west1.firebasedatabase.app/'
})


ref = db.reference('/Talho')
print(ref.get())