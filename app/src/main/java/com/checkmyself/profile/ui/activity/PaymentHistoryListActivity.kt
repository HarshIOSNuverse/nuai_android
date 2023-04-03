package com.checkmyself.profile.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.PaymentHistoryListActivityBinding
import com.checkmyself.network.ResponseStatus
import com.checkmyself.network.Status
import com.checkmyself.onboarding.ui.adapters.SpinnerAdapter
import com.checkmyself.profile.model.MyPlan
import com.checkmyself.profile.ui.adapters.PaymentHistoryListAdapter
import com.checkmyself.profile.viewmodel.ProfileViewModel
import com.checkmyself.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class PaymentHistoryListActivity : BaseActivity() {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, PaymentHistoryListActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: PaymentHistoryListActivityBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private var selectedYear: String? = ""
    private val paymentList: ArrayList<MyPlan> = arrayListOf()
    private val yearList: ArrayList<String> = arrayListOf()
    private var offset = 0
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.payment_history_list_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.payment_history))
        initClickListener()
        initObserver()
        initAdapter()
        init()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init() {
        selectedYear = DateFormatter.getDateToString(
            DateFormatter.yyyy,
            Calendar.getInstance().time
        )
        var year = selectedYear!!.toInt()
        val count = year - 5
        while (year > count) {
            yearList.add(year.toString())
            year--
        }
        initGenderSpinnerAdapter()
    }

    private fun reloadPaymentList() {
        offset = 0
        loading = false
        getPaymentList()
    }

    private fun getPaymentList() {
        if (CommonUtils.isNetworkAvailable(this)) {
            profileViewModel.getPaymentList(selectedYear!!, offset)
        } else {
            if (!binding.swipeRefreshLayout.isRefreshing && !loading) {
                binding.viewFlipper.displayedChild = 3
            } else {
                CommonUtils.showToast(this, getString(R.string.no_internet_connection))
            }
            binding.llLoadMore.visibility = View.GONE
            binding.swipeRefreshLayout.isRefreshing = false
            loading = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        lifecycleScope.launch {
            profileViewModel.paymentListState.collect {
                when (it.status) {
                    Status.LOADING -> {
//                        showHideProgress(it.data == null)
                        showHideProgress(false)
                        if (offset == 0) {
                            if (!binding.swipeRefreshLayout.isRefreshing) {
                                binding.viewFlipper.displayedChild = 0
                            }
                        } else if (loading) {
                            binding.llLoadMore.visibility = View.VISIBLE
                        }
                    }
                    Status.SUCCESS -> {
                        showHideProgress(false)
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.llLoadMore.visibility = View.GONE
                        if (it.data != null && (it.code == ResponseStatus.STATUS_CODE_SUCCESS)) {
                            if (offset == 0)
                                paymentList.clear()
                            if (!it.data.paymentList.isNullOrEmpty()) {
                                paymentList.addAll(it.data.paymentList!!)
                            }
                            if (loading) {
                                binding.adapter!!.notifyItemInserted(paymentList.size)
                            } else {
                                binding.adapter!!.notifyDataSetChanged()
                            }

                            offset = it.data.nextOffset
                        }
                        loading = false
                        setNoResult()
                    }
                    Status.ERROR -> {
                        binding.llLoadMore.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                        loading = false
                        showHideProgress(false)
                        CommonUtils.showToast(this@PaymentHistoryListActivity, it.message)
                    }
                }
            }
        }
    }

    private fun setNoResult() {
        if (paymentList.isNotEmpty()) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 2
            binding.noDataFound.txtMessage.text = getString(R.string.no_payment_data_found)
        }
    }

    private fun initClickListener() {
        initNoInternet(binding.noInternetConnection) {
            getPaymentList()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            reloadPaymentList()
        }
    }

    private fun initAdapter() {
        binding.adapter = PaymentHistoryListAdapter(paymentList).apply {
            paymentHistoryListener =
                object : PaymentHistoryListAdapter.PaymentHistoryListener {
                    override fun onViewDetailClick(payment: MyPlan?) {
                        if (payment != null) {
                            PaymentDetailActivity.startActivity(
                                this@PaymentHistoryListActivity, payment.uuid
                            )
                        }
                    }
                }
        }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val linearLayoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && paymentList.isNotEmpty() && offset > 0) {
                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
                    if (!loading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = true
                        getPaymentList()
                    }
                }
            }
        })
    }

    private fun initGenderSpinnerAdapter() {
        binding.genderSpinner.apply {
            adapter =
                SpinnerAdapter(this@PaymentHistoryListActivity, R.layout.spinner_item, yearList, -1)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    selectedYear = yearList[position]
                    reloadPaymentList()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }
}