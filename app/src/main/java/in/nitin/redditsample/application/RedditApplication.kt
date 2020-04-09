package `in`.nitin.redditsample.application


import `in`.nitin.redditsample.di.ApplicationComponent
import `in`.nitin.redditsample.di.DaggerApplicationComponent
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


class RedditApplication : Application() {
    private lateinit var mApplicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        if (instance == null) {
            instance = this
        }
        mApplicationComponent = DaggerApplicationComponent.builder().build()
    }

    companion object {
        var instance: RedditApplication? = null

        fun getNetworkStatus(): Boolean {
            return instance!!.isConnectionOn()
        }

        fun getComponent(): ApplicationComponent? {
            return instance!!.mApplicationComponent
        }
    }

    @Suppress("DEPRECATION")
    private fun isConnectionOn(): Boolean {
        var result = false
        val cm =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }
}