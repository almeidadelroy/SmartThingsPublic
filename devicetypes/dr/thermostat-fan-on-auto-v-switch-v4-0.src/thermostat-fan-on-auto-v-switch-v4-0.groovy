/**
 *  ThermostatFan On_Auto V-Switch
 *
 *  Copyright 2015 Joseph Quander III
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
 *			V1.0 2015-05-15: Simple Switch to change thermostat Fan-State base on switch being off or on handlers.
 *			V2.0 2015-05-17: Changed Switch to flip state based on state of switch.
 *			V3.0 2015-05-19: Updated to have switch and SmartThings tile to match activity between them.
 *			V3.1 2015-05-19: Added Push Notification and changed to Momentary Push
 *			V4.0 2015-05-21: Changed to Switch that updates from apps and Smartthings
 *
 *
 */
definition(
    name: "Thermostat Fan On/Auto V-Switch v4.0",
    namespace: "DR",
    author: "DarcRanger",
    description: "Switch used primarily with dashboards, like SmartTiles.  This fills the void of not having control of the Thermostat Fan from the tile.  Using Virtual Switch to turn the thermostat fan from on to auto and back.",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: true)


preferences {

	section("Select Virtual Switch to change between Thermostat-Fan States [ON/AUTO]...") {
	 input "master", "capability.switch", multiple: false, title: "Switch button is trigged...", required: true
	}
    section("Choose thermostat(s)") {
     input "thermostat", "capability.thermostat", multiple: true
        }
    section("Notify me...") {
     input "pushNotification", "bool", title: "Push notification", required: false, defaultValue: "true"
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

def initialize()  {
   
  
    subscribe(master, "switch.on", "onSwitchHandler")
    subscribe(master, "switch.off", "offSwitchHandler")
    
    subscribe(thermostat, "thermostatFanMode.fanOn", thermostatOnHandler)
    subscribe(thermostat, "thermostatFanMode.fanAuto", thermostatAutoHandler)
}

import groovy.time.TimeCategory
def onSwitchHandler(evt) {
       // log.debug ""
     log.debug "S-ON-------------------------------------------"
	
   log.info "onSwitchHandler Event Value: ${evt.value}" //  which event fired is here
   log.info "onSwitchHandler Event Name: ${evt.name}"   //  name of device firing it here
    def MasterV = master.currentValue("switch")
    def Thermofan = thermostat.currentValue("thermostatFanMode")
 
		/*log.debug "Thermofan-Status: $Thermofan"*/ 

        if (MasterV =="on" ){
            thermostat.setThermostatFanMode("on")  
            
            }else {
            log.debug "problem" + Thermofan
            }
	}
    def offSwitchHandler(evt) {
     log.debug "S-OFF---------------------------------------------"

   log.info "offSwitchHandler Event Value: ${evt.value}" //  which event fired is here
   log.info "offSwitchHandler Event Name: ${evt.name}"   //  name of device firing it here
    def MasterV = master.currentValue("switch")
    def Thermofan = thermostat.currentValue("thermostatFanMode")

         if (MasterV =="off" ){
            thermostat.setThermostatFanMode("auto")
            
            }else {
            log.debug "problem" + Thermofan
            }  
	}
     
def thermostatOnHandler(evt) {

        log.debug ""
       log.debug "------T-ON-----"
       //log.debug "$evt.value"
   master.on()
   
   log.info "FanHandler Event Value-ON: ${evt.value}" //  which event fired is here
   log.info "FanHandler Event Name: ${evt.name}"   //  name of device firing it here
	 Notification()
   }
   
         
def thermostatAutoHandler(evt) {

        log.debug ""
        log.debug "------T-OFF-----"
        //log.debug "$evt.value"
	master.off()
    
   log.info "FanHandler Event Value-Auto: ${evt.value}" //  which event fired is here
   log.info "FanHandler Event Name: ${evt.name}"   //  name of device firing it here
	Notification()
        }
        
 def Notification(){
 log.debug "test Thermofan-Status: " + thermostat.currentValue("thermostatFanMode")
          if (pushNotification) {
            log.debug "Notify Thermofan-Status: " + thermostat.currentValue("thermostatFanMode")
            sendPush("Thermofan-Status: " + thermostat.currentValue("thermostatFanMode"))
        	}
            }