import com.apwide.jenkins.golive.Environment
import com.apwide.jenkins.util.Parameters
import com.apwide.jenkins.util.ScriptWrapper

import static com.apwide.jenkins.util.Utilities.executeStep

def call(Map config = null) {
    executeStep(this, config) { ScriptWrapper script, Parameters parameters ->

        def environmentClient = new Environment(script, parameters)
        def environmentId = parameters.environmentId || environmentClient.get(parameters.application, parameters.category).id

        return environmentClient
                .checkAndUpdateStatus(environmentId, parameters.unavailableStatus, parameters.availableStatus,
                parameters.dontTouchStatus, parameters.params.checkStatus)
    }
}
