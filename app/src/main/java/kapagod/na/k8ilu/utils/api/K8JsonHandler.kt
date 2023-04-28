package kapagod.na.k8ilu.utils.api

import kapagod.na.k8ilu.data.list.K8DataList
import retrofit2.http.GET

interface K8JsonHandler {
    @GET("project.json?auth=nvOo8N6bit2yEJCw981nfvJETnp4CUDdridkNie7")
    fun readyJson(): retrofit2.Call<List<K8DataList>>
}