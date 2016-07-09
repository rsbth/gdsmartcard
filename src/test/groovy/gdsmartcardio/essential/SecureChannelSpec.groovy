package gdsmartcardio.essential

import gdsmartcard.globalplatform.SecureCardChannel
import gdsmartcardio.GlobalPlatformSpecification

import javax.smartcardio.CardException

class SecureChannelSpec extends GlobalPlatformSpecification {

    def void setup() {
        connect()
    }

    def "Can open secure channel"() {
        when: "an INITIALZE UPDATE command is sent"
        SecureCardChannel secureChannel = SecureCardChannel.initializeUpdate(channel)

        then: "the card cryptogram in the response can be verified " +
        "AND the secure channel can be initiated with a EXTERNAL AUTHENTICATE command."
        secureChannel.verifyCardCryptogram()
        secureChannel.externalAuthenticate()
        notThrown(CardException)
    }
}
