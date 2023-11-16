package com.dy.app.db;

import android.util.Log;

import com.dy.app.gameplay.Player;
import com.dy.app.ui.view.FragmentCreateAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Database {
    public static final String TAG = "Database";
    private FirebaseFirestore db = null;
    private FirebaseAuth auth = null;
    private static Database instance = null;
    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_FAILED = -1;
    private Database() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public void signUpWithEmailAndPassword(FragmentCreateAccount.RegisterInformation registerInformation, OnDBRequestListener listener){
        if(registerInformation.email == null || registerInformation.email.isEmpty()){
            listener.onDBRequestCompleted(RESULT_FAILED, "Email is empty");
            return;
        }

        if(registerInformation.password == null || registerInformation.password.isEmpty()){
            listener.onDBRequestCompleted(RESULT_FAILED, "Password is empty");
            return;
        }


        try {
            auth.createUserWithEmailAndPassword(registerInformation.email, registerInformation.password)
                .addOnCompleteListener(task -> {
                    try{
                        if(task.isSuccessful()){
                            listener.onDBRequestCompleted(RESULT_SUCCESS, "Sign up successful");
                        }else{
                            listener.onDBRequestCompleted(RESULT_FAILED, getUserMessage(task.getException()));
                        }
                    }catch(Exception e){
                        listener.onDBRequestCompleted(RESULT_FAILED, getUserMessage(e));
                    }
                });
        }catch(Exception e){
            listener.onDBRequestCompleted(RESULT_FAILED, getUserMessage(e));
        }


    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void checkForPlayerInitialization(OnDBRequestListener listener){
        if(!isSignedIn()){
            throw new RuntimeException("User is not signed in");
        }
        DocumentReference docRef = db.collection("users")
                .document(auth.getCurrentUser().getUid()).collection("user_data").document("profile");

        try{
            docRef.get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                listener.onDBRequestCompleted(RESULT_SUCCESS, true);
                            }else{
                                listener.onDBRequestCompleted(RESULT_SUCCESS, false);
                            }
                        }else{
                            listener.onDBRequestCompleted(RESULT_FAILED, getUserMessage(task.getException()));
                        }
                    });
        }catch (Exception e){
            listener.onDBRequestCompleted(RESULT_FAILED, getUserMessage(e));
        }

    }

    public void pushAllPlayerData(OnDBRequestListener listener){
        OnDBRequestListener allListener = new OnDBRequestListener() {
            int count = 6;
            boolean failed = false;
            @Override
            public void onDBRequestCompleted(int result, Object object) {
                if(result == RESULT_SUCCESS){
                    if(failed){
                        return;
                    }
                    count--;
                    if(count == 0 &&listener != null){
                        listener.onDBRequestCompleted(RESULT_SUCCESS, null);
                    }
                    return;
                }else{
                    failed = true;
                    if(listener != null)
                    {
                        listener.onDBRequestCompleted(RESULT_FAILED, null);
                    }

                }
            }
        };

        updateUserProfileOnDB(allListener);
        updateUserInventoryOnDB(allListener);
        updateBattlePassOnDB(allListener);
        updateUserStatisticsOnDB(allListener);
        updateUserPurchaseOnDB(allListener);
        updateUserPreferencesOnDB(allListener);
    }

    public void updateUserProfileOnDB(OnDBRequestListener listener){
        Player player = Player.getInstance();
        DocumentReference profileRef =  getUserDataColRef().document("profile");
        Map<String, Object> data = player.profile.getData();
        if(auth.getCurrentUser().isAnonymous()){
            data.put("isAnonymous", true);
        }
        updateDocRef(profileRef, data, listener);
    }

    public void updateUserInventoryOnDB(OnDBRequestListener listener) {
        Player player = Player.getInstance();
        DocumentReference inventoryRef =  getUserDataColRef().document("inventory");
        updateDocRef(inventoryRef, player.inventory.getData(), listener);
    }

    public void updateBattlePassOnDB(OnDBRequestListener listener) {
        Player player = Player.getInstance();
        DocumentReference battlePassRef =  getUserDataColRef().document("battle_pass");
        updateDocRef(battlePassRef, player.battlePass.getData(), listener);
    }

    private void updateUserStatisticsOnDB(OnDBRequestListener allListener) {
        Player player = Player.getInstance();
        DocumentReference statisticsRef =  getUserDataColRef().document("statistics");
        updateDocRef(statisticsRef, player.statistics.getData(), allListener);
    }


    private void updateUserPurchaseOnDB(OnDBRequestListener allListener) {
        Player player = Player.getInstance();
        DocumentReference purchaseRef =  getUserDataColRef().document("purchase");
        updateDocRef(purchaseRef, player.purchase.getData(), allListener);
    }

    private void updateUserPreferencesOnDB(OnDBRequestListener allListener) {
        Player player = Player.getInstance();
        DocumentReference preferencesRef =  getUserDataColRef().document("preferences");
        updateDocRef(preferencesRef, player.preferences.getData(), allListener);
    }

    public void fetchAllPlayerData(OnDBRequestListener listener){
        OnDBRequestListener allListener = new OnDBRequestListener() {
            int count = 6;
            boolean failed = false;
            @Override
            public void onDBRequestCompleted(int result, Object object) {
                if(result == RESULT_SUCCESS){
                    if(failed){
                        return;
                    }
                    count--;
                    if(count == 0){
                        listener.onDBRequestCompleted(RESULT_SUCCESS, null);
                    }
                    return;
                }else{
                    failed = true;
                    listener.onDBRequestCompleted(RESULT_FAILED, null);
                }
            }
        };

        fetchPlayerProfile(allListener);
        fetchPlayerInventory(allListener);
        fetchBattlePass(allListener);
        fetchPlayerStatistics(allListener);
        fetchPlayerPurchase(allListener);
        fetchPlayerPreferences(allListener);
    }

    public void signInWithEmailAndPassword(String email, String password, OnDBRequestListener listener) throws ExecutionException, InterruptedException {
        if(email == null || email.isEmpty()){
            listener.onDBRequestCompleted(RESULT_FAILED, "Email is empty");
            return;
        }

        if(password == null || password.isEmpty()){
            listener.onDBRequestCompleted(RESULT_FAILED, "Password is empty");
            return;
        }

         auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    try{
                        if(task.isSuccessful()){

                            listener.onDBRequestCompleted(RESULT_SUCCESS, "Sign in successful");

                        }else{
                            listener.onDBRequestCompleted(RESULT_FAILED, getUserMessage(task.getException()));
                        }
                    }catch(Exception e){
                        listener.onDBRequestCompleted(RESULT_FAILED, getUserMessage(e));
                    }
                });

    }

    public synchronized void fetchPlayerProfile(OnDBRequestListener listener){
        selfCheckUserSignedIn();

        DocumentReference userProfileDocRef = db.collection("users").document(auth.getCurrentUser().getUid())
                .collection("user_data").document("profile");

        getDocument(userProfileDocRef, (res, o)->{
            if(res == RESULT_SUCCESS){
                Player.getInstance().profile.putAll((Map<String, Object>) o);
            }
            listener.onDBRequestCompleted(res, o);
        });
    }

    public synchronized void fetchPlayerInventory(OnDBRequestListener listener){
        selfCheckUserSignedIn();

        DocumentReference userInventoryDocRef = db.collection("users").document(auth.getCurrentUser().getUid())
                .collection("user_data").document("inventory");

        getDocument(userInventoryDocRef, (res, o)->{
            if(res == RESULT_SUCCESS){
                Player.getInstance().inventory.putAll((Map<String, Object>) o);
            }
            listener.onDBRequestCompleted(res, o);
        });
    }

    public synchronized void fetchBattlePass(OnDBRequestListener listener) {
        selfCheckUserSignedIn();

        DocumentReference userBattlePassDocRef = db.collection("users").document(auth.getCurrentUser().getUid())
                .collection("user_data").document("battle_pass");

        getDocument(userBattlePassDocRef, (res, o)->{
            if(res == RESULT_SUCCESS){
                Player.getInstance().battlePass.putAll((Map<String, Object>) o);
            }
            listener.onDBRequestCompleted(res, o);
        });
    }

    public void fetchPlayerStatistics(OnDBRequestListener listener){
        selfCheckUserSignedIn();

        DocumentReference userStatisticDocRef = db.collection("users").document(auth.getCurrentUser().getUid())
                .collection("user_data").document("statistics");

        getDocument(userStatisticDocRef, (res, o)->{
            if(res == RESULT_SUCCESS){
                Player.getInstance().statistics.putAll((Map<String, Object>) o);
            }
            listener.onDBRequestCompleted(res, o);
        });
    }

    private void fetchPlayerPreferences(OnDBRequestListener allListener) {
        selfCheckUserSignedIn();

        DocumentReference userPreferencesDocRef = db.collection("users").document(auth.getCurrentUser().getUid())
                .collection("user_data").document("preferences");

        getDocument(userPreferencesDocRef, (res, o)->{
            if(res == RESULT_SUCCESS){
                Player.getInstance().preferences.putAll((Map<String, Object>) o);
            }
            allListener.onDBRequestCompleted(res, o);
        });
    }

    private void fetchPlayerPurchase(OnDBRequestListener allListener) {
        selfCheckUserSignedIn();

        DocumentReference userPurchaseDocRef = db.collection("users").document(auth.getCurrentUser().getUid())
                .collection("user_data").document("purchase");

        getDocument(userPurchaseDocRef, (res, o)->{
            if(res == RESULT_SUCCESS){
                Player.getInstance().purchase.putAll((Map<String, Object>) o);
            }
            allListener.onDBRequestCompleted(res, o);
        });
    }

    public void fetchPlayerAchievement(OnDBRequestListener listener){

    }

    public void fetchPlayerRank(OnDBRequestListener listener){

    }

    public void fetchPlayerActivity(OnDBRequestListener listener){

    }

    public void fetchPlayerRelationships(OnDBRequestListener listener){

    }

    private void selfCheckUserSignedIn(){
        if(!isSignedIn()){
            throw new RuntimeException("User is not signed in");
        }
    }

    public boolean isSignedIn(){
        return auth.getCurrentUser() != null;
    }

    public void updateDocRef(DocumentReference docRef, Map<String, Object> data, OnDBRequestListener listener){
        try{
            docRef.set(data, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        listener.onDBRequestCompleted(RESULT_SUCCESS, "Update successful");
                    })
                    .addOnFailureListener(e -> {
                        listener.onDBRequestCompleted(RESULT_FAILED, getUserMessage(e));
                        throw new RuntimeException(e.getMessage());
                    });
        }catch (Exception e){
            listener.onDBRequestCompleted(RESULT_FAILED, getUserMessage(e));
            throw new RuntimeException(e.getMessage());
        }
    }

    private CollectionReference getUserDataColRef(){
        if(auth == null){
            throw new RuntimeException("User is not signed in");
        }
        return db.collection("users").document(auth.getCurrentUser().getUid())
                .collection("user_data");
    }

    public void getDocument(DocumentReference docRef, OnDBRequestListener listener){
        try{
            docRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()){
                            listener.onDBRequestCompleted(RESULT_SUCCESS, documentSnapshot.getData());
                        }else{
                            listener.onDBRequestCompleted(RESULT_FAILED, "No such document");
                        }
                    })
                    .addOnFailureListener(e -> {
                        listener.onDBRequestCompleted(RESULT_FAILED, getUserMessage(e));
                    });
        }catch(Exception e){
            listener.onDBRequestCompleted(RESULT_FAILED, getUserMessage(e));
        }
    }

    public DocumentReference getUserDataDocRef(String name){
        if(auth == null){
            throw new RuntimeException("User is not signed in");
        }
        return db.collection("users").document(auth.getCurrentUser().getUid()).collection("user_data").document(name);
    }

    public void logOut(){
        if(!isSignedIn()){
            return;
        }

        //if user is signed in as anonymous, delete the user
        if(isSignedInAsAnonymous()){
            auth.getCurrentUser().delete();
        }

        auth.signOut();
    }

    public void addAuthStateListener(FirebaseAuth.AuthStateListener authStateListener){
        auth.addAuthStateListener(authStateListener);
    }

    public String getUserMessage(Exception e){
        final String exceptionMessage = e.getMessage();
        //throw  new RuntimeException(exceptionMessage);
        if(exceptionMessage == null){
            return "Unknown error";
        }else if(exceptionMessage.contains(FB_NETWORK_EXCEPTION)) {
            return "No internet connection";
        }else if(exceptionMessage.contains(FB_USER_COLLISION_EXCEPTION)) {
            return "User already exists";
        }else if(exceptionMessage.contains(FB_INVALID_EMAIL_EXCEPTION)) {
            return "Invalid email";
        } else if (exceptionMessage.contains(FB_USER_NOT_FOUND_EXCEPTION)) {
            return "User not found";
        }else if(exceptionMessage.contains(FB_WRONG_PASSWORD_EXCEPTION)) {
            return "Wrong password";
        }else if(exceptionMessage.contains(FB_EMAIL_ALREADY_IN_USE_EXCEPTION)) {
            return "Email already in use";
        }else if(exceptionMessage.contains(FB_WEAK_PASSWORD_EXCEPTION)) {
            return "Weak password";
        }else if(exceptionMessage.contains(FB_TOO_MANY_REQUESTS_EXCEPTION)) {
            return "Too many requests";
        } else if(exceptionMessage.contains(FB_INVALID_CREDENTIAL_EXCEPTION)) {
            return "Email or password is incorrect";
        } else if(exceptionMessage.contains(FB_INVALID_LOGIN_CREDENTIAL_EXCEPTION)) {
            return "Email or password is incorrect";
        }
        else{
            return exceptionMessage;
        }
    }

    public String getUserDisplayName(){
        if(auth.getCurrentUser() == null){
            throw new RuntimeException("User is not signed in");
        }
        //if user is signed in as anonymous, return "Guest"
        if(isSignedInAsAnonymous()){
            return "Guest";
        }

        if(auth.getCurrentUser().getDisplayName() == null){
            return "";
        }

        return auth.getCurrentUser().getDisplayName();
    }

    public String getUserEmail(){
        if(auth.getCurrentUser() == null){
            throw new RuntimeException("User is not signed in");
        }

        if(auth.getCurrentUser().getEmail() == null){
            return "";
        }

        return auth.getCurrentUser().getEmail();
    }

    public String getUserPhoneNumber(){
        if(auth.getCurrentUser() == null){
            throw new RuntimeException("User is not signed in");
        }

        if(auth.getCurrentUser().getPhoneNumber() == null){
            return "";
        }

        return auth.getCurrentUser().getPhoneNumber();
    }

    public Object getUserPhotoUrl(){
        if(auth.getCurrentUser() == null){
            throw new RuntimeException("User is not signed in");
        }

        if(auth.getCurrentUser().getPhotoUrl() == null){
            return "";
        }

        return auth.getCurrentUser().getPhotoUrl();
    }

    public String getUserUID(){
        if(auth.getCurrentUser() == null){
            return "";
        }

        if(auth.getCurrentUser().getUid() == null){
            return "";
        }

        return auth.getCurrentUser().getUid();
    }

    public void removeAuthStateListener(FirebaseAuth.AuthStateListener authStateListener){
        auth.removeAuthStateListener(authStateListener);
    }

    public boolean isSignedInAsAnonymous() {
        return auth.getCurrentUser().isAnonymous();
    }

    public static final String FB_NETWORK_EXCEPTION = "FirebaseNetworkException";
    public static final String FB_USER_COLLISION_EXCEPTION = "FirebaseAuthUserCollisionException";
    public static final String FB_INVALID_EMAIL_EXCEPTION = "FirebaseInvalidEmailException";
    public static final String FB_USER_NOT_FOUND_EXCEPTION = "FirebaseUserNotFoundException";
    public static final String FB_WRONG_PASSWORD_EXCEPTION = "FirebaseWrongPasswordException";
    public static final String FB_EMAIL_ALREADY_IN_USE_EXCEPTION = "FirebaseEmailAlreadyInUseException";
    public static final String FB_WEAK_PASSWORD_EXCEPTION = "FirebaseWeakPasswordException";
    public static final String FB_TOO_MANY_REQUESTS_EXCEPTION = "FirebaseTooManyRequestsException";
    public static final String FB_INVALID_CREDENTIAL_EXCEPTION = "FirebaseInvalidCredentialsException";
    public static final String FB_INVALID_LOGIN_CREDENTIAL_EXCEPTION = "INVALID_LOGIN_CREDENTIALS";

}
