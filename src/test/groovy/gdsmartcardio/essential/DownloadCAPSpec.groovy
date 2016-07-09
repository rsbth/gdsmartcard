package gdsmartcardio.essential

import gdsmartcard.AID
import gdsmartcard.JCPackage
import gdsmartcardio.GlobalPlatformSpecification

class DownloadCAPSpec extends GlobalPlatformSpecification {

    def "Can download package"() {
        given: "a package is not yet present on the card"
        JCPackage jcPackage = JCPackage.fromCAP()
        AID packageAID = jcPackage.getAID()
        if (channel.isAIDPresentOnCard(jcPackage)) {
            throw new Exception("Test package already present on card.")
        }

        when: "the package is being downloaded"
        jcPackage.download(channel)

        then: "the Issuer Security Domain successfully processes all commands"
        noExceptionThrown()
    }
}
