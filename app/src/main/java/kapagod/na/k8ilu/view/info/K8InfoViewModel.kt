package kapagod.na.k8ilu.view.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kapagod.na.k8ilu.data.model.K8DataModel
import kapagod.na.k8ilu.utils.tabs.K8TabInfoContent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class K8InfoViewModel : ViewModel() {

    private var privacyList = ArrayList<K8DataModel>()
    private var privacyInfo = MutableLiveData<List<K8DataModel>>()
    val trmNf : LiveData<List<K8DataModel>>
        get() = privacyInfo
    private var privacyError = CoroutineExceptionHandler { _, _ ->
        privacyInfo.postValue(listOfNotNull())
    }

    fun termiFun(): MutableLiveData<List<K8DataModel>> {
        viewModelScope.launch(privacyError + Dispatchers.IO) {
            for (n in K8TabInfoContent.k8InfoTitle.indices) {
                privacyList.add(K8DataModel(K8TabInfoContent.k8InfoTitle[n], K8TabInfoContent.k8InfoDesc[n]))
            }
            privacyInfo.postValue(privacyList)
        }
        return privacyInfo
    }
}