package com.nuai.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.nuai.databinding.LayoutNoInternetBinding
import com.nuai.utils.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    companion object {
        val TAG: String = BaseFragment::class.java.simpleName
    }

//    @Inject
//    lateinit var viewModelFactory: ViewModelProviderFactory

//    lateinit var stateChangeListener: DataStateChangeListener

    //    lateinit var viewModel: LoginViewModel
    private var cartCountText: TextView? = null
    private var notificationCountText: TextView? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setupActionBarWithNavController(R.id.accountFragment, activity as AppCompatActivity)

//        viewModel = activity?.run {
//            ViewModelProvider(this, providerFactory).get(LoginViewModel::class.java)
//        } ?: throw Exception("Invalid Activity")

        // Cancels jobs when switching between fragments in the same graph
        // ex: from AccountFragment to UpdateAccountFragment
        // NOTE: Must call before "subscribeObservers" b/c that will create new jobs for the next fragment
//        cancelActiveJobs()
    }

    protected fun initNoInternet(
        noInternetLayout: LayoutNoInternetBinding, onClickListener: View.OnClickListener
    ) {
        noInternetLayout.onClickListener = onClickListener
    }

    protected fun hideKeyboard(v: EditText) {
        val imm: InputMethodManager =
            v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    protected fun showHideProgress(show: Boolean) {
        if (show) {
            CustomProgressDialog.showProgressDialog(requireActivity())
        } else {
            CustomProgressDialog.dismissProgressDialog()
        }
    }
}
















