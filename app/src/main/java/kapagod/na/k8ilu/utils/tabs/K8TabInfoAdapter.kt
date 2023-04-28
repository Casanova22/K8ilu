package kapagod.na.k8ilu.utils.tabs

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kapagod.na.k8ilu.K8FragmentSecond
import kapagod.na.k8ilu.data.model.K8DataModel
import kapagod.na.k8ilu.databinding.InfoViewBinding

class K8TabInfoAdapter(val listener: K8FragmentSecond) : RecyclerView.Adapter<K8TabInfoAdapter.AdapterHolder>() {

    private var privacyList = emptyList<K8DataModel>()
    class AdapterHolder (val adpts : InfoViewBinding) : RecyclerView.ViewHolder(adpts.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder = AdapterHolder(
        InfoViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
        with (holder){
            with(privacyList[position]){
                adpts.infoTitle.text  = this.contentTitle
                adpts.infoDesc.text = this.contentDesc
                adpts.infoDesc.ellipsize = TextUtils.TruncateAt.MARQUEE
                adpts.infoDesc.isSelected = true

                adpts.onclickCardview.setOnClickListener {
                    listener.onItemClick(this)
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return privacyList.size
    }

    fun setAdapter (setAdapt : List<K8DataModel>){
        this.privacyList = setAdapt
    }
}