package gdsmartcardio

import gdsmartcard.AID
import gdsmartcard.globalplatform.GlobalPlatformCardChannel

import java.nio.ByteBuffer

class SelectIssuerSecurityDomainSpec extends GlobalPlatformSpecification {

    def "Does the Issuer Security Domain respond to a SELECT command with the FCI"() {
        when: "the Issuer Security Domain is selected"
        ByteBuffer fciBuffer = ByteBuffer.wrap(channel.selectIssuerSecurityDomain().getBytes())

        then: "it responds with the File Control Information acc. to GlobalPlatform Card Specification 2.1.1, table 9-55"
        // the FCI is wrapped in a FCI template
        fciBuffer.get() == 0x6F as byte
        fciBuffer.get() == fciBuffer.remaining()

        // the AID is contained
        fciBuffer.get() == 0x84 as byte
        byte[] aidBytes = new byte[fciBuffer.get()]
        fciBuffer.get(aidBytes)
        new AID(aidBytes).isValid()

        // proprietary data is contained
        fciBuffer.get() == 0xA5 as byte
        fciBuffer.get() == fciBuffer.remaining()
    }
}
