package services

import mocks.ReviewMockedDao
import models.Review
import org.scalatestplus.play.PlaySpec
import utils.Awaits

class ReviewServiceTestSpec extends PlaySpec {
  "ReviewService" must {
    val service: ReviewService = new ReviewServiceImpl(new ReviewMockedDao)

    "insert a review properly" in {
      val review = Review(Some(1), Some(1), "diegopacheco", "Testing is cool!")
      service.insert(review)
    }

    "update a review" in {
      val review = Review(Some(1), Some(1), "diegopacheco", "Testing is so cool!")
      service.update(1, review)
    }

    "not update because does not exist" in {
      intercept[RuntimeException] {
        Awaits.get(5, service.update(333, null))
      }
    }

    "find the review 1" in {
      val review = Awaits.get(5, service.findById(1))
      review.get.id mustBe Some(1)
      review.get.productId mustBe Some(1)
      review.get.author mustBe "diegopacheco"
      review.get.comment mustBe "Testing is so cool!"
    }

    "find all" in {
      val reviews = Awaits.get(5, service.findAll())
      reviews.get.length mustBe 1
      reviews.get.head.id mustBe Some(1)
      reviews.get.head.productId mustBe Some(1)
      reviews.get.head.author mustBe "diegopacheco"
      reviews.get.head.comment mustBe "Testing is so cool!"
    }

    "remove 1 review" in {
      val review = Awaits.get(5, service.remove(1))
      review mustBe 1

      val oldReview = Awaits.get(5, service.findById(1))
      oldReview mustBe None
    }

    "not remove because does not exist" in {
      intercept[RuntimeException] {
        Awaits.get(5, service.remove(-1))
      }
    }
  }
}
