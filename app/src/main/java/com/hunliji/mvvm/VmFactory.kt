package com.hunliji.mvvm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.InvocationTargetException

/**
 * VmFactory
 *
 * @author wm
 * @date 20-2-11
 */
class VmFactory(
    private val app: Application,
    private val map: MutableMap<String, @JvmSuppressWildcards Any>
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (BaseVm::class.java.isAssignableFrom(modelClass)) {
            try {
                return modelClass.getConstructor(Application::class.java, MutableMap::class.java)
                    .newInstance(app, map)
            } catch (e: NoSuchMethodException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
        }
        return super.create(modelClass)
    }
}
