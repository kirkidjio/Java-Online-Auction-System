package io.etorg.microservice.notifications.events

data class UserConfirmRegistrationEvent (val email:String, val token: String) {

}