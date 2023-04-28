package kapagod.na.k8ilu

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kapagod.na.k8ilu.databinding.FragmentFirstBinding
import kapagod.na.k8ilu.utils.K8NetworkManager
import kapagod.na.k8ilu.view.app.K8ViewModel

class K8FragmentFirst : Fragment(), View.OnClickListener {

    private var _firstBinding : FragmentFirstBinding? = null
    private val binding get() = _firstBinding!!

    private val args = Bundle()
    private lateinit var viewModel: K8ViewModel
    private var checkInternetConnection = K8NetworkManager()
    private var checknet = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        _firstBinding = FragmentFirstBinding.inflate(inflater,container,false)

        viewModel = ViewModelProvider(this)[K8ViewModel::class.java]
        connectionCheck()
        return binding.root
    }

    private fun connectionCheck() {
        checknet = checkInternetConnection.connectionError(requireActivity())
        if (checknet) {
            viewModel.initJson()
            buttonCode()
        } else {
            Toast.makeText(context, "PLEASE CHECK YOUR INTERNET CONNECTION!!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun onClickMain(){
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        /*    binding.button3.setOnClickListener {
                val intent = Intent(context, WebGameActivity::class.java)
                startActivity(intent)
            }*/
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun buttonCode() {
        onClickMain()
        viewModel.dataListModel.observe(viewLifecycleOwner) {
            it.let {
                if (it != null) {
                    for (i in it.indices) {
                        val packageName = it[i].K8PACKAPP
                        val webView = it[i].K8URL
                        val jumpCode = it[i].K8CODESTATUS
                        if (packageName == context?.packageName) {
                            Log.e(ContentValues.TAG, packageName.toString())
                            when (jumpCode) {
                                true -> {
                                    binding.b1.text = it[i].K8BUTTON1
                                    binding.b2.text = it[i].K8BUTTON2
                                    args.putBoolean("code", true)
                                    args.putBoolean("title",true)
                                    args.putString("urlview", webView)
                                }
                                else -> {
                                    binding.b1.text = getString(R.string.b1)
                                    binding.b2.text = getString(R.string.b2)
                                    args.putBoolean("code", false)
                                    args.putBoolean("title",false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v){
            binding.btn1 -> findNavController().navigate(R.id.action_FirstFragment_to_InfoFragment, args)
            binding.btn2 -> findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, args)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _firstBinding = null
    }
}