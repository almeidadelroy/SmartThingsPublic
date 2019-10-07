/*
 * -----------------------
 * --- DEVICE HANDLER ----
 * -----------------------
 *
 * STOP:  Do NOT PUBLISH the code to GitHub, it is a VIOLATION of the license terms.
 * You are NOT allowed share, distribute, reuse or publicly host (e.g. GITHUB) the code. Refer to the license details on our website.
 *
 */

/* **DISCLAIMER**
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * Without limitation of the foregoing, Contributors/Regents expressly does not warrant that:
 * 1. the software will meet your requirements or expectations;
 * 2. the software or the software content will be free of bugs, errors, viruses or other defects;
 * 3. any results, output, or data provided through or generated by the software will be accurate, up-to-date, complete or reliable;
 * 4. the software will be compatible with third party software;
 * 5. any errors in the software will be corrected.
 * The user assumes all responsibility for selecting the software and for the results obtained from the use of the software. The user shall bear the entire risk as to the quality and the performance of the software.
 */ 
 
def clientVersion() {
    return "02.04.03"
}

/**
 * Monoprice and Linear/GoControl WAPIRZ-1 Motion and Temperature Sensor
 * 
 * http://www.monoprice.com/product?p_id=10796
 * http://www.amazon.com/GoControl-Z-Wave-PIR-Motion-Detector/dp/B00MOF3EU2
 *
 * Copyright RBoy Apps, redistribution or reuse of code is not allowed without permission
 * Change log:
 * 2018-4-19 - (v02.04.03) Patch for firmware low temperature correction at sub 32F
 * 2018-4-11 - (v02.04.02) Patch for firmware low temperature correction at 32F
 * 2017-10-18 - (v02.04.01) Update tile layout with ST mobile app release 2.8.0
 * 2017-8-7 - (v02.04.00) Added tamper alert and updated to latest specs from Monoprice, improved sensor reporting after initial pairing/configuration change
 * 2017-8-1 - (v02.03.00) Added ability for users to select the default sensor for the Things summary page
 * 2017-5-4 - (v02.02.08) Updated color scheme to match ST UX recommendations
 * 2017-1-19 - (v02.02.07) Updated color scheme to match ST UX recommendations
 * 2017-1-19 - (v2.2.6) Added ability to change icons and added option for showing motion sensor in main page
 * 2016-10-31 - Fixed error when pressing request update button and added ability to report device version for update checks
 * 2016-8-27 - Fixed v2 hub fingerprint and improved joining process for plus devices
 * 2016-8-23 - Updated battery icon
 * 2016-8-21 - Update sensors everytime the device wakes up (fix for manual poll) and fix for MSR reading
 * 2016-8-21 - Minor bug fix to check the PIR sensititivity inputs
 * 2016-8-21 - Code clean up, added support for future Z-Wave plus/secure devices, added support for manual poll interval for Z-Wave plus devices
 * 2016-8-20 - Added support to explicit temperature sensor query on a wake up
 * 2016-8-18 - Added support for setting the temperature units and reporting threshold automatically for Z-Wave Plus devices
 * 2016-8-10 - Removed capability refresh because there is no refresh instead it's configure
 * 2016-8-9 - Added support for Z-Wave Plus Monoprice Motion/Temp sensor
 * 2016-8-9 - Added support to read temp and battery when cover is opened
 * 2016-6-17 - Bugfix for invalid temp offset
 * 2016-6-15 - Added offset for manual temperature correction
 * 2016-4-20 - Added DH version in setup page
 * 2016-3-21 - The temperature in the default widget shown in the list of device things
 * 2016-3-15 - Fixed updated function to refresh after updating parameters
 * 2016-2-6 - Added support for manual polling and improved battery life by settings configuration only once per refresh request
 * 2016-2-2 - According to updated documentation from manufacturer, it supports SensorMultiLevel v4
 * 2016-2-2 - Added support for Linear/GoControl WAPIRZ-1 Motion Sensor
 * 2016-2-2 - Add initialization on settings update
 * 2016-2-1 - Fixed a bug in the base ST code causing Phantom motion events after an inactive event
 * 2016-1-30 - Force state change while reporting active and inactive motion to avoid ST platform phantom events
 * 2016-1-30 - WakeUpInformation is V2 and MultiSensor is V2, optimized response for wakeupcommand
 * 2016-1-30 - Added .format() to some Z-Wave calls to ensure compatibility
 * 2016-1-27 - Fixed a crash error while printing a debug message
 * 2016-1-24 - Debug code
 * 2016-1-23 - Initial release
 *
 *  Copyright 2014 SmartThings
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
 *
 */

preferences {
    input title: "", description: "Monoprice Motion and Temperature Sensor Device Handler v${clientVersion()}", displayDuringSetup: false, type: "paragraph", element: "paragraph"
	input title: "", description: "OPEN AND CLOSE THE SENSOR COVER AFTER CONFIGURING THESE OPTIONS FOR THEM TO TAKE EFFECT IMMEDIATELY.", displayDuringSetup: false, type: "paragraph", element: "paragraph"
	input title: "", description: "Select the Sensor to show on the 'Things' page", displayDuringSetup: true, type: "paragraph", element: "paragraph"
    input "displaySensor", "enum", title: "Primary Sensor", displayDuringSetup: true, options: ["Motion", "Temperature"]
	input title: "", description: "Inactive timeout is the number of minutes since the last motion was detected after which the sensor will report no motion. Change only if required. Leave it blank to use the default inactivity timeout (3 minutes).", displayDuringSetup: false, type: "paragraph", element: "paragraph" //http://www.pepper1.net/zwavedb/device/197 (1 to 127 seconds)
    input "noMotionTimeout", "number", title: "Inactive timeout (minutes)", displayDuringSetup: false, range: "1..254"
	input title: "", description: "Temperature correction offset is a +ve or -ve number to correct the temperature reported by the sensor", displayDuringSetup: false, type: "paragraph", element: "paragraph"
    input "tempOffset", "number", title: "Temperature correction offset", displayDuringSetup: false, range: "*..*"
	input title: "", description: "Set the motion detection sensitivity level. THIS ONLY APPLIES TO Z-WAVE PLUS DEVICES\n1 is MOST sensitive and 7 is LEAST sensitive (default is 4)", displayDuringSetup: false, type: "paragraph", element: "paragraph"
    input "pirSensitivity", "number", title: "Motion Sensitivity", displayDuringSetup: false, range: "1..7"
	input title: "", description: "If you want to force a manual poll of the temperature/motion/battery status, enter the polling interval in seconds in increments of 180 seconds (minimum 600, maximum 604800, in intervals of 200 seconds). Leave it blank to disable polling\nNOTE: Polling will reduce battery life", displayDuringSetup: false, type: "paragraph", element: "paragraph"
    input "manualPollInterval", "number", title: "Manual Poll Interval (seconds)", displayDuringSetup: false, range: "600..604800"
}

metadata {
    definition (name:"Monoprice Motion and Temperature Sensor", namespace:"rboy", author: "RBoy Apps") {
		capability "Configuration"
        capability "Sensor"
        capability "Motion Sensor"
        capability "Temperature Measurement"
        capability "Battery"
        capability "Tamper Alert"
        
        attribute "codeVersion", "string"
        attribute "dhName", "string"
        attribute "display", "string"

        fingerprint deviceId:"0x2001", inClusters:"0x71, 0x85, 0x80, 0x72, 0x30, 0x86, 0x31, 0x70, 0x84", manufacturer: "Monoprice", model: "10796" // Z-Wave motion sensor
        fingerprint deviceId:"0x2001", inClusters:"0x30, 0x31, 0x80, 0x84, 0x70, 0x85, 0x72, 0x86", manufacturer: "Linear", model: "WAPIRZ-1"
        fingerprint deviceId:"0x0701", inClusters:"0x5E, 0x98, 0x86, 0x72, 0x5A, 0x85, 0x59, 0x73, 0x80, 0x71, 0x31, 0x70, 0x84, 0x7A", manufacturer: "Monoprice", model: "15271" // Z-Wave Plus Motion sensor
        
        // New fingerprint format (MSR ==> mfr-prod-model)
        fingerprint type:"2001", cc:"30,31,80,84,70,85,72,86", deviceJoinName:"Linear WAPIRZ-1"
        fingerprint type:"2001", cc:"71,85,80,72,30,86,31,70,84", mfr: "0109", prod: "2002", model: "0203", deviceJoinName:"Monoprice Z-Wave Motion Sensor and Temp (10796)"
        fingerprint type:"0701", cc:"5E,98", sec:"86,72,5A,85,59,73,80,71,31,70,84,7A", mfr: "0109", prod: "2002", model: "0205", deviceJoinName:"Monoprice Z-Wave Plus Motion Sensor and Temp (15271)"
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"summary", type: "generic", width: 6, height: 4, canChangeIcon: true) {
            tileAttribute ("device.motion", key: "PRIMARY_CONTROL") {
                attributeState "active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#00a0dc"
                attributeState "inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff"
            }
            tileAttribute ("device.temperature", key: "SECONDARY_CONTROL") {
				attributeState "temperature", label:'${currentValue}°'
            }
        }
		valueTile("temperature", "device.temperature", width: 4, height: 4) {
			state("temperature", label:'${currentValue}°',
                  backgroundColors:[
                      // Celsius
                      [value: 0, color: "#153591"],
                      [value: 7, color: "#1e9cbb"],
                      [value: 15, color: "#90d2a7"],
                      [value: 23, color: "#44b621"],
                      [value: 28, color: "#f1d801"],
                      [value: 35, color: "#d04e00"],
                      [value: 37, color: "#bc2323"],
                      // Fahrenheit
                      [value: 40, color: "#153591"],
                      [value: 44, color: "#1e9cbb"],
                      [value: 59, color: "#90d2a7"],
                      [value: 74, color: "#44b621"],
                      [value: 84, color: "#f1d801"],
                      [value: 95, color: "#d04e00"],
                      [value: 96, color: "#bc2323"]
                  ]
			)
		}
		valueTile("battery", "device.battery", width: 2, height: 2, inactiveLabel: false) {
            state "battery", label:'${currentValue}%', unit: "", icon: "http://smartthings.rboyapps.com/images/battery.png",
                backgroundColors:[
                    [value: 15, color: "#ff0000"],
                    [value: 30, color: "#fd4e3a"],
                    [value: 50, color: "#fda63a"],
                    [value: 60, color: "#fdeb3a"],
                    [value: 75, color: "#d4fd3a"],
                    [value: 90, color: "#7cfd3a"],
                    [value: 99, color: "#55fd3a"]
                ]
        }
        standardTile("motion", "device.motion", width: 2, height: 2, inactiveLabel: false) {
            state "active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#00a0dc"
            state "inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff"
        }
		standardTile("tamper", "device.tamper", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "clear", label:'Request update', action:"configure", backgroundColor:"#FFFFFF", defaultState: true
			state "detected", label:'TAMPER', backgroundColor:"#e86d13"
		}
        standardTile("configure", "device.configure", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:'Request update', action:"configure"
		}
        standardTile("display", "device.display", width: 2, height: 2, inactiveLabel: false) {
            state "default", label:'${currentValue}', defaultState: true
            state "active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#00a0dc"
            state "inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff"
        }
        
        main "display"
        details(["summary", "temperature", "battery", "tamper"])
    }
}


def updated() {
	log.trace "Update called settings: $settings"
	try {
		if (!state.init) {
			state.init = true
		}
        configure() // Get the updates
	} catch (e) {
		log.warn "updated() threw $e"
	}
}

def parse(String description) {
	//log.trace "$description"
    
    sendEvent([name: "codeVersion", value: clientVersion()]) // Save client version for parent app
    sendEvent([name: "dhName", value: "Monoprice and WAPIRZ-1 Motion and Temperature Sensor Device Handler"]) // Save DH Name for parent app

	def result = null
	if (description.startsWith("Err")) {
	    result = createEvent(descriptionText:description)
	} else {
		def cmd = zwave.parse(description, [0x20: 1, 0x30: 1, 0x31: 5, 0x80: 1, 0x84: 2, 0x71: 3, 0x9C: 1, 0x70: 1]) // Take the highest level, Z-Wave plus is level 5 MultiSensor, it's backwards compatible with 4
		if (cmd) {
			result = zwaveEvent(cmd)
		} else {
			result = createEvent(value: description, descriptionText: description, isStateChange: false)
		}
	}
    log.debug "Parse returned ${result}"
	return result
}

def sensorValueEvent(def value) {
    if (!displaySensor || displaySensor == "Motion") { // Update summary display sensor
        sendEvent(name: "display", value: value ? "active" : "inactive")
    }

	if (value) {
		createEvent(name: "motion", value: "active", descriptionText: "$device.displayName detected motion")
	} else {
		createEvent(name: "motion", value: "inactive", descriptionText: "$device.displayName motion has stopped")
	}
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd)
{
	log.trace "$cmd"
	sensorValueEvent(cmd.value)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd)
{
	log.trace "$cmd"
	sensorValueEvent(cmd.value)
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd)
{
	log.trace "$cmd"
	sensorValueEvent(cmd.value)
}

def zwaveEvent(physicalgraph.zwave.commands.sensorbinaryv1.SensorBinaryReport cmd)
{
	log.trace "$cmd"
	sensorValueEvent(cmd.sensorValue)
}

def zwaveEvent(physicalgraph.zwave.commands.sensoralarmv1.SensorAlarmReport cmd)
{
	log.trace "$cmd"
	sensorValueEvent(cmd.sensorState)
}

def zwaveEvent(physicalgraph.zwave.commands.notificationv3.NotificationReport cmd)
{
	log.trace "NotificationReport: $cmd"
	def result = []
	if (cmd.notificationType == 0x07) {
        if ((cmd.event == 0x03)) { // Bug, eventParameter is supposed to be 0x03 but it doesn't send it
            if (state.MSR == "0109-2002-0205") { // Z-Wave Plus Motion and Temp supports tamper opening and closing, the v1 sensor doesn't support closing notifications
                result << createEvent(name: "tamper", value: "detected", descriptionText: "$device.displayName covering was removed", isStateChange: true)
            }
		} else if ((cmd.event == 0x00) && (cmd.eventParameter[0] == 0x03)) {
			result << createEvent(name: "tamper", value: "clear", descriptionText: "$device.displayName covering was closed", isStateChange: true)
		} else if (cmd.event == 0x02 || cmd.event == 0x08 || ((cmd.event == 0x00) && (cmd.eventParameter[0] == 0x08))) { // redundant - monoprice sends both notificationreport and basicset, no need to report again
			//result << sensorValueEvent(cmd.v1AlarmLevel) // Bugfix, use the v1AlarmLevel instead of assuming it's a motion active event
        }
	} else if (cmd.notificationType) {
		def text = "Notification $cmd.notificationType: event ${([cmd.event] + cmd.eventParameter).join(", ")}"
		result << createEvent(name: "notification$cmd.notificationType", value: "$cmd.event", descriptionText: text, displayed: false)
	} else {
		def value = cmd.v1AlarmLevel == 255 ? "active" : cmd.v1AlarmLevel ?: "inactive"
		result << createEvent(name: "alarm $cmd.v1AlarmType", value: value, displayed: false)
	}
	result
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv2.WakeUpNotification cmd)
{
	log.debug "Device woke up"
    
	def event = createEvent(descriptionText: "${device.displayName} woke up", isStateChange: false)
    def cmds = []
    if(!state.MSR) { // Until we have MSR we don't know what product we have, so try both
        log.debug "Getting MSR"
        cmds << zwave.manufacturerSpecificV2.manufacturerSpecificGet()
    }
    
    /*log.debug "Discovering parameters from 1 to 5 from device"
    (1..5).each { parameter ->
    	cmds << zwave.configurationV1.configurationGet(parameterNumber: parameter)
    }*/
    
    switch (state.MSR) {
        case "0109-2002-0205": // Z-Wave Plus Motion and Temp
        	log.trace "Found Monoprice Z-Wave Plus device with MSR $state.MSR"
        	break
            
        default: // Z-Wave motion and Temp
        	log.trace "Found Z-Wave device with MSR $state.MSR"
        	break
    }

    if (!state.wakeupSet) { // Configure polling
        if (manualPollInterval) { // Manual poll period set
            log.info "Setting manual poll interval to $manualPollInterval seconds"
            if (!state.security) { // NOTE: ST Platform has a bug, cannot query wake up parameters through secure command right now
                cmds << zwave.wakeUpV2.wakeUpIntervalCapabilitiesGet() // Get Wake up interval capabilities
            }
            cmds << zwave.wakeUpV1.wakeUpIntervalSet(seconds:manualPollInterval, nodeid:zwaveHubNodeId) // Set the wake up interval
            if (!state.security) { // NOTE: ST Platform has a bug, cannot query wake up parameters through secure command right now
                cmds << zwave.wakeUpV1.wakeUpIntervalGet() // Check our current wake up interval
            }
        } else {
            log.info "No manual poll, setting default wake up 4 hours"
            cmds << zwave.wakeUpV1.wakeUpIntervalSet(seconds:4*3600, nodeid:zwaveHubNodeId) // Default: Set the wake up interval to every 4 hours
            if (!state.security) { // NOTE: ST Platform has a bug, cannot query wake up parameters through secure command right now
                cmds << zwave.wakeUpV1.wakeUpIntervalGet() // Check our current wake up interval
            }
        }
        state.wakeupSet = true // We're done, don't update again unless requested
    }

    if (!state.configSet) { // Set configuration
        if (noMotionTimeout && (noMotionTimeout > 0 && noMotionTimeout < 255)) {
            log.info "Updating Inactivity timeout to $noMotionTimeout minutes"
            cmds << zwave.configurationV1.configurationSet(parameterNumber: 1, configurationValue: [noMotionTimeout]) // Set inactivity timeout in minutes
            cmds << zwave.configurationV1.configurationGet(parameterNumber: 1) // Confirm inactivity timeout
        } else {
            log.info "Settings inactivity timeout to default"
            cmds << zwave.configurationV1.configurationSet(parameterNumber: 1, defaultValue: true) // Set inactivity timeout in minutes
            cmds << zwave.configurationV1.configurationGet(parameterNumber: 1) // Confirm inactivity timeout
        }

        if (state.MSR == "0109-2002-0205") { // Z-Wave Plus Motion and Temp supports PIR sensitivity
            if (pirSensitivity && (pirSensitivity >= 1 && pirSensitivity <= 7)) {
                log.info "Updating PIR sensitivity to $pirSensitivity"
                cmds << zwave.configurationV1.configurationSet(parameterNumber: 3, configurationValue: [pirSensitivity]) // Set PIR sensitivity
                cmds << zwave.configurationV1.configurationGet(parameterNumber: 3) // Confirm PIR sensitivity
            } else {
                log.info "Setting PIR sensitivity to default"
                cmds << zwave.configurationV1.configurationSet(parameterNumber: 3, defaultValue: true) // Set PIR sensitivity
                cmds << zwave.configurationV1.configurationGet(parameterNumber: 3) // Confirm PIR sensitivity
            }

            log.debug "Setting temperature reporting threshold to 1°"
            cmds << zwave.configurationV1.configurationSet(parameterNumber: 5, configurationValue: [1]) // Set temperature reporting interval to 1 degree (options are 1 or 2 degrees)
            //cmds << zwave.configurationV1.configurationGet(parameterNumber: 5) // TODO: this parameter cannot be read back for some reason, pending with Monoprice

            // Set temperature reporting
            if (getTemperatureScale() == "F") {
                log.debug "Setting temperature scale to F"
                cmds << zwave.configurationV1.configurationSet(parameterNumber: 2, configurationValue: [1]) // Set to F
            } else {
                log.debug "Setting temperature scale to C"
                cmds << zwave.configurationV1.configurationSet(parameterNumber: 2, configurationValue: [0]) // Set to C
            }
            cmds << zwave.configurationV1.configurationGet(parameterNumber: 2)
        }

        state.configSet = true // We're done, dont' update again unless requested
    }

    log.info "Requesting motion sensor, temperature and battery update"
    cmds << zwave.sensorBinaryV1.sensorBinaryGet() // Motion Sensor
    cmds << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 0x01) // current temperature, use the highest level it's backwards compatible
    cmds << zwave.batteryV1.batteryGet() // Battery level
    
	cmds << zwave.wakeUpV1.wakeUpNoMoreInformation() // All DONE
    
    //log.trace "Sending: $cmds"
    
    [event, getResponses(cmds)]
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
	log.trace "BatteryReport: $cmd"
    
	def map = [ name: "battery", unit: "%" ]
	if (cmd.batteryLevel == 0xFF) {
		map.value = 1
		map.descriptionText = "${device.displayName} has a low battery"
		map.isStateChange = true
	} else {
		map.value = cmd.batteryLevel
	}
	state.lastbat = now()

    return [createEvent(map)]
}

def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv5.SensorMultilevelReport cmd) // Take the highest level 5 (for Z-Wave Plus Motion) which is backwards compatible with level 4 (for Z-Wave motion sensor)
{
	log.trace "SensorMultiLevelReport: $cmd"
    
	def map = [ displayed: true, value: cmd.scaledSensorValue.toString(), isStateChange: true ] // Report each temperature report even if the value hasn't changed
	switch (cmd.sensorType) {
		case 1:
			map.name = "temperature"
			def cmdScale = cmd.scale == 1 ? "F" : "C"
            map.value = ((convertTemperatureIfNeeded(cmd.scaledSensorValue, cmdScale, cmd.precision) as float) + (tempOffset ?: 0)).round(1)
            if ((cmd.scale == 1) && (map.value < 0)) { // Between 30F and 32F it reports a negative temperature, so fix it until firmware is patched up
                log.info "Correcting for firmware issue with negative temperature reporting"
                map.value = 54 + map.value // At 31F this issue is starts
            }
            map.unit = getTemperatureScale()
            map.descriptionText = "${device.displayName} temperature is ${map.value} °${map.unit}"
            if (displaySensor == "Temperature") { // Update summary display sensor
                sendEvent(name: "display", value: map.value + "°")
            }
			break;
            
		default:
        	log.warn "Unknown Sensor report sensor type: $cmd.sensorType"
            map.name = "unknown $cmd.sensorType"
			break;
	}
	createEvent(map)
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.warn "Unhandled command: $cmd"
	createEvent(descriptionText: "$device.displayName: $cmd", displayed: false)
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	def result = []

	def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
	log.debug "msr: $msr"
	updateDataValue("MSR", msr)

	result << createEvent(descriptionText: "$device.displayName MSR: $msr", isStateChange: false)
	result
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv2.WakeUpIntervalReport cmd) {
    log.trace "WakeUpIntervalReport $cmd"
}

// WakeUpIntervalCapabilitiesReport(defaultWakeUpIntervalSeconds: 3600, maximumWakeUpIntervalSeconds: 604800, minimumWakeUpIntervalSeconds: 600, wakeUpIntervalStepSeconds: 200)
def zwaveEvent(physicalgraph.zwave.commands.wakeupv2.WakeUpIntervalCapabilitiesReport cmd) {
    log.trace "WakeUpIntervalCapabilitiesReport $cmd"
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
    //log.debug "Security cmd: $cmd"
	def encapsulatedCommand = cmd.encapsulatedCommand([0x71: 3, 0x80: 1, 0x85: 2, 0x98: 1, 0x86: 2, 0x84: 2, 0x31: 5, 0x70: 1]) // 0x71 should be level 4, but ST doesn't support level 4
	//log.trace "encapsulated: $encapsulatedCommand"
	if (encapsulatedCommand) {
        state.security = true // This is a secure communications device we are working with
		zwaveEvent(encapsulatedCommand)
	}
}

def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd) {
	log.trace "ConfigurationReport $cmd"
    def result = []
    def msg = null
    switch (cmd.parameterNumber) {
        case 1:
        	msg = "Inactivity timeout: ${cmd.configurationValue[0]} minutes"
            result << createEvent(descriptionText: "$device.displayName $msg", displayed: true, isStateChange:false)
            break
            
        case 2:
        	msg = "Temperature unit: ${cmd.configurationValue[0] ? "F" : "C"}"
            result << createEvent(descriptionText: "$device.displayName $msg", displayed: true, isStateChange:false)
            break

        case 3:
        	msg = "PIR Sensitivity: ${cmd.configurationValue[0]}"
            result << createEvent(descriptionText: "$device.displayName $msg", displayed: true, isStateChange:false)
            break

        case 4:
        	msg = "Temperature offset: ${cmd.configurationValue[0]}"
            result << createEvent(descriptionText: "$device.displayName $msg", displayed: true, isStateChange:false)
            break
            
        case 5:
        	msg = "Temperature reporting threshold: ${cmd.configurationValue[0]}"
            result << createEvent(descriptionText: "$device.displayName $msg", displayed: true, isStateChange:false)
            break
            
        default:
            log.warn "Unknown parameter"
            break
    }
    
    log.debug msg
    result
}

private getResponses(commands, delay=1200) {
    if (state.security) {
        response(delayBetween(commands.collect{ zwave.securityV1.securityMessageEncapsulation().encapsulate(it).format() }, delay))
    } else {
        response(delayBetween(commands.collect{ it.format() }, delay))
    }
}

private getResponse(command) {
    if (state.security) {
        response(zwave.securityV1.securityMessageEncapsulation().encapsulate(command).format())
    } else {
        response(command.format())
    }
}

private secure(physicalgraph.zwave.Command cmd) {
	if (state.security) {
		zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
	} else {
		cmd.format()
	}
}

private secureSequence(commands, delay=200) {
	delayBetween(commands.collect{ secure(it) }, delay)
}

def configure() {
    // Force a refresh
    log.debug "Requested a refresh of sensors and settings at next wakeup"
    
    if (tempOffset) {
        log.trace "Temperature offset set to $tempOffset"
    }
        
    // Sanity check for polling interval
    if (manualPollInterval && ((manualPollInterval < 600) || (manualPollInterval > 604800) || (manualPollInterval % 200 != 0))) { // Range is 600 - 604800 with an interval of 200 seconds
    	log.error "Invalid Manual Poll Interval $manualPollInterval! It must be between 600 and 604800 and must be in interval of 200 seconds, NOT using Manual Polling"
    }
    
    if (noMotionTimeout && (noMotionTimeout <= 0 || noMotionTimeout >= 255)) {
    	log.error "Invalid Inactivity timeout $noMotionTimeout! It must be between 1 and 254, NOT updating inactivity timeout"
    }
    
    if (pirSensitivity && (pirSensitivity < 1 || pirSensitivity > 7)) {
    	log.error "Invalid PIR Sensitivity $pirSensitivity! It must be between 1 and 7, NOT updating PIR sensitivity"
    }

    switch(displaySensor) {
        case "Temperature":
        	sendEvent(name: "display", value: device.currentValue("temperature") + "°")
            break
            
        case "Motion":
        default:
        	sendEvent(name: "display", value: device.currentValue("motion"))
        	break
    }

    state.configSet = false // We should set the inactivity timer
    state.wakeupSet = false // We should set the wake up interval
        
    // Configure is called at inclusion so we have a SMALL window to execute some commands here, won't make any differnce when called manually
    def cmds = []

    cmds += secureSequence([
        zwave.manufacturerSpecificV2.manufacturerSpecificGet(),
        zwave.batteryV1.batteryGet(),
        zwave.sensorBinaryV1.sensorBinaryGet(), // Motion Sensor
        zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 0x01), // current temperature
    ], 500) // Small delay works since typically at inclusion the sensor is near the hub for the NIF command so there's no routing involved here

    // Don't do a no more wakeup here as the hub ends up queueing it becase it's a passive device and it interfers with the wakup command due to a delayed retry from the hub, the device will timeout by itself in 10 seconds
    //cmds << "delay 8000"
    //cmds << secure(zwave.wakeUpV1.wakeUpNoMoreInformation()) // We're done here
    
    return cmds
}

// THIS IS THE END OF THE FILE