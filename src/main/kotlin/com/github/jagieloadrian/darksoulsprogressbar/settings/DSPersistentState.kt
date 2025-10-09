package com.github.jagieloadrian.darksoulsprogressbar.settings

import com.github.jagieloadrian.darksoulsprogressbar.model.Icons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "DS Progress Bar Setting",
    storages = [Storage("DSPluginSettings.xml")]
)
@Service(Service.Level.APP)
class DSPersistentState : PersistentStateComponent<DSPersistentState> {

    var iconPaths: MutableSet<String> = Icons.entries.map { it.path }.toMutableSet()
    var animateOnFailedBuild:Boolean = true

    override fun getState(): DSPersistentState {
      return this
    }

    override fun loadState(state: DSPersistentState) {
        XmlSerializerUtil.copyBean(state, this)
    }
    companion object {
        fun getInstance(): DSPersistentState {
            return ApplicationManager.getApplication().getService(DSPersistentState::class.java)
        }
    }

}