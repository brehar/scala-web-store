package controllers

import org.scalatestplus.play.{ HtmlUnitFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec }
import utils.DBCleaner

class ReviewControllerTestSpec
    extends PlaySpec
    with OneServerPerSuite
    with OneBrowserPerSuite
    with HtmlUnitFactory {
  "ReviewController" should {
    DBCleaner.cleanUp()

    "insert a new review should be ok" in {
      goTo(s"http://localhost:$port/product/add")
      click on id("name")
      enter("Blue Ball")
      click on id("details")
      enter("Blue Ball is an Awesome and Simple Product")
      click on id("price")
      enter("17.55")
      submit()

      goTo(s"http://localhost:$port/review/add")
      singleSel("productId").value = "1"
      click on id("author")
      enter("diegopacheco")
      click on id("comment")
      enter("Tests are amazing!")
      submit()
    }

    "details from the review 1 should be ok" in {
      goTo(s"http://localhost:$port/review/details/1")
      textField("author").value mustBe "diegopacheco"
      textField("comment").value mustBe "Tests are amazing!"
    }

    "update review 1 should be ok" in {
      goTo(s"http://localhost:$port/review/details/1")
      textField("author").value = "diegopacheco2"
      textField("comment").value = "Tests are amazing 2!"
      submit()

      goTo(s"http://localhost:$port/review/details/1")
      textField("author").value mustBe "diegopacheco2"
      textField("comment").value mustBe "Tests are amazing 2!"
    }

    "delete a review should be ok" in {
      goTo(s"http://localhost:$port/review")
      click on id("btnDelete")
    }

    "clean up db in the end" in {
      DBCleaner.cleanUp()
    }
  }
}
