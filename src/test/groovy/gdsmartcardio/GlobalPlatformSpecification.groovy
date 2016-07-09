package gdsmartcardio;

import gdsmartcard.globalplatform.GlobalPlatformCardChannel

import javax.smartcardio.Card
import javax.smartcardio.CardTerminal
import javax.smartcardio.TerminalFactory
import javax.swing.JComboBox
import javax.swing.JOptionPane

import spock.lang.Specification

public class GlobalPlatformSpecification extends Specification {

    CardTerminal terminal
    Card card
    GlobalPlatformCardChannel channel

    /**
     * Prepares the specification. I.e. initialization of the terminal.
     */
    def void setup() {
        // When a terminal has been specified in the system properties, we assume unattended mode and just use it.
        // Otherwise we assume interactive mode and let the user select the terminal.
        if (System.properties.'test.terminal') {
            terminal = TerminalFactory.getDefault().terminals().getTerminal(System.properties.'test.terminal')
            if (!terminal) throw new Exception("Terminal not found: " + System.properties.'test.terminal')
        } else {
            JComboBox terminalComboBox = new JComboBox()
            TerminalFactory.getDefault().terminals().list().each { terminalComboBox.addItem(it) }
            if (JOptionPane.showConfirmDialog(null, terminalComboBox, "Select terminal", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                throw new Exception("Terminal selection aborted")
            }
            terminal = terminalComboBox.getSelectedItem()
        }
    }

    /**
     * Initializes the test card and channel by connecting to the terminal.
     */
    def void connect() {
        if (System.properties.'test.protocol') {
            card = terminal.connect(System.properties.'test.protocol')
        } else {
            card = terminal.connect("*")
        }
        println card
        channel = new GlobalPlatformCardChannel(card.basicChannel)
        print(card.ATR)
        String atr = "["
        card.ATR.bytes.each { atr += String.format("%02X ", it) }
        printf(" %s]%n", atr.trim())
    }
}
