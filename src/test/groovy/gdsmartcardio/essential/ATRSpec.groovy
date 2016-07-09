package gdsmartcardio.essential

import gdsmartcardio.GlobalPlatformSpecification

import javax.smartcardio.Card

class ATRSpec extends GlobalPlatformSpecification {

    def "The card sends an ATR"() {
        given: "The card is present in a terminal"
        if (!terminal.isCardPresent()) throw new Exception("No card present in terminal " + terminal.getName())

        when: "the terminal connects to the card using the default protocol"
        connect()

        then: "the card responds with an ATR"
        card.getATR()
    }

    def "A test that does not do anything"() {
        expect:
        true
    }
}
