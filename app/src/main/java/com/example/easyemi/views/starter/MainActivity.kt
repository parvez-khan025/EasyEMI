package com.example.easyemi.views.starter

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.easyemi.R

class MainActivity : AppCompatActivity() {

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Register network callback
        registerNetworkCallback()

        // Initial check after NavHostFragment is ready
        window.decorView.post { checkInitialInternet() }
    }

    private fun checkInitialInternet() {
        val navController = findNavController(R.id.fragmentContainerView)
        if (!isNetworkAvailable() && navController.currentDestination?.id != R.id.nointernetFragment) {
            navController.navigate(R.id.nointernetFragment)
        }
    }

    private fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                runOnUiThread {
                    val navController = findNavController(R.id.fragmentContainerView)
                    if (navController.currentDestination?.id != R.id.nointernetFragment) {
                        navController.navigate(R.id.nointernetFragment)
                    }
                }
            }

            override fun onAvailable(network: Network) {
                runOnUiThread {
                    val navController = findNavController(R.id.fragmentContainerView)
                    if (navController.currentDestination?.id == R.id.nointernetFragment) {
                        navController.navigateUp() // Go back to previous page
                    }
                }
            }
        }

        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}