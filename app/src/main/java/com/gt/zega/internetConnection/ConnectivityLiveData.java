package com.gt.zega.internetConnection;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.annotations.NotNull;

public class ConnectivityLiveData extends LiveData<Boolean> {

    private static final String TAG = "ConnectivityLiveData";
    private final ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback connectivityManagerCallback;
    private final NetworkRequest.Builder networkRequestBuilder;
    @NotNull
    private final Context context;

    protected void onActive() {
        super.onActive();
        this.updateConnection();
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                this.connectivityManager.registerDefaultNetworkCallback(this.getConnectivityMarshmallowManagerCallback());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            try {
                this.marshmallowNetworkAvailableRequest();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            try {
                this.lollipopNetworkAvailableRequest();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    protected void onInactive() {
        super.onInactive();
        Log.e(TAG, "onInactive: I am inActive ");
        if (Build.VERSION.SDK_INT >= 21) {
            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback);
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void lollipopNetworkAvailableRequest() throws IllegalAccessException {
        this.connectivityManager.registerNetworkCallback(this.networkRequestBuilder.build(), this.getConnectivityLollipopManagerCallback());
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void marshmallowNetworkAvailableRequest() throws IllegalAccessException {
        this.connectivityManager.registerNetworkCallback(this.networkRequestBuilder.build(), this.getConnectivityMarshmallowManagerCallback());
    }

    private ConnectivityManager.NetworkCallback getConnectivityLollipopManagerCallback() throws IllegalAccessException {
        if (Build.VERSION.SDK_INT >= 21) {
            this.connectivityManagerCallback = new ConnectivityManager.NetworkCallback() {
                public void onAvailable(@NotNull Network network) {
                    postValue(true);
                }

                public void onLost(@NotNull Network network) {
                    postValue(false);
                }
            };

            return this.connectivityManagerCallback;
        } else {
            throw new IllegalAccessException();
        }
    }

    private ConnectivityManager.NetworkCallback getConnectivityMarshmallowManagerCallback() throws IllegalAccessException {
        if (Build.VERSION.SDK_INT >= 23) {
            this.connectivityManagerCallback = new ConnectivityManager.NetworkCallback() {
                public void onCapabilitiesChanged(@NotNull Network network, @NotNull NetworkCapabilities networkCapabilities) {
                    if (connectivityManager != null) {
                        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                        if (capabilities != null) {
                            if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                                postValue(true);
                            }
                        }
                    }
                }

                public void onLost(@NotNull Network network) {
                    postValue(false);
                }
            };

            return this.connectivityManagerCallback;
        } else {
            throw new IllegalAccessException();
        }
    }

    private void updateConnection() {
        boolean isConnected;
        NetworkInfo activeNetwork = this.connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            isConnected = activeNetwork.isConnected();
            Toast.makeText(context.getApplicationContext(), "onInactive: I am Active ", Toast.LENGTH_SHORT).show();
        } else {
            isConnected = false;
            Toast.makeText(context.getApplicationContext(), "onInactive: I am inActive ", Toast.LENGTH_SHORT).show();
        }
        this.postValue(isConnected);
    }

    @NotNull
    public final Context getContext() {
        return this.context;
    }

    public ConnectivityLiveData(@NotNull Context context) {
        this.context = context;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            throw new NullPointerException("null cannot be cast to non-null type android.net.ConnectivityManager");
        } else {
            this.connectivityManager = cm;
            this.networkRequestBuilder = (new NetworkRequest.Builder()).addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).addTransportType(NetworkCapabilities.TRANSPORT_VPN);
        }
    }

}
