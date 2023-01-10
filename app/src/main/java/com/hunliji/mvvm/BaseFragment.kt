package com.hunliji.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.hunliji.mvvm.core.ILoading
import com.hunliji.mvvm.core.IView
import com.hunliji.mvvm.core.IViewEvent
import com.hunliji.hlj_refresh.RefreshPresenter
import com.hunliji.mvvm.loading.DefaultEmptyCallback
import com.hunliji.mvvm.loading.DefaultErrorCallback
import com.hunliji.mvvm.loading.DefaultLoadingCallback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import java.lang.reflect.ParameterizedType

/**
 * BaseFragment
 *
 * @author wm
 * @date 19-8-12
 */
abstract class BaseFragment<B : ViewDataBinding, VM : BaseVm> : Fragment(),
    IView,
    IViewEvent,
    RefreshPresenter,
    ILoading {
    var rootView: View? = null
    private var isLoaded: Boolean = false
    private val paramsMap = mutableMapOf<String, @JvmSuppressWildcards Any>()
    lateinit var binding: B
    var baseVm: BaseVm? = null
    var loadService: LoadService<*>? = null
    open lateinit var viewModel: VM

    inline fun <reified T : ViewModel> createVM(): T = ViewModelProvider(
        this.viewModelStore, VmFactory(requireActivity().application, getMap())
    ).get(T::class.java)

    inline fun <reified T : ViewModel> getSharedVM(): T = ViewModelProvider(
        requireActivity().viewModelStore, VmFactory(requireActivity().application, getMap())
    ).get(T::class.java)

    val linearLayoutManager by lazy {
        LinearLayoutManager(requireContext())
    }

    @Suppress("UNCHECKED_CAST")
    private fun createVM() {
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val tp = type.actualTypeArguments[1]
            val tClass = tp as? Class<VM> ?: BaseVm::class.java
            viewModel = ViewModelProvider(
                this.viewModelStore,
                VmFactory(requireActivity().application, getMap())
            ).get(tClass) as VM
        }
    }

    fun getMap(): MutableMap<String, @JvmSuppressWildcards Any> {
        return paramsMap
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (rootView?.parent as? ViewGroup)?.removeView(rootView)
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), null, false)
        binding.lifecycleOwner = this
        rootView = binding.root
        if (isSupportLoading()) {
            val build = LoadSir.Builder()
                .addCallback(DefaultLoadingCallback(showWhenLoading()))
                .addCallback(DefaultEmptyCallback())
                .addCallback(DefaultErrorCallback())
                .setDefaultCallback(DefaultLoadingCallback::class.java)
                .build()
            loadService = build.register(rootView) {
                if (isSupportReload()) {
                    onRequestReload()
                }
            }
            return loadService?.loadLayout
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setVariable(BR.v, this)
        getData(arguments)
        initVm()
        initView()
        fitScreen()
        baseVm?.stateModel?.observe(viewLifecycleOwner, Observer {
            val value = baseVm?.stateModel?.value
            when (true) {
                value == BaseVm.NORMAL -> hideLoading()
                value == BaseVm.PROGRESS -> showLoading()
                value == BaseVm.EMPTY -> showEmpty()
                value == BaseVm.ERROR -> showError()
            }
        })
    }

    open fun initVm() {
        createVM()
        binding.setVariable(BR.vm, viewModel)
        baseVm = viewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        isLoaded = false
    }

    override fun onResume() {
        super.onResume()
        if (!isLoaded && !isHidden) {
            onLazyLoad()
            isLoaded = true
        }
    }

    fun setValue(pair: Pair<String, @JvmSuppressWildcards Any>) {
        paramsMap[pair.first] = pair.second
    }

    open fun getData(bundle: Bundle?) {

    }

    open fun fitScreen() {

    }

    open fun onLazyLoad() {

    }

    override fun onClick(v: View?) {

    }

    override fun loadData(isNormal: Boolean, isRefresh: Boolean) {

    }

    override fun showLoading() {
        if (isSupportLoading()) {
            loadService?.showCallback(DefaultLoadingCallback::class.java)
        }
    }

    override fun hideLoading() {
        if (isSupportLoading()) {
            loadService?.showSuccess()
        }
    }

    override fun showEmpty() {
        if (isSupportLoading()) {
            loadService?.showCallback(DefaultEmptyCallback::class.java)
        }
    }

    override fun showError() {
        if (isSupportLoading()) {
            loadService?.showCallback(DefaultErrorCallback::class.java)
        }
    }

    fun runOnUiThread(runnable: () -> Unit) {
        activity?.runOnUiThread(runnable)
    }
}
