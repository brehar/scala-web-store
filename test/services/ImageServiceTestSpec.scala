package services

import mocks.ImageMockedDao
import models.Image
import org.scalatestplus.play.PlaySpec
import utils.Awaits

class ImageServiceTestSpec extends PlaySpec {
  "ImageService" must {
    val service: ImageService = new ImageServiceImpl(new ImageMockedDao)

    "insert an image properly" in {
      val image = Image(Some(1), Some(1), "http://www.google.com/myimage")
      service.insert(image)
    }

    "update an image" in {
      val image = Image(Some(1), Some(2), "http://www.google.com/myimage")
      service.update(1, image)
    }

    "not update because does not exist" in {
      intercept[RuntimeException] {
        Awaits.get(5, service.update(333, null))
      }
    }

    "find the image 1" in {
      val image = Awaits.get(5, service.findById(1))
      image.get.id mustBe Some(1)
      image.get.productId mustBe Some(2)
      image.get.url mustBe "http://www.google.com/myimage"
    }

    "find all" in {
      val images = Awaits.get(5, service.findAll())
      images.get.length mustBe 1
      images.get.head.id mustBe Some(1)
      images.get.head.productId mustBe Some(2)
      images.get.head.url mustBe "http://www.google.com/myimage"
    }

    "remove 1 image" in {
      val image = Awaits.get(5, service.remove(1))
      image mustBe 1

      val oldImage = Awaits.get(5, service.findById(1))
      oldImage mustBe None
    }

    "not remove because does not exist" in {
      intercept[RuntimeException] {
        Awaits.get(5, service.remove(-1))
      }
    }
  }
}
