package kapagod.na.k8ilu.utils.tabs

import kapagod.na.k8ilu.data.model.K8DataModel

interface K8TabInfoListener {
    fun onItemClick (data : K8DataModel)
}