package com.example.detect_voice_app.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction.TRANSIT_NONE
import com.example.detect_voice_app.BR

abstract class BaseFragment<ViewBinding : ViewDataBinding, ViewModel : BaseViewModel> : Fragment() {

    protected lateinit var viewBinding: ViewBinding

    protected abstract val viewModel: ViewModel

    @get:LayoutRes
    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (::viewBinding.isInitialized.not()) {
            viewBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
            viewBinding.apply {
                lifecycleOwner = this@BaseFragment
                setVariable(BR.viewModel, viewModel)
                root.isClickable = true
                executePendingBindings()
            }
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * fragment transaction
     */

    fun findFragment(TAG: String): Fragment? {
        return activity?.supportFragmentManager?.findFragmentByTag(TAG)
    }

    fun findChildFragment(parentFragment: Fragment = this, TAG: String): Fragment? {
        return parentFragment.childFragmentManager.findFragmentByTag(TAG)
    }

    @SuppressLint("ResourceType")
    fun addFragment(
        fragment: BaseFragment<*, *>,
        containerViewId: Int,
        addToBackStack: Boolean = true,
        transit: Int = -1,
        @IdRes animationEnter: Int = 0,
        @IdRes animationExit: Int = 0,
        @IdRes animationPopEnter: Int = 0,
        @IdRes animationPopExit: Int = 0
    ) {
        activity?.supportFragmentManager?.beginTransaction()?.setCustomAnimations(
            animationEnter,
            animationExit,
            animationPopEnter,
            animationPopExit
        )
            ?.apply {
                if (addToBackStack) addToBackStack(this@BaseFragment::class.simpleName)
                if (transit != TRANSIT_NONE) setTransition(TRANSIT_NONE)
            }
            ?.add(containerViewId, fragment, fragment::class.simpleName)
            ?.commit()
    }

    fun replaceFragment(
        fragment: BaseFragment<*, *>,
        containerViewId: Int,
        addToBackStack: Boolean = true,
        transit: Int = -1
    ) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.apply {
                if (addToBackStack) addToBackStack(this@BaseFragment::class.simpleName)
                if (transit != TRANSIT_NONE) setTransition(TRANSIT_NONE)
            }
            ?.replace(containerViewId, fragment, fragment::class.simpleName)
            ?.commit()
    }

    fun replaceChildFragment(
        parentFragment: Fragment = this, containerViewId: Int,
        fragment: BaseFragment<*, *>, addToBackStack: Boolean = true,
        transit: Int = TRANSIT_NONE
    ) {
        parentFragment.childFragmentManager.beginTransaction()
            .apply {
                if (addToBackStack) addToBackStack(this@BaseFragment::class.simpleName)
                if (transit != TRANSIT_NONE) setTransition(TRANSIT_NONE)
            }
            .replace(
                containerViewId, fragment, fragment::class.simpleName
            )
            .commit()
    }

    fun replaceChildFragmentAllowLost(
        parentFragment: Fragment = this, containerViewId: Int,
        fragment: BaseFragment<*, *>, addToBackStack: Boolean = true,
        transit: Int = TRANSIT_NONE
    ) {
        parentFragment.childFragmentManager.beginTransaction()
            .apply {
                if (addToBackStack) addToBackStack(this@BaseFragment::class.simpleName)
                if (transit != TRANSIT_NONE) setTransition(TRANSIT_NONE)
            }
            .replace(
                containerViewId, fragment, fragment::class.simpleName
            )
            .commitAllowingStateLoss()
    }

    fun addChildFragment(
        parentFragment: Fragment = this, containerViewId: Int,
        fragment: Fragment, addToBackStack: Boolean = true, transit: Int = TRANSIT_NONE
    ) {
        parentFragment.childFragmentManager.beginTransaction()
            .apply {
                if (addToBackStack) addToBackStack(this@BaseFragment::class.simpleName)
                if (transit != TRANSIT_NONE) setTransition(TRANSIT_NONE)
            }
            .add(containerViewId, fragment, fragment::class.simpleName)
            .commit()
    }


    fun popChildFragment(parentFragment: Fragment = this) {
        parentFragment.childFragmentManager.popBackStack()
    }

    fun showDialogFragment(
        dialogFragment: DialogFragment,
        addToBackStack: Boolean = false
    ) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        if (addToBackStack) transaction?.addToBackStack(dialogFragment::class.simpleName)
        if (transaction != null) {
            dialogFragment.show(transaction, dialogFragment::class.simpleName)
        }
    }

    fun clearBackStack() {
        activity?.supportFragmentManager?.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    fun backToPreviousScreen() {
        activity?.supportFragmentManager?.popBackStack()
    }

    fun <T> backToFragmentByTag(
        clazz: Class<T>,
        failCallback: (() -> Unit)? = null,
        callback: ((T) -> Unit)? = null
    ) {
        activity?.supportFragmentManager?.popBackStack(
            clazz.simpleName,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        val targetFragment =
            activity?.supportFragmentManager?.findFragmentByTag(clazz.simpleName)
                    as? T
        if (targetFragment != null) {
            callback?.invoke(targetFragment)
        } else
            failCallback?.invoke()
    }

    fun <T> backToFragment(
        clazz: Class<T>? = null,
        failCallback: (() -> Unit)? = null,
        callback: ((T) -> Unit)? = null,
    ) {
        activity?.supportFragmentManager?.popBackStack(
            clazz?.simpleName,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        val targetFragment =
            activity?.supportFragmentManager?.fragments?.find { it::class.java == clazz } as? T
        if (targetFragment != null) {
            callback?.invoke(targetFragment)
        } else
            failCallback?.invoke()
    }

    fun <T : BaseFragment<*, *>> backToFragmentOrNavigate(
        clazz: Class<T>,
        @IdRes frameId: Int? = null,
        bundle: Bundle? = null,
        isReplace: Boolean = false,
        addToBackStack: Boolean = true,
        backCallback: ((T) -> Unit)? = null,
        navigateCallback: (() -> Unit)? = null
    ) {
        val targetFragment =
            activity?.supportFragmentManager?.fragments?.find { it::class.java == clazz } as? T
        if (targetFragment != null) {
            backToFragment(clazz, null, backCallback)
        } else {
            if (frameId == null) return

            val newFragment = clazz.newInstance()
                .apply { arguments = bundle }
            navigateCallback?.invoke()
            if (isReplace) {
                replaceFragment(
                    newFragment,
                    frameId,
                    addToBackStack
                )
            } else {
                addFragment(
                    newFragment,
                    frameId,
                    addToBackStack
                )
            }
        }
    }

    fun fragmentResult(requestKey: String, bundle: Bundle? = null) {
        requireActivity().supportFragmentManager
            .setFragmentResult(requestKey, bundle ?: Bundle())
    }

    fun fragmentResultListener(requestKey: String, handleOnBackPressed: (Bundle) -> Unit) {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            requestKey,
            viewLifecycleOwner
        ) { _, bundle ->
            handleOnBackPressed.invoke(bundle)
        }
    }
}

