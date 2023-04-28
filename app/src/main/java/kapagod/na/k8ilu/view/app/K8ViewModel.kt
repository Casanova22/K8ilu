package kapagod.na.k8ilu.view.app

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kapagod.na.k8ilu.data.list.K8DataList
import kapagod.na.k8ilu.utils.firebase.K8FirebaseAppHandler
import retrofit2.Call
import retrofit2.Response
import java.util.ArrayList

class K8ViewModel : ViewModel() {

    private val manifest = K8FirebaseAppHandler()

    var chismisInfo: ArrayList<K8DataList> = ArrayList<K8DataList>()
    private val kuhaInfo = MutableLiveData<List<K8DataList>?>()
    val dataListModel: MutableLiveData<List<K8DataList>?> get() = kuhaInfo

    fun initJson() {
        manifest.getFirebaseJson().enqueue(object : retrofit2.Callback<List<K8DataList>> {
            override fun onResponse(
                call: Call<List<K8DataList>>,
                responde: Response<List<K8DataList>>
            ) {
                try {
                    val sheeEsh: List<K8DataList> = responde.body()!!
                    for (ind in sheeEsh.indices) {
                        chismisInfo.add(sheeEsh[ind])
                        Log.e(ContentValues.TAG, sheeEsh.toString())
                    }
                    kuhaInfo.value = chismisInfo
                    Log.e(ContentValues.TAG, chismisInfo.toString())

                } catch (e: Exception) {
                    kuhaInfo.value = chismisInfo
                }
            }
            override fun onFailure(call: Call<List<K8DataList>>, t: Throwable) {
                kuhaInfo.value = chismisInfo
            }
        } )
    }
}