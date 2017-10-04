/**
 *  Turn on stuff
 *
 *  Copyright 2016 David Rosner
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Turn on stuff",
    namespace: "Davidmrosner",
    author: "David Rosner",
    description: "run a routine from a single virtual switch",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    page(name: "configure")
    
}

def configure() {
    dynamicPage(name: "configure", title: "Configure Switch and Routine", install: true, uninstall: true) {
       section("Select your switch") {
                    input "theswitch", "capability.switch",required: true
            }

            def actions = location.helloHome?.getPhrases()*.label
            if (actions) {
                actions.sort()
                section("Routine to execute on ON") {
                log.trace actions
                input "onAction", "enum", title: "Action to execute when turned on", options: actions, required: true
                }
                
                section("Routine to execute on OFF") {
                log.trace actions
                input "offAction", "enum", title: "Action to execute when turned off", options: actions, required: true
                }
               
            }
            section("Select mode") {
            mode(name: "modeMultiple",
                 title: "Run in these modes only",
                 required: false)
            }
            
            section("Minutes to wait before shutting off switch") {
                log.trace actions
                input "offWait", "number"
                }
    }    
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    subscribe(theswitch, "switch", handler)
    subscribe(location, "routineExecuted", routineChanged)
    log.debug "selected on action $onAction"
}

def handler(evt) {
    if (evt.value == "on") {
            log.debug "switch turned on, will execute action ${settings.onAction}"
            location.helloHome?.execute(settings.onAction)                 
            runIn( offWait * 60, offHandler)
    }
    
    if (evt.value == "off") {
            log.debug "switch turned off will execute action ${settings.offAction}"
            location.helloHome?.execute(settings.offAction)
    }
    
}

def offHandler() {
    theswitch.off()
}

def routineChanged(evt) {
    log.debug "routineChanged: $evt"
    log.debug "evt name: ${evt.name}"
    log.debug "evt value: ${evt.value}"
    log.debug "evt displayName: ${evt.displayName}"
    log.debug "evt descriptionText: ${evt.descriptionText}"
}