package kapagod.na.k8ilu.utils.firebase

import kapagod.na.k8ilu.data.list.K8DataList
import kapagod.na.k8ilu.utils.api.K8JsonHandler
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class K8FirebaseAppHandler {
    private val fireBaseProject = "https://android-projects-82fc0-default-rtdb.firebaseio.com/"

    private val getApiJson : K8JsonHandler = Retrofit.Builder().baseUrl(fireBaseProject).addConverterFactory(
        GsonConverterFactory.create()
    ).build().create(K8JsonHandler::class.java)

    fun getFirebaseJson(): Call<List<K8DataList>> {
        return getApiJson.readyJson()
    }
}