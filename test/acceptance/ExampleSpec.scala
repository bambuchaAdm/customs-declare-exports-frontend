package acceptance

import forms.Choice
import forms.declaration._
import forms.declaration.additionaldeclarationtype.AdditionalDeclarationType
import forms.declaration.destinationCountries.DestinationCountriesSupplementary
import models.declaration.{DeclarationAdditionalActorsData, DeclarationHoldersData}

class ExampleSpec extends AcceptanceSpec {

  before {

    authorizedUser()
    withCaching[Choice](Some(Choice("SMP")), "Choice")
    withCaching[DispatchLocation](Some(DispatchLocation("EX")), "DispatchLocation")
    withCaching[AdditionalDeclarationType](Some(AdditionalDeclarationType("Simplified")), "AdditionalDeclarationType")
    withCaching[ConsignmentReferences](Some(ConsignmentReferences(None, "12345")), "ConsignmentReferences")
    withCaching[ExporterDetails](Some(ExporterDetails(EntityDetails(Some("12345"), None))), "ExporterDetails")
    withCaching[ConsigneeDetails](Some(ConsigneeDetails(EntityDetails(Some("12345"), None))), "ConsigneeDetails")
    withCaching[DeclarantDetails](Some(DeclarantDetails(EntityDetails(Some("12345"), None))), "DeclarantDetails")
    withCaching[RepresentativeDetails](Some(RepresentativeDetails(EntityDetails(Some("12345"), None), "2")), "RepresentativeDetails")
    withCaching[DeclarationAdditionalActorsData](Some(DeclarationAdditionalActorsData(Seq(DeclarationAdditionalActors(Some("112233"), Some("CS"))))), "DeclarationAdditionalActorsData")
    withCaching[DeclarationHoldersData](Some(DeclarationHoldersData(Seq(DeclarationHolder(Some("8899"), Some("0099887766"))))), "DeclarationHoldersData")
    withCaching[DestinationCountriesSupplementary](None, "DestinationCountries")
  }

  "Country input" must {
    "accept: Greece and France" in {

      go to s"http://localhost:$port/customs-declare-exports/declaration/destination-countries"
      pageTitle mustBe "Countries of dispatch and destination"

      click on find(id("countryOfDispatch")).value
      enter("Greece")

      click on find(id("countryOfDestination")).value
      enter("France")

      click on find(id("submit")).value

      eventually { pageTitle mustBe "Location of goods" }
    }
  }
}
